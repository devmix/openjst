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

package org.openjst.protocols.basic.test.simulator;

import org.openjst.commons.encodings.Base64;
import org.openjst.commons.protocols.auth.SecretKey;
import org.openjst.commons.protocols.auth.SecretKeys;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.protocols.basic.events.*;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.beans.Parameter;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.AuthClientRequestPacket;
import org.openjst.protocols.basic.pdu.packets.AuthServerRequestPacket;
import org.openjst.protocols.basic.server.Server;
import org.openjst.protocols.basic.server.ServerEventsListener;
import org.openjst.protocols.basic.session.ClientSession;
import org.openjst.protocols.basic.session.ServerSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public final class RouterSimulator {

    private final Server server;
    private final Map<String, Account> accounts = new HashMap<String, Account>();
    private final Map<String, String> apiKeys = new HashMap<String, String>();
    private final Map<String, SecretKey> cachedSecretKeys = new HashMap<String, SecretKey>();

    private final Map<Long, AuthClientRequestPacket> asyncAuth = new HashMap<Long, AuthClientRequestPacket>();

    public RouterSimulator(final String host, final int clientsPort, final int serversPort) {
        server = new Server(host, clientsPort, serversPort);
        server.addListener(new ServerEventsListener() {

            @Override
            public boolean onAuthenticate(final ServerAuthenticationEvent event) {
                System.out.println("RouterSimulator onAuthenticate: " + event);

                final AbstractAuthPacket packet = event.getPacket();
                if (packet instanceof AuthServerRequestPacket) {

                    final String secretKey = Base64.encodeToString(((AuthServerRequestPacket) packet).getSecretKey().get(), true);
                    if (!apiKeys.containsKey(secretKey)) {
                        return false;
                    }
                    event.assignSession(new ServerSession(apiKeys.get(secretKey), secretKey));
                    return true;

                } else if (packet instanceof AuthClientRequestPacket) {

                    final String clientId = ((AuthClientRequestPacket) packet).getClientId();
                    final String accountId = ((AuthClientRequestPacket) packet).getAccountId();

                    event.assignSession(new ClientSession(accountId, clientId));

                    final SecretKey cachedSecretKey = cachedSecretKeys.get(clientId);
                    if (cachedSecretKey != null && ((AuthClientRequestPacket) packet).getSecretKey().equals(cachedSecretKey)) {
                        System.out.println("RouterSimulator onAuthenticate: use cached " + cachedSecretKey);
                        return true;
                    } else {
                        event.forward();
                    }

                    return true;
                }

                return false;
            }

            @Override
            public void onForwardAuthenticationResponse(final ForwardAuthenticationResponseEvent event) {
                System.out.println("RouterSimulator onForwardAuthenticationResponse:" + event);
                final Set<Parameter> parameters = event.getResponse().getParameters();
                if (parameters != null && !parameters.isEmpty()) {
                    for (final Parameter parameter : parameters) {
                        if (Parameter.CACHE.equals(parameter.getKey()) && Boolean.TRUE.equals(parameter.getValue())) {
                            cachedSecretKeys.put(event.getSession().getClientId(), event.getRequest().getSecretKey());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onForwardRPC(final ForwardRPCEvent event) {
                try {
                    System.out.println("RouterSimulator onForwardRPC: " + event + "," +
                            event.getFormat().newFormatter(true).read(event.getData()));
                } catch (RPCException e) {
                    e.printStackTrace();
                }

                try {
                    server.rpcInvoke(event.getSession().getAccountId(), event.getFormat(), event.getData())
                            .await();
                } catch (ClientNotConnectedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnect(final ConnectEvent event) {
                System.out.println("RouterSimulator onConnect: " + event);
            }

            @Override
            public void onDisconnect(final DisconnectEvent event) {
                System.out.println("RouterSimulator onDisconnect: " + event);
            }

            @Override
            public void onRPC(final RPCEvent event) {
                try {
                    System.out.println("RouterSimulator onRPC: " + event + "," +
                            event.getFormat().newFormatter(true).read(event.getData()));
                } catch (RPCException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addAccountClient(final String accountId, final String clientId, final String password) {
        synchronized (accounts) {
            ensureAccountExists(accountId).clients.put(clientId, SecretKeys.PLAIN.create(password.getBytes()));
        }
    }

    public void addAccountApiKey(final String accountId, final byte[] apiKey) {
        synchronized (apiKeys) {
            apiKeys.put(Base64.encodeToString(apiKey, true), accountId);
        }
    }

    public void start() {
        server.start();
    }

    private Account ensureAccountExists(final String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            account = new Account();
            accounts.put(accountId, account);
        }
        return account;
    }

    private static final class Account {
        private final Map<String, SecretKey> clients = new HashMap<String, SecretKey>();
        private SecretKey apiKey;
    }
}
