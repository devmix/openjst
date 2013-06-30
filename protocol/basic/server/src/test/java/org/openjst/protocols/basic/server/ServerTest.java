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

import org.openjst.commons.dto.ApplicationVersion;
import org.openjst.commons.rpc.*;
import org.openjst.commons.rpc.objects.RPCObjectsFactory;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.packets.*;
import org.openjst.protocols.basic.server.session.ServerSession;
import org.openjst.protocols.basic.sessions.Session;
import org.openjst.protocols.basic.utils.ProtocolBasicConstants;
import org.testng.annotations.Test;

/**
 * @author Sergey Grachev
 */
public final class ServerTest {

    public static final String ACCOUNT = "demo";
    public static final String CLIENT = "user";

    @Test(groups = "manual")
    public void testServer() {
        final Server server = new Server("localhost", ProtocolBasicConstants.DEFAULT_PORT);

        final RPCMobile rpcMobile = new RPCMobile(server);

        final RPCContext rpcContext = RPC.newInstance()
                .registerHandlerDefault(rpcMobile)
                .registerHandler("mobile", rpcMobile);

        server.addListener(new ServerEventsListener() {
            public ServerSession onTryAuthenticate(final AuthRequestBasicPacket packet) {
                if (ACCOUNT.equals(packet.getAccountId()) && CLIENT.equals(packet.getClientId())) {
                    return new ServerSession(packet.getAccountId(), packet.getClientId());
                }
                System.out.println("server: AuthRequestPacket " + packet.toString());
                return null;
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
                System.out.println("onRPC (PDU): " + packet);
                try {
                    final RPCMessage message = packet.getFormat().newFormatter(true).read(packet.getData());
                    System.out.println("onRPC: " + message);
                    if (message instanceof RPCRequest) {
                        final RPCRequest request = (RPCRequest) message;
                        final Object response = rpcContext.invoke(request.getObject(), request.getMethod(), request.getParameters());
                        server.sendPacket(session.getAccountId(), session.getClientId(), PacketsFactory.newRPCPacket(
                                packet.getUUID(), System.currentTimeMillis(), RPCMessageFormat.JSON,
                                RPCMessageFormat.JSON.newFormatter(true).write(RPCObjectsFactory
                                        .newResponse(String.valueOf(packet.getUUID()), RPCObjectsFactory.newParameters().add(response)))
                        ));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (packet.getMethodName().equals("close")) {
//                    server.disconnectClient(1, 1);
//                }
                System.out.println("server: client RPC " + packet.toString());
                try {
                    server.sendPacket(ACCOUNT, CLIENT, PacketsFactory.newRPCPacket(
                            System.nanoTime(), System.currentTimeMillis(), RPCMessageFormat.XML, new byte[]{1}));
                } catch (ClientNotConnectedException e) {
                    e.printStackTrace();
                }
            }

            public void onMessage(final PDU packet) {
                System.out.println("server: onMessage " + packet.toString());
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

        while (server.isStarted()) {
            Thread.yield();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) {
            }
        }

//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                server.stop();
//            }
//        });
    }

    public static final class RPCMobile {

        private final Server server;

        public RPCMobile(final Server server) {
            this.server = server;
        }

        public ApplicationVersion checkUpdate(final ApplicationVersion version) {
            System.out.println("BINARY-RPC: checkUpdate " + version);
            return new ApplicationVersion(System.currentTimeMillis(), 1, 2, 6, "new version 1.2.3");
        }

        public void method() {
            System.out.println("XML-RPC: default method");
        }
    }
}
