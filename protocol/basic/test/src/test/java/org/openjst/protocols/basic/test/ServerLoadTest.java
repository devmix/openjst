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

package org.openjst.protocols.basic.test;

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.protocols.basic.constants.ProtocolBasicConstants;
import org.openjst.protocols.basic.events.*;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.packets.AuthClientRequestPacket;
import org.openjst.protocols.basic.server.Server;
import org.openjst.protocols.basic.server.ServerEventsListener;
import org.openjst.protocols.basic.session.ClientSession;

/**
 * @author Sergey Grachev
 */
//@Test(groups = "manual")
public class ServerLoadTest {
    private static Server server;

    public static void main(final String[] arg) {
        final Server server = new Server("localhost", ProtocolBasicConstants.DEFAULT_CLIENTS_PORT, ProtocolBasicConstants.DEFAULT_SERVERS_PORT);
        server.addListener(new ServerEventsListener() {

            @Override
            public boolean onAuthenticate(final ServerAuthenticationEvent event) {
                event.assignSession(new ClientSession(
                        ((AuthClientRequestPacket) event.getPacket()).getAccountId(),
                        ((AuthClientRequestPacket) event.getPacket()).getClientId()));
                return true;
            }

            @Override
            public void onConnect(final ConnectEvent event) {
                System.out.println("server: client connected " + event.toString());
            }

            @Override
            public void onDisconnect(final DisconnectEvent event) {
                System.out.println("server: client disconnected " + event.toString());
            }

            @Override
            public void onRPC(final RPCEvent event) {
//                System.out.println("server: client RPC " + packet.toString());
                try {
                    server.rpcForwardToClient(event.getSession().getAccountId(), event.getSession().getClientId(),
                            RPCMessageFormat.BINARY, new byte[]{1});
                } catch (ClientNotConnectedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onForwardAuthenticationResponse(final ForwardAuthenticationResponseEvent event) {

            }

            @Override
            public void onForwardRPC(final ForwardRPCEvent event) {

            }
        });

        if (!server.start()) {
            System.exit(-1);
            return; // not really needed...
        }

        System.out.println("Server started...");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.stop();
            }
        });
    }
}
