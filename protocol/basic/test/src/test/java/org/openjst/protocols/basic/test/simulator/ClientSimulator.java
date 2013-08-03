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

import org.openjst.commons.protocols.auth.SecretKey;
import org.openjst.commons.protocols.auth.SecretKeys;
import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.commons.rpc.objects.RPCObjectsFactory;
import org.openjst.protocols.basic.client.Client;
import org.openjst.protocols.basic.client.ClientEventsListener;
import org.openjst.protocols.basic.constants.ProtocolBasicConstants;
import org.openjst.protocols.basic.events.*;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;

/**
 * @author Sergey Grachev
 */
public final class ClientSimulator {

    private final Client client;
    private final String accountId;
    private final String clientId;
    private final SecretKey secretKey;

    public ClientSimulator(final String accountId, final String clientId, final String clientPassword, final String host, final int port) {
        this.accountId = accountId;
        this.clientId = clientId;
        this.secretKey = SecretKeys.PLAIN.create(clientPassword.getBytes());

        client = new Client(host, port);
        client.addListener(new ClientEventsListener() {
            @Override
            public void onConnect(final ConnectEvent event) {
                System.out.println("ClientSimulator onConnect: " + event);

                try {
                    client.sendRPC(clientId, RPCMessageFormat.XML, RPCMessageFormat.XML.newFormatter(true)
                            .write(RPCObjectsFactory.newRequest("1", "o123", "qwe", null)));

                    client.sendRPC(RPCMessageFormat.BINARY, RPCMessageFormat.BINARY.newFormatter(true)
                            .write(RPCObjectsFactory.newRequest("2", "o456", "asd", null)));
                } catch (RPCException e) {
                    e.printStackTrace();
                } catch (ClientNotConnectedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisconnect(final DisconnectEvent event) {
                System.out.println("ClientSimulator onDisconnect: " + event);
            }

            @Override
            public void onRPC(final RPCEvent event) {
                try {
                    System.out.println("ClientSimulator onRPC: " + event + ", " +
                            event.getFormat().newFormatter(true).read(event.getData()));
                } catch (RPCException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean onAuthenticate(final ClientAuthenticationEvent packet) {
                return false;
            }

            @Override
            public void onAuthenticationFail(final AuthenticationFailEvent event) {
                System.out.println("ClientSimulator onAuthenticationFail: " + event);
            }
        });
    }

    public void connect() {
        client.connect(accountId, clientId, secretKey, null);
    }

    public void disconnect() {
        client.disconnect();
    }

    public static void main(final String[] args) {
        final ClientSimulator client = new ClientSimulator("system", "client", "client", "localhost", ProtocolBasicConstants.DEFAULT_CLIENTS_PORT);
        client.connect();
    }
}
