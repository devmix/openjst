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

package org.openjst.server.commons.network.producer;

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.server.commons.model.types.MessageDeliveryState;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.network.*;
import org.openjst.server.commons.network.router.RouterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.util.*;

/**
 * @author Sergey Grachev
 */
public final class MessageProducerTest {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProducerTest.class);

    @DataProvider
    private static Object[][] dsClients(final ITestContext context) throws ParseException {
        return new Object[][]{new Object[]{
                new TestActor[]{
                        new TestActor(1L, "a1", "c1"),
                        new TestActor(2L, "a1", "c2"),
                        new TestActor(3L, "a2", "c1"),
                        new TestActor(4L, "a2", "c2")
                },
                new TestMessage[]{
                        new TestMessage(1L, "1".getBytes(), RPCMessageFormat.UNKNOWN),
                        new TestMessage(2L, "2".getBytes(), RPCMessageFormat.UNKNOWN)
                }
        }};
    }

    @Test(groups = "manual", dataProviderClass = MessageProducerTest.class, dataProvider = "dsClients", timeOut = 10000)
    public void priorityTest(final TestActor[] clients, final TestMessage[] messages) throws InterruptedException {
        final Map<ProtocolType, Route<Actor<Long>, Message<Long>>> routes = new HashMap<ProtocolType, Route<Actor<Long>, Message<Long>>>();
        routes.put(ProtocolType.BASIC, new Route<Actor<Long>, Message<Long>>() {
            @Override
            public DeliveryResult sendToServer(final Actor<Long> actor, final Message<Long> message) {
                LOG.info("toServer {} {}", actor, message);
                return DeliveryResult.ok();
            }

            @Override
            public DeliveryResult sendToClient(final Actor<Long> actor, final Message<Long> message) {
                LOG.info("toClient {} {}", actor, message);
                return DeliveryResult.as(MessageDeliveryState.INTERNAL_SERVER_ERROR, null);
            }
        });

        final Persistence<Actor<Long>, Message<Long>> persistence = new TestRouterPersistence(clients, messages);

        final Router<Actor<Long>, Message<Long>> clientsRouter = RouterFactory.createRouterForClients(routes);
        final MessageProducer<Persistence<Actor<Long>, Message<Long>>, Actor<Long>, Message<Long>> clientsProducer
                = MessageProducerFactory.newProducerForClients(clientsRouter, persistence, 2);

        clientsProducer.start();

        Thread.sleep(1000);
        clientsRouter.connected(clients[0], ProtocolType.BASIC);
        clientsProducer.checkMessagesFor(clients[0]);

        Thread.sleep(1000);
        clientsRouter.disconnected(clients[0], ProtocolType.BASIC);
        clientsProducer.checkMessagesFor(clients[0]);

        Thread.sleep(2000);
        clientsProducer.stop();
    }

    @SuppressWarnings("unchecked")
    public static class TestRouterPersistence implements Persistence<Actor<Long>, Message<Long>> {

        private final Actor<Long>[] recipients;
        private final Message<Long>[] messages;

        public TestRouterPersistence(final Actor<Long>[] recipients, final Message<Long>[] messages) {
            this.recipients = recipients;
            this.messages = messages;
        }

        @Override
        public List<Message<Long>> getNotDeliveredToClients(final Actor<Long> actor, final int maxSize) {
            LOG.info("getNotDeliveredToClients {} {}", actor, maxSize);
            return new LinkedList<Message<Long>>(Arrays.asList(messages));
        }

        @Override
        public List<Message<Long>> getNotDeliveredToServers(final Actor<Long> actor, final int maxSize) {
            LOG.info("getNotDeliveredToServers {} {}", actor, maxSize);
            return new LinkedList<Message<Long>>(Arrays.asList(messages));
        }

        @Override
        public void deliverySuccess(final Message<Long> message) {
            LOG.info("deliverySuccess", message);
        }

        @Override
        public void deliveryFail(final Message<Long> message, final MessageDeliveryState error, final String errorMessage) {
            LOG.info("deliveryFail {} {} {}", message, error, errorMessage);
        }

        @Override
        public Set<Actor<Long>> filterClientsWithNotDeliveredMessages(final Set<Actor<Long>> actors) {
            LOG.info("filterClientsWithNotDeliveredMessages {}", actors);
            return new LinkedHashSet<Actor<Long>>(Arrays.asList(this.recipients));
        }

        @Override
        public Set<Actor<Long>> filterServersWithNotDeliveredMessages(final Set<Actor<Long>> actors) {
            LOG.info("filterServersWithNotDeliveredMessages {}", actors);
            return new LinkedHashSet<Actor<Long>>(Arrays.asList(this.recipients));
        }
    }
}
