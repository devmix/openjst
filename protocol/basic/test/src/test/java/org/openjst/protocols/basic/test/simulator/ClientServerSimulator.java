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

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.commons.rpc.objects.RPCObjectsFactory;
import org.openjst.commons.security.auth.SecretKey;
import org.openjst.commons.security.auth.SecretKeys;
import org.openjst.protocols.basic.client.Client;
import org.openjst.protocols.basic.client.ClientEventsListener;
import org.openjst.protocols.basic.constants.ProtocolBasicConstants;
import org.openjst.protocols.basic.events.*;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.packets.AuthClientRequestPacket;

import java.util.Date;

/**
 * @author Sergey Grachev
 */
public final class ClientServerSimulator {

    private final Client client;
    private final SecretKey apiKey;
    private final SecretKey secretKey;

    public ClientServerSimulator(final String accountId, final String clientId, final String clientPassword,
                                 final byte[] apiKey, final String host, final int port) {

        this.apiKey = SecretKeys.PLAIN.create(apiKey);
        this.secretKey = SecretKeys.PLAIN.create(clientPassword.getBytes());

        this.client = new Client(host, port);
        this.client.addListener(new ClientEventsListener() {
            @Override
            public void onConnect(final ConnectEvent event) {
                System.out.println("ClientServerSimulator onConnect: " + event);
            }

            @Override
            public boolean onAuthenticate(final ClientAuthenticationEvent event) {
                System.out.println("ClientServerSimulator: onAuthenticate " + event);

                if (!(event.getPacket() instanceof AuthClientRequestPacket)) {
                    return false;
                }

                final AuthClientRequestPacket packet = (AuthClientRequestPacket) event.getPacket();

                if (accountId.equals(packet.getAccountId()) && secretKey.equals(packet.getSecretKey())) {
                    event.cacheCredentials(true, new Date()).addResponseParameter("test-parameter", "test-parameter-value");
                    return true;
                }

                return false;
            }

            @Override
            public void onAuthenticationFail(final AuthenticationFailEvent event) {
                System.out.println("ClientServerSimulator onAuthenticationFail: " + event);
            }

            @Override
            public void onDisconnect(final DisconnectEvent event) {
                System.out.println("ClientServerSimulator onDisconnect: " + event);
            }

            @Override
            public void onRPC(final RPCEvent event) {
                try {
                    System.out.println("ClientServerSimulator onRPC: " + event.getSession() + ", " +
                            event.getFormat().newFormatter(true).read(event.getData()));

                    if (event.getRecipientId() != null) {
                        client.sendRPC(event.getRecipientId(), RPCMessageFormat.XML, RPCMessageFormat.XML.newFormatter(true)
                                .write(RPCObjectsFactory.newRequest("3", "o789", "zxc", null)));
                    }

                } catch (RPCException e) {
                    e.printStackTrace();
                } catch (ClientNotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void connect() {
        client.connect(apiKey, null);
    }

    public static void main(final String[] args) {
        final String HOST = "localhost";
        final String ACCOUNT = "system";
        final byte[] ACCOUNT_KEY = new byte[]{1, 2, 3, 4, 5};
        final String CLIENT = "client";
        final String CLIENT_PASSWORD = "client";

        final ClientServerSimulator clientServerSimulator = new ClientServerSimulator(
                ACCOUNT, CLIENT, CLIENT_PASSWORD, ACCOUNT_KEY, HOST, ProtocolBasicConstants.DEFAULT_SERVERS_PORT);

        clientServerSimulator.connect();
    }
}
