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

import android.os.AsyncTask;
import org.openjst.client.android.Constants;
import org.openjst.client.android.R;
import org.openjst.client.android.activity.ApplicationUpdateActivity;
import org.openjst.client.android.commons.inject.annotations.Inject;
import org.openjst.client.android.commons.inject.annotations.OnCreate;
import org.openjst.client.android.commons.inject.annotations.Singleton;
import org.openjst.client.android.commons.inject.annotations.android.AService;
import org.openjst.client.android.commons.managers.ApplicationManager;
import org.openjst.client.android.commons.managers.NotificationsManager;
import org.openjst.client.android.dao.LogsDAO;
import org.openjst.client.android.dao.PacketsDAO;
import org.openjst.client.android.dao.VersionDAO;
import org.openjst.client.android.managers.RPCManager;
import org.openjst.client.android.managers.handlers.core.UpdatesHandler;
import org.openjst.client.android.service.ServerConnectionService;
import org.openjst.commons.dto.ApplicationVersion;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.dto.tuples.Triple;
import org.openjst.commons.dto.tuples.Tuples;
import org.openjst.commons.rpc.*;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.commons.rpc.objects.RPCObjectsFactory;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import vendor.java.util.concurrent.BlockingDeque;
import vendor.java.util.concurrent.LinkedBlockingDeque;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.openjst.client.android.commons.GlobalContext.context;

/**
 * @author Sergey Grachev
 */
@Singleton(lazy = false)
public class DefaultRPCManager implements RPCManager {

    public static final RPCMessageFormat RPC_MESSAGE_FORMAT = RPCMessageFormat.BINARY;

    private final RPCContext rpcContext = RPC.newInstance();
    private final AtomicLong requestId = new AtomicLong(0);
    private final BlockingDeque<Triple<String, RPCMessageFormat, byte[]>> produceQueue = new LinkedBlockingDeque<Triple<String, RPCMessageFormat, byte[]>>();
    private final BlockingDeque<Pair<RPCMessageFormat, byte[]>> consumeQueue = new LinkedBlockingDeque<Pair<RPCMessageFormat, byte[]>>();
    private final AsyncTask<Void, Void, Void> produceTask;
    private final AsyncTask<Void, Void, Void> consumeTask;
    private final Map<String, String> requestIds = new HashMap<String, String>();

    private ServerConnectionService connection;

    @Inject
    private ApplicationManager applicationManager;

    @Inject
    private PacketsDAO packetsDAO;

    @Inject
    private LogsDAO logsDAO;

    @Inject
    private VersionDAO versionDAO;

    @Inject
    private NotificationsManager notifications;

    @Inject
    private UpdatesHandler updatesHandler;

    @OnCreate
    private void onCreate() {
        rpcContext.registerHandler(Constants.RPC.OBJECT_CORE_UPDATES, updatesHandler);
        rpcContext.registerHandler(Constants.RPC.OBJECT_MOBILE, this);

        produceTask.execute();
        consumeTask.execute();
    }

    public DefaultRPCManager() {
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

                        final Triple<String, RPCMessageFormat, byte[]> msg = produceQueue.take();
                        try {
                            connection.send(msg.second(), msg.third());
                            packetsDAO.outStatus(msg.first(), PacketsDAO.PacketStatus.SENT);
                        } catch (final ClientNotConnectedException e) {
                            produceQueue.addFirst(msg); // try later
                        } catch (final Exception e) {
                            logsDAO.errorSendRPC(connection.getServerHost(), connection.getServerPort(),
                                    connection.getAccountId(), connection.getClientId(), e.getMessage());
                            packetsDAO.outStatus(msg.first(), PacketsDAO.PacketStatus.ERROR);
                        }
                    } catch (final InterruptedException e) {
                        break;
                    }
                }
                return null;
            }
        };

        consumeTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                final Thread thread = Thread.currentThread();
                while (!thread.isInterrupted() && thread.isAlive()) {
                    try {
                        final Pair<RPCMessageFormat, byte[]> msg = consumeQueue.take();
                        try {
                            incoming(msg);
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    } catch (final InterruptedException e) {
                        break;
                    }
                }
                return null;
            }
        };
    }

    @AService(ServerConnectionService.class)
    private void onBindServerConnectionService(final ServerConnectionService service) {
        connection = service;
    }

    public void checkUpdate() throws RPCException {
        outgoing(Constants.RPC.MOBILE_CHECK_UPDATE, RPCObjectsFactory.newParameters()
                .add(applicationManager.getVersion()));
    }

    @SuppressWarnings("UnusedDeclaration")
    public void checkUpdateResponse(final ApplicationVersion version) throws RPCException {
        if (versionDAO.findVersionId(version.getMajor(), version.getMinor(), version.getBuild()) != null) {
            return;
        }

        versionDAO.add(version);

        notifications.newActivityInvoke(Constants.Notifications.TAG_UPDATE, Constants.Notifications.ID_APPLICATION,
                String.format(context().getString(R.string.notification_new_version), version),
                String.format(context().getString(R.string.notification_click_to_start_upgrade_to_new_version), version),
                ApplicationUpdateActivity.class, version);
    }

    public void consume(final RPCMessageFormat format, final byte[] data) {
        consumeQueue.add(Tuples.newPair(format, data));
    }

    private void outgoing(final String methodName, final RPCParameters parameters) throws RPCException {
//        try {

        final String id = String.valueOf(requestId.incrementAndGet());
        final RPCRequest request = RPCObjectsFactory.newRequest(id, Constants.RPC.OBJECT_MOBILE, methodName, parameters);
        final byte[] data = RPC_MESSAGE_FORMAT.newFormatter(true).write(request);
        final Triple<String, RPCMessageFormat, byte[]> msg = Tuples.newTriple(String.valueOf(id), RPC_MESSAGE_FORMAT, data);

        packetsDAO.outPersist(msg.first(), msg.second(), msg.third());
        requestIds.put(id, methodName);
        produceQueue.add(msg);

//        } catch (RPCException e) {
//            logsDAO.errorSendRPC(connection.getServerHost(), connection.getServerPort(),
//                    connection.getAccountId(), connection.getClientId(), e.getMessage());
//        }
    }

    private void incoming(final Pair<RPCMessageFormat, byte[]> msg) {

        try {
            final RPCMessage message = msg.first().newFormatter(true).read(msg.second());
            packetsDAO.inPersist(message.getId(), msg.first(), msg.second());
            if (message instanceof RPCRequest) {

                packetsDAO.inStatus(message.getId(), PacketsDAO.PacketStatus.RECEIVED);
                final RPCRequest request = (RPCRequest) message;
                rpcContext.invoke(request.getObject(), request.getMethod(), request.getParameters());

            } else if (message instanceof RPCResponse) {

                packetsDAO.inResponse(message.getId());

                final RPCResponse response = (RPCResponse) message;
                final String requestMethodName = requestIds.get(message.getId());
                if (requestMethodName != null) {
                    rpcContext.invoke(Constants.RPC.OBJECT_MOBILE, requestMethodName + "Response", response.getParameters());
                }
                // TODO errors, responses w/o requestMethodName

            }
        } catch (final RPCException e) {
            e.printStackTrace();
        }
    }
}
