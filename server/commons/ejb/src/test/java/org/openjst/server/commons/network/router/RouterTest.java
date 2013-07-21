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

package org.openjst.server.commons.network.router;

import org.openjst.server.commons.model.types.MessageDeliveryState;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.network.*;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sergey Grachev
 */
public final class RouterTest {

    @Test(groups = "unit")
    public void testClients() {
        final Map<ProtocolType, Route<TestActor, TestMessage>> routes = new HashMap<ProtocolType, Route<TestActor, TestMessage>>();
        @SuppressWarnings("unchecked") final Route<TestActor, TestMessage> routeBasic = mock(Route.class);
        @SuppressWarnings("unchecked") final Route<TestActor, TestMessage> routeSoap = mock(Route.class);

        final Router<TestActor, TestMessage> router = RouterFactory.createRouterForClients(routes);
        final TestActor actor1 = new TestActor(1L);
        final TestActor actor2 = new TestActor(2L);
        final TestMessage msg = new TestMessage(1L);

        // actor 1 : not connected

        assertThat(router.send(actor1, msg))
                .isEqualTo(DeliveryResult.as(MessageDeliveryState.NO_FORWARD_RECIPIENT, null));

        // actor 1 : no route

        router.connected(actor1, ProtocolType.BASIC);
        assertThat(router.send(actor1, msg))
                .isEqualTo(DeliveryResult.as(MessageDeliveryState.NO_FORWARD_RECIPIENT, null));

        // actor 1 : first call -> ok, second -> error

        routes.put(ProtocolType.BASIC, routeBasic);
        when(routeBasic.sendToClient(actor1, msg))
                .thenReturn(DeliveryResult.ok())
                .thenReturn(DeliveryResult.as(MessageDeliveryState.INTERNAL_SERVER_ERROR, null))
                .thenReturn(DeliveryResult.ok());

        assertThat(router.send(actor1, msg))
                .isEqualTo(DeliveryResult.ok());
        assertThat(router.send(actor1, msg))
                .isEqualTo(DeliveryResult.as(MessageDeliveryState.INTERNAL_SERVER_ERROR, null));

        // actor 2 : not connected

        assertThat(router.send(actor2, msg))
                .isEqualTo(DeliveryResult.as(MessageDeliveryState.NO_FORWARD_RECIPIENT, null));

        // actor 2 : no route

        router.connected(actor2, ProtocolType.SOAP);
        assertThat(router.send(actor2, msg))
                .isEqualTo(DeliveryResult.as(MessageDeliveryState.NO_FORWARD_RECIPIENT, null));

        // actor 2 : first call -> ok, second -> error

        routes.put(ProtocolType.SOAP, routeSoap);
        when(routeSoap.sendToClient(actor2, msg))
                .thenReturn(DeliveryResult.ok())
                .thenReturn(DeliveryResult.as(MessageDeliveryState.INTERNAL_SERVER_ERROR, null))
                .thenReturn(DeliveryResult.ok());

        assertThat(router.send(actor2, msg))
                .isEqualTo(DeliveryResult.ok());
        assertThat(router.send(actor2, msg))
                .isEqualTo(DeliveryResult.as(MessageDeliveryState.INTERNAL_SERVER_ERROR, null));

        // actor 1 : disconnect

        router.disconnected(actor1, ProtocolType.SOAP);
        assertThat(router.send(actor1, msg)).isEqualTo(DeliveryResult.ok());

        router.disconnected(actor1, ProtocolType.BASIC);
        assertThat(router.send(actor1, msg))
                .isEqualTo(DeliveryResult.as(MessageDeliveryState.NO_FORWARD_RECIPIENT, null));

        // actor 2 : disconnect

        router.disconnected(actor2, ProtocolType.BASIC);
        assertThat(router.send(actor2, msg)).isEqualTo(DeliveryResult.ok());

        router.disconnected(actor2, ProtocolType.SOAP);
        assertThat(router.send(actor2, msg))
                .isEqualTo(DeliveryResult.as(MessageDeliveryState.NO_FORWARD_RECIPIENT, null));

    }
}
