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
import org.jetbrains.annotations.Nullable;
import org.openjst.client.android.Constants;
import org.openjst.client.android.R;
import org.openjst.client.android.activity.ScheduleTodayActivity;
import org.openjst.client.android.activity.StartupActivity;
import org.openjst.client.android.commons.GlobalContext;
import org.openjst.client.android.commons.events.annotations.OnConnectionEvent;
import org.openjst.client.android.commons.events.types.ConnectionEvent;
import org.openjst.client.android.commons.inject.DefaultInjector;
import org.openjst.client.android.commons.inject.Injector;
import org.openjst.client.android.commons.inject.annotations.Inject;
import org.openjst.client.android.commons.managers.NotificationsManager;
import org.openjst.client.android.commons.managers.SessionManager;
import org.openjst.client.android.commons.managers.SettingsManager;
import org.openjst.client.android.commons.services.LocalServiceBinder;
import org.openjst.client.android.commons.utils.NotificationBuilder;
import org.openjst.client.android.managers.RPCManager;
import org.openjst.commons.protocols.auth.SecretKey;
import org.openjst.commons.protocols.auth.SecretKeys;
import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.commons.rpc.RPCRequest;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.commons.rpc.objects.RPCObjectsFactory;
import org.openjst.protocols.basic.client.Client;
import org.openjst.protocols.basic.client.ClientEventsListener;
import org.openjst.protocols.basic.events.*;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sergey Grachev
 */
public final class ServerConnectionService extends Service {

    private static final SecretKey SECRET_KEY_NONE = SecretKeys.NONE.create(new byte[0]);

    private final LocalServiceBinder<ServerConnectionService> localBinder;
    private final Injector injector = new DefaultInjector();
    private final AtomicLong requestId = new AtomicLong(0);
    private final Object lock = new Object();

    @Inject
    private NotificationsManager notifications;

    @Inject
    private SettingsManager settings;

    @Inject
    private SessionManager sessionManager;

    @Inject
    private RPCManager rpcManager;

    private final ClientEventsListener clientEventsListener;
    private final Handler handler;
    private Client client;
    private boolean updateChecked = false;
    private String accountId;
    private String clientId;
    private String secretKey;

    public ServerConnectionService() {
        GlobalContext.registerService(this);
        GlobalContext.addAndroidService(ServerConnectionService.class, this);

        this.handler = new Handler();

        this.localBinder = new LocalServiceBinder<ServerConnectionService>() {
            @Override
            public ServerConnectionService getService() {
                return ServerConnectionService.this;
            }
        };

        this.clientEventsListener = new ClientEventsListener() {

            @Override
            public boolean onAuthenticate(final ClientAuthenticationEvent event) {
                return false;
            }

            @Override
            public void onAuthenticationFail(final AuthenticationFailEvent event) {
                GlobalContext.fireEvent(OnConnectionEvent.class, new ConnectionEvent(null));
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(ServerConnectionService.this,
                                "Authorization fail " + event.getStatus() + " " + event.getAuthRequest().toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onConnect(final ConnectEvent event) {
                sessionManager.setSession(event.getSession());

                GlobalContext.fireEvent(OnConnectionEvent.class, new ConnectionEvent(event.getSession()));

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
                    client.sendRPC(RPCMessageFormat.XML, data);
                    client.sendRPC(sessionManager.getClientId(), RPCMessageFormat.XML, data);
                } catch (ClientNotConnectedException e) {
                    e.printStackTrace();
                } catch (RPCException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisconnect(final DisconnectEvent event) {
                GlobalContext.fireEvent(OnConnectionEvent.class, new ConnectionEvent(null));
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

            @Override
            public void onRPC(final RPCEvent event) {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(ServerConnectionService.this, "RPC " + event, Toast.LENGTH_SHORT).show();
                    }
                });
                rpcManager.consume(event.getFormat(), event.getData());
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        injector.apply(this);
        injector.finish();
        injector.enableEvents(this);

        sessionManager.setSession(null);

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
        injector.disableEvents(this);
        notifications.cancel(Constants.Notifications.ID_APPLICATION);
        GlobalContext.unregisterService();
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

    public boolean connect(@Nullable final String newClientId, @Nullable final String newSecretKey) {
        synchronized (lock) {
            disconnect();

            client = new Client(
                    settings.getString(Constants.Settings.SERVER_HOST),
                    settings.getInteger(Constants.Settings.SERVER_PORT));

            client.addListener(clientEventsListener);

            accountId = settings.getString(Constants.Settings.LOGIN_CLIENT_ACCOUNT);
            if (newClientId != null) {
                clientId = newClientId;
            }

            if (newSecretKey != null) {
                secretKey = newSecretKey;
            }

            try {
                return client.connect(accountId, this.clientId,
                        secretKey != null ? SecretKeys.PLAIN.encode(secretKey) : SECRET_KEY_NONE, null);
            } catch (Exception ignore) {
                return false;
            }
        }
    }

    public boolean reconnect() {
        return reconnect(null, null);
    }

    public boolean reconnect(@Nullable final String clientId, @Nullable final String secretKey) {
        disconnect();
        return connect(clientId, secretKey);
    }

    public boolean isConnected() {
        synchronized (lock) {
            return client != null && client.isConnected();
        }
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
        return accountId;
    }

    public String getClientId() {
        return clientId;
    }

    public void send(final RPCMessageFormat format, final byte[] data) throws ClientNotConnectedException {
        client.sendRPC(format, data);
    }
}
