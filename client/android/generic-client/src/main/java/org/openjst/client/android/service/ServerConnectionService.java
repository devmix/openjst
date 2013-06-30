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

package org.openjst.client.android.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import org.openjst.client.android.Constants;
import org.openjst.client.android.R;
import org.openjst.client.android.activity.ScheduleTodayActivity;
import org.openjst.client.android.activity.StartupActivity;
import org.openjst.client.android.commons.ApplicationContext;
import org.openjst.client.android.commons.events.annotations.OnConnectionEvent;
import org.openjst.client.android.commons.events.types.ConnectionEvent;
import org.openjst.client.android.commons.inject.GenericInjector;
import org.openjst.client.android.commons.inject.Inject;
import org.openjst.client.android.commons.inject.Injector;
import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.openjst.client.android.commons.managers.NotificationsManager;
import org.openjst.client.android.commons.managers.SessionManager;
import org.openjst.client.android.commons.managers.SettingsManager;
import org.openjst.client.android.commons.services.LocalServiceBinder;
import org.openjst.client.android.commons.utils.NotificationBuilder;
import org.openjst.client.android.managers.RPCManager;
import org.openjst.commons.protocols.auth.SecretKeys;
import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.commons.rpc.RPCRequest;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.commons.rpc.objects.RPCObjectsFactory;
import org.openjst.protocols.basic.client.Client;
import org.openjst.protocols.basic.client.ClientEventsListener;
import org.openjst.protocols.basic.client.session.ClientSession;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;
import org.openjst.protocols.basic.pdu.packets.RPCPacket;
import org.openjst.protocols.basic.sessions.Session;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sergey Grachev
 */
public final class ServerConnectionService extends Service {

    private final LocalServiceBinder<ServerConnectionService> localBinder;
    private final Injector injector = new GenericInjector();
    private final AtomicLong requestId = new AtomicLong(0);
    private final Object lock = new Object();

    @JSTInject
    private NotificationsManager notifications;

    @JSTInject
    private SettingsManager settings;

    @JSTInject
    private SessionManager sessionManager;

    @JSTInject
    private RPCManager rpcManager;

    private final ClientEventsListener clientEventsListener;
    private final Handler handler;
    private Client client;
    private AbstractAuthPacket auth;
    private boolean updateChecked = false;

