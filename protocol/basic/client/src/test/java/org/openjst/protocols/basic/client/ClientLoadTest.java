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
import org.openjst.protocols.basic.pdu.packets.AuthRequestBasicPacket;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;
import org.openjst.protocols.basic.pdu.packets.RPCPacket;
import org.openjst.protocols.basic.sessions.Session;
import org.openjst.protocols.basic.utils.ProtocolBasicConstants;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sergey Grachev
 */
//@Test
public class ClientLoadTest {
    public static final String HOST = "localhost";

    public static final int ITERATIONS = 100;
    public static final int ITERATION_MESSAGES = 100000;
    public static final int CLIENTS = 20;

    public static void main(final String[] arg) throws IOException, ClientNotConnectedException {

        final ExecutorService executorService = Executors.newCachedThreadPool();
        final LinkedList<TestClient> testClients = new LinkedList<TestClient>();

        for (int i = 0; i < CLIENTS; i++) {
            final TestClient client = new TestClient(HOST, ProtocolBasicConstants.DEFAULT_PORT, executorService);
            client.start();
            testClients.add(client);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (final TestClient client : testClients) {
                    client.disconnect();
                }
                executorService.shutdownNow();
            }
        });
    }

    public static final class TestClient {
        private static final AtomicInteger GEN_CLIENT_ID = new AtomicInteger(0);
        private static final AtomicInteger GEN_USER_ID = new AtomicInteger(0);

        private final Client client;
        private final String clientId;
        private final AtomicLong messageId = new AtomicLong(0);
        private final AtomicInteger receivedCount = new AtomicInteger(0);
        private final AtomicInteger iteration = new AtomicInteger(0);
        private final ExecutorService executorService;
        private double epsTotal;

        private long startTime;

        public TestClient(final String host, final int port, final ExecutorService executorService) {
            this.executorService = executorService;
            clientId = String.valueOf(GEN_CLIENT_ID.incrementAndGet());
            client = new Client(host, port);
            client.addListener(new ClientEventsListener() {
                public void onConnect(final Session session) {
                    System.out.println(TestClient.this.toString() + " connected");
                    try {
                        Thread.sleep(5000);
                        send();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                public void onAuthorizationFail(final int errorCode, final AbstractAuthPacket authRequest) {
                    System.err.println(TestClient.this.toString() + " auth fail");
                }

                public void onDisconnect(final Session session) {
                    System.out.println(TestClient.this.toString() + " disconnected");
                }

                public void onRPC(final Session session, final RPCPacket packet) {
//                    if (!clientId.equals(packet.getParameters())) {
//                        System.err.println(TestClient.this.toString() + " packet " + packet.toString());
//                        throw new RuntimeException("Alien response!!!");
//                    }

                    if (receivedCount.incrementAndGet() == ITERATION_MESSAGES) {
                        final long stopTime = System.currentTimeMillis();
                        final float timeInSeconds = (stopTime - startTime) / 1000f;
                        epsTotal += (ITERATION_MESSAGES / timeInSeconds);
                        System.out.println(TestClient.this.toString()
                                + ": sent and received " + ITERATION_MESSAGES + " in " + timeInSeconds + "s"
                                + ", that's " + (ITERATION_MESSAGES / timeInSeconds) + " echoes per second!"
                                + " avg " + epsTotal / (iteration.get() + 1));

                        receivedCount.set(0);

                        if (iteration.getAndIncrement() < ITERATIONS) {
                            send();
                        }
                    }
                }
            });
        }

        private void send() {
            if (!client.isConnected()) {
                System.out.println(toString() + " not connected");
                return;
            }

            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        startTime = System.currentTimeMillis();

                        for (int i = 0; i < ITERATION_MESSAGES; i++) {
                            client.sendPacket(PacketsFactory.newRPCPacket(System.nanoTime(), System.currentTimeMillis(),
                                    RPCMessageFormat.XML, new byte[]{0}));
                        }
                    } catch (ClientNotConnectedException e) {
                        e.printStackTrace();
                    }
                }
            });
//            new Thread(new Runnable() {
//                public void run() {
//                    try {
//                        startTime = System.currentTimeMillis();
//
//                        for (int i = 0; i < ITERATION_MESSAGES; i++) {
//                            client.sendPacket(new RPCPacket(
//                                    System.currentTimeMillis(), System.currentTimeMillis(),
//                                    "method-" + messageId.incrementAndGet(),
//                                    String.valueOf(clientId)));
//                        }
//                    } catch (ClientNotConnectedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
        }

        public void start() {
            epsTotal = 0;
            iteration.set(0);
            final AuthRequestBasicPacket packet = PacketsFactory.newAuthRequestBasicPacket(
                    System.nanoTime(), "account", String.valueOf(GEN_USER_ID.getAndIncrement()), SecretKeys.PLAIN.encode(""), null);
            if (!client.connect(packet)) {
                System.err.println(toString() + " fail connect");
            }
        }

        @Override
        public String toString() {
            return "Client{" +
                    "clientId=" + clientId +
                    '}';
        }

        public void disconnect() {
            if (client != null) {
                client.disconnect();
            }
        }
    }
}
