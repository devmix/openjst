/*
 * Copyright (C) 2013 OpenJST Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openjst.client.android.managers.impl;

import android.app.Application;
import android.os.AsyncTask;
import org.openjst.client.android.Constants;
import org.openjst.client.android.R;
import org.openjst.client.android.activity.ApplicationUpdateActivity;
import org.openjst.client.android.commons.inject.annotations.AndroidService;
import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.openjst.client.android.commons.managers.ApplicationManager;
import org.openjst.client.android.commons.managers.NotificationsManager;
import org.openjst.client.android.dao.LogsDAO;
import org.openjst.client.android.dao.SessionDAO;
import org.openjst.client.android.managers.RPCManager;
import org.openjst.client.android.service.ServerConnectionService;
import org.openjst.commons.dto.ApplicationVersion;
import org.openjst.commons.rpc.*;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.commons.rpc.objects.RPCObjectsFactory;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;
import org.openjst.protocols.basic.pdu.packets.RPCPacket;
import vendor.java.util.concurrent.BlockingDeque;
import vendor.java.util.concurrent.LinkedBlockingDeque;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sergey Grachev
 */
@JSTInject(RPCManager.class)
public class DefaultRPCManager implements RPCManager {

    public static final RPCMessageFormat RPC_MESSAGE_FORMAT = RPCMessageFormat.BINARY;

    private final RPCContext rpcContext = RPC.newInstance();
    private final AtomicLong requestId = new AtomicLong(0);
    private final BlockingDeque<RPCPacket> produceQueue = new LinkedBlockingDeque<RPCPacket>();
    private final BlockingDeque<RPCPacket> consumeQueue = new LinkedBlockingDeque<RPCPacket>();
    private final AsyncTask<Void, Void, Void> produceTask;
    private final AsyncTask<Void, Void, Void> consumeTask;
    private final Map<Long, String> requestIds = new HashMap<Long, String>();
    private final Application application;

    private ServerConnectionService connection;

    @JSTInject
    private ApplicationManager applicationManager;

    @JSTInject
    private SessionDAO sessionDAO;

    @JSTInject
    private LogsDAO logsDAO;

    @JSTInject
    private NotificationsManager notifications;

    public DefaultRPCManager(final Application application) {
        this.application = application;
        rpcContext.registerHandler(Constants.RPC.OBJECT_MOBILE, this);

        produceTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                final Thread thread = Thread.currentThread();
                while (!thread.isInterrupted() && thread.isAlive()) {
                    try {
                        if (connection == null || !connection.isConnected()) {
                            Thread.sleep(1000);
                            continue;
                        }

                        final RPCPacket packet = produceQueue.take();
                        try {
                            connection.sendPacket(packet);
                            sessionDAO.outStatus(packet.getUUID(), SessionDAO.PacketStatus.SENT);
                        } catch (ClientNotConnectedException e) {
                            produceQueue.addFirst(packet); // try later
                        } catch (Exception e) {
                            logsDAO.errorSendRPC(connection.getServerHost(), connection.getServerPort(),
                                    connection.getAccountId(), connection.getClientId(), e.getMessage());
                            sessionDAO.outStatus(packet.getUUID(), SessionDAO.PacketStatus.ERROR);
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                return null;
            }
        };
        produceTask.execute();

        consumeTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                final Thread thread = Thread.currentThread();
                while (!thread.isInterrupted() && thread.isAlive()) {
                    try {
                        final RPCPacket packet = consumeQueue.take();
                        try {
                            incoming(packet);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                return null;
            }
        };
        consumeTask.execute();
    }

    @AndroidService(ServerConnectionService.class)
    private void onBindServerConnectionService(final ServerConnectionService service) {
        connection = service;
    }

    public void checkUpdate() throws RPCException {
        outgoing(Constants.RPC.MOBILE_CHECK_UPDATE, RPCObjectsFactory.newParameters()
                .add(applicationManager.getVersion()));
    }

    @SuppressWarnings("UnusedDeclaration")
    public void checkUpdateResponse(final ApplicationVersion version) throws RPCException {
        if (sessionDAO.findVersionId(version.getMajor(), version.getMinor(), version.getBuild()) != null) {
            return;
        }

        sessionDAO.persistVersion(version);

        notifications.newActivityInvoke(Constants.Notifications.TAG_UPDATE, Constants.Notifications.ID_APPLICATION,
                String.format(application.getString(R.string.notification_new_version), version),
                String.format(application.getString(R.string.notification_click_to_start_upgrade_to_new_version), version),
                ApplicationUpdateActivity.class, version);
    }

    public void consume(final RPCPacket packet) {
        consumeQueue.add(packet);
    }

    private void outgoing(final String methodName, final RPCParameters parameters) throws RPCException {
//        try {

        final long id = requestId.incrementAndGet();
        final RPCRequest request = RPCObjectsFactory.newRequest(String.valueOf(id), Constants.RPC.OBJECT_MOBILE, methodName, parameters);
        final byte[] data = RPC_MESSAGE_FORMAT.newFormatter(true).write(request);
        final RPCPacket packet = PacketsFactory.newRPCPacket(id, System.currentTimeMillis(), RPC_MESSAGE_FORMAT, data);

        sessionDAO.outPersist(packet);
        requestIds.put(id, methodName);
        produceQueue.add(packet);

//        } catch (RPCException e) {
//            logsDAO.errorSendRPC(connection.getServerHost(), connection.getServerPort(),
//                    connection.getAccountId(), connection.getClientId(), e.getMessage());
//        }
    }

    private void incoming(final RPCPacket packet) {
        sessionDAO.inPersist(packet);

        try {
            final RPCMessage message = packet.getFormat().newFormatter(true).read(packet.getData());
            if (message instanceof RPCRequest) {

                sessionDAO.inStatus(packet.getUUID(), SessionDAO.PacketStatus.RECEIVED);
                final RPCRequest request = (RPCRequest) message;
                rpcContext.invoke(request.getObject(), request.getMethod(), request.getParameters());

            } else if (message instanceof RPCResponse) {

                sessionDAO.inResponse(packet.getUUID());

                final RPCResponse response = (RPCResponse) message;
                final String requestMethodName = requestIds.get(packet.getUUID());
                if (requestMethodName != null) {
                    rpcContext.invoke(Constants.RPC.OBJECT_MOBILE, requestMethodName + "Response", response.getParameters());
                }
                // TODO errors, responses w/o requestMethodName

            }
        } catch (RPCException e) {
            e.printStackTrace();
        }
    }
}