    public ServerConnectionService() {
        ApplicationContext.addAndroidService(ServerConnectionService.class, this);

        this.handler = new Handler();

        this.localBinder = new LocalServiceBinder<ServerConnectionService>() {
            @Override
            public ServerConnectionService getService() {
                return ServerConnectionService.this;
            }
        };

        this.clientEventsListener = new ClientEventsListener() {
            public void onConnect(final Session session) {
                sessionManager.setSession(session);

                ApplicationContext.fireEvent(OnConnectionEvent.class, new ConnectionEvent(session));

                if (!updateChecked) {
                    updateChecked = true;
                    try {
                        rpcManager.checkUpdate();
                    } catch (RPCException e) {
                        e.printStackTrace();
                    }
                }

                handler.post(new Runnable() {
                    public void run() {
                        notifications.show(Constants.Notifications.ID_APPLICATION,
                                new NotificationBuilder(getApplicationContext())
                                        .icon(R.drawable.ic_notification_app)
                                        .title(getString(R.string.app_name))
                                        .statusBarText("Connected").text(getString(R.string.online))
                                        .timestamp(System.currentTimeMillis())
                                        .viewIntent(PendingIntent.getActivity(ServerConnectionService.this, 0, new Intent(ServerConnectionService.this, ScheduleTodayActivity.class), 0))
                                        .build());
                    }
                });

                try {
                    final RPCRequest request = RPCObjectsFactory.newRequest("1", null, "method", null);
                    final byte[] data = RPCMessageFormat.XML.newFormatter(true).write(request);
                    client.sendPacket(PacketsFactory.newRPCPacket(
                            System.nanoTime(), System.currentTimeMillis(), RPCMessageFormat.XML, data));
                } catch (ClientNotConnectedException e) {
                    e.printStackTrace();
                } catch (RPCException e) {
                    e.printStackTrace();
                }
            }

            public void onAuthorizationFail(final int errorCode, final AbstractAuthPacket authRequest) {
                ApplicationContext.fireEvent(OnConnectionEvent.class, new ConnectionEvent(null));
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(ServerConnectionService.this, "Authorization fail " + errorCode + " " + authRequest.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void onDisconnect(final Session session) {
                ApplicationContext.fireEvent(OnConnectionEvent.class, new ConnectionEvent(null));
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(ServerConnectionService.this, "Client disconnected", Toast.LENGTH_SHORT).show();
                        notifications.show(Constants.Notifications.ID_APPLICATION,
                                new NotificationBuilder(getApplicationContext())
                                        .icon(R.drawable.ic_notification_app)
                                        .title(getString(R.string.app_name))
                                        .statusBarText("Disconnected").text(getString(R.string.offline))
                                        .timestamp(System.currentTimeMillis())
                                        .viewIntent(PendingIntent.getActivity(ServerConnectionService.this, 0, new Intent(ServerConnectionService.this, ScheduleTodayActivity.class), 0))
                                        .build());
                    }
                });
            }

            public void onRPC(final Session session, final RPCPacket packet) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(ServerConnectionService.this, "RPC " + packet, Toast.LENGTH_SHORT).show();
                    }
                });
                rpcManager.consume(packet);
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Inject.apply(this, injector);

        sessionManager.setSession(new ClientSession("<UNKNOWN>", "<UNKNOWN>"));

        startForeground(Constants.Notifications.ID_APPLICATION, new NotificationBuilder(getApplicationContext())
                .icon(R.drawable.ic_notification_app)
                .title(getString(R.string.app_name))
                .statusBarText("Started")
                .timestamp(System.currentTimeMillis())
                .viewIntent(PendingIntent.getActivity(this, 0, new Intent(this, StartupActivity.class), 0))
                .build());
    }

    @Override
    public void onStart(final Intent intent, final int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        notifications.cancel(Constants.Notifications.ID_APPLICATION);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return localBinder;
    }

    public void disconnect() {
        synchronized (lock) {
            if (client != null) {
                client.disconnect();
            }
            client = null;
        }
    }

    public boolean connect(final AbstractAuthPacket auth) {
        synchronized (lock) {
            disconnect();

            this.auth = auth;

            client = new Client(
                    settings.getString(Constants.Settings.SERVER_HOST),
                    settings.getInteger(Constants.Settings.SERVER_PORT));

            client.addListener(clientEventsListener);

            try {
                return client.connect(auth);
            } catch (Exception e) {
                return false;
            }
        }
    }

    public boolean reconnect() {
        disconnect();

        final String secretKey = settings.getString(Constants.Settings.LOGIN_CLIENT_SECRET_KEY);
        final AbstractAuthPacket packet = PacketsFactory.newAuthRequestBasicPacket(
                requestId.incrementAndGet(),
                settings.getString(Constants.Settings.LOGIN_CLIENT_ACCOUNT),
                settings.getString(Constants.Settings.LOGIN_CLIENT_ID),
                secretKey != null ? SecretKeys.PLAIN.encode(secretKey) : null,
                null);

        return connect(packet);
    }

    public boolean isConnected() {
        synchronized (lock) {
            return client != null && client.isConnected();
        }
    }

    public void sendPacket(final PDU pdu) throws ClientNotConnectedException {
        client.sendPacket(pdu);
    }

    public String getServerHost() {
        synchronized (lock) {
            return client != null ? client.getHost() : null;
        }
    }

    public Integer getServerPort() {
        synchronized (lock) {
            return client != null ? client.getPort() : null;
        }
    }

    public String getAccountId() {
        synchronized (lock) {
            return auth != null ? auth.getAccountId() : null;
        }
    }

    public String getClientId() {
        synchronized (lock) {
            return auth != null ? auth.getClientId() : null;
        }
    }
}
