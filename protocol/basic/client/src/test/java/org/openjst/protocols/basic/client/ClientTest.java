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

package org.openjst.protocols.basic.client;

import org.openjst.commons.protocols.auth.SecretKeys;
import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;
import org.openjst.protocols.basic.pdu.packets.RPCPacket;
import org.openjst.protocols.basic.sessions.Session;
import org.openjst.protocols.basic.utils.ProtocolBasicConstants;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sergey Grachev
 */
//@Test(groups = "manual")
public class ClientTest {

    public static final String ACCOUNT = "demo";
    public static final String CLIENT = "user";
    public static final String HOST = "localhost";

    private static int messages;
    private static int floods;
    private static int flood;
    private static long startTime;
    private static AtomicInteger received;
    private static Client client;

    private static void flood() throws ClientNotConnectedException {
        if (!client.isConnected()) {
            return;
        }

        startTime = System.currentTimeMillis();

        for (int i = 0; i < messages; i++) {
            client.sendPacket(PacketsFactory.newRPCPacket(
                    System.nanoTime(), System.currentTimeMillis(), RPCMessageFormat.XML, new byte[]{0}));
        }
    }

    public static void main(final String[] arg) throws IOException, ClientNotConnectedException {
        received = new AtomicInteger(0);
        messages = 100000;
        floods = 100;

        client = new Client(HOST, ProtocolBasicConstants.DEFAULT_PORT);

        final ClientEventsListener eventsListener = new ClientEventsListener() {
            public void onConnect(final Session session) {
                System.out.println("client connected");
//                try {
////                    client.sendPacket(new RPCPacket(
////                            System.currentTimeMillis(), System.currentTimeMillis(),
////                            "method1", "<xml>123</xml>"));
//
//                    flood();
//
//                } catch (ClientNotConnectedException e) {
//                    e.printStackTrace();
//                }
            }

            public void onAuthorizationFail(final int errorCode, final AbstractAuthPacket authRequest) {
                System.out.println("fail " + errorCode + " " + authRequest.toString());
            }

            public void onDisconnect(final Session session) {
                System.out.println("client disconnected");
            }

            public void onRPC(final Session session, final RPCPacket packet) {
//                System.out.println("client RPC " + packet.toString());
                if (received.incrementAndGet() == messages) {
                    final long stopTime = System.currentTimeMillis();
                    final float timeInSeconds = (stopTime - startTime) / 1000f;
                    System.err.println("Sent and received " + messages + " in " + timeInSeconds + "s");
                    System.err.println("That's " + (messages / timeInSeconds) + " echoes per second!");

                    // ideally, this should be sent to another thread, since this method is called by a netty worker thread.
                    if (flood < floods) {
                        received.set(0);
                        flood++;
                        try {
                            flood();
                        } catch (ClientNotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        client.addListener(eventsListener);


        final AbstractAuthPacket packet = PacketsFactory.newAuthRequestBasicPacket(
                System.nanoTime(), ACCOUNT, CLIENT + "1", SecretKeys.PLAIN.encode("password1"), null);
        if (!client.connect(packet)) {
            System.exit(-1);
            return; // not really needed...
        }

        System.out.println("Client started...");

//        flood();

//        client.sendPacket(new RPCPacket(
//                System.currentTimeMillis(), System.currentTimeMillis(),
//                "method1", "<xml>123</xml>"));


//        final List<Client> clients = new ArrayList<Client>();
//        for (int i = 0; i < 5; i++) {
//            final Client client2 = new Client(HOST, PORT);
//            client2.addListener(eventsListener);
//            if (client2.start()) {
//                clients.add(client2);
//                client2.sendPacket(new RPCPacket(
//                        System.currentTimeMillis(), System.currentTimeMillis(),
//                        "method1", "<xml>123</xml>"));
//            }
//        }

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                client.connect(PacketsFactory.newAuthRequestBasicPacket(
                        System.nanoTime(), "account1", "user1", SecretKeys.PLAIN.encode("password1"), null));
            }
        }).start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                client.disconnect();
//                for (final Client item : clients) {
//                    client.disconnect();
//                }
            }
        });
    }

}
