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

package org.openjst.protocols.basic.server;

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.packets.*;
import org.openjst.protocols.basic.server.session.ServerSession;
import org.openjst.protocols.basic.sessions.Session;
import org.openjst.protocols.basic.utils.ProtocolBasicConstants;

/**
 * @author Sergey Grachev
 */
//@Test(groups = "manual")
public class ServerLoadTest {
    private static Server server;

    public static void main(final String[] arg) {
        server = new Server("localhost", ProtocolBasicConstants.DEFAULT_PORT);
        server.addListener(new ServerEventsListener() {
            public ServerSession onTryAuthenticate(final AuthRequestBasicPacket packet) {
                return new ServerSession(packet.getAccountId(), packet.getClientId());
            }

            public void onConnect(final Session session) {
                System.out.println("server: client connected " + session.toString());
            }

            public void onAuthorizationFail(final int errorCode, final AbstractAuthPacket authRequest) {
                System.out.println("fail " + errorCode + " " + authRequest.toString());
            }

            public void onDisconnect(final Session session) {
                System.out.println("server: client disconnected " + session.toString());
            }

            public void onRPC(final Session session, final RPCPacket packet) {
//                System.out.println("server: client RPC " + packet.toString());
                try {
                    server.sendPacket(session.getAccountId(), session.getClientId(), PacketsFactory.newRPCPacket(
                            1L, 1L, RPCMessageFormat.XML, new byte[]{1}));
                } catch (ClientNotConnectedException e) {
                    e.printStackTrace();
                }
            }

            public void onPresenceState(final PresenceStatePacket packet) {
                System.out.println("server: client presence " + packet.toString());
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
