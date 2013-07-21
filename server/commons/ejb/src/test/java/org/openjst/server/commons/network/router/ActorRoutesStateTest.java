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

import org.openjst.server.commons.model.types.ProtocolType;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class ActorRoutesStateTest {

    @Test(groups = "unit")
    public void testImmutable() {
        final ActorRoutesState state = new ActorRoutesState();
        assertThat(state.getConnectedRoutes()).isEqualTo(new ProtocolType[]{});

        state.connected(ProtocolType.BASIC);
        state.connected(ProtocolType.SOAP);
        assertThat(state.getConnectedRoutes()).isEqualTo(new ProtocolType[]{ProtocolType.BASIC, ProtocolType.SOAP});

        state.getConnectedRoutes()[0] = null;
        assertThat(state.getConnectedRoutes()).isEqualTo(new ProtocolType[]{ProtocolType.BASIC, ProtocolType.SOAP});
    }

    @Test(groups = "unit")
    public void testConnectAndDisconnect() {
        final ActorRoutesState state = new ActorRoutesState();
        assertThat(state.isConnected()).isFalse();

        state.connected(ProtocolType.BASIC);
        state.connected(ProtocolType.SOAP);
        assertThat(state.getConnectedRoutes()).isEqualTo(new ProtocolType[]{ProtocolType.BASIC, ProtocolType.SOAP});
        assertThat(state.isConnected()).isTrue();

        state.disconnected(ProtocolType.BASIC);
        assertThat(state.getConnectedRoutes()).isEqualTo(new ProtocolType[]{ProtocolType.SOAP});
        assertThat(state.isConnected()).isTrue();

        state.disconnected(ProtocolType.SOAP);
        assertThat(state.getConnectedRoutes()).isEqualTo(new ProtocolType[]{});
        assertThat(state.isConnected()).isFalse();
    }

    @Test(groups = "unit")
    public void testPriority() {
        final ActorRoutesState state = new ActorRoutesState();

        state.connected(ProtocolType.BASIC);
        state.connected(ProtocolType.SOAP);
        assertThat(state.getConnectedRoutes()).isEqualTo(new ProtocolType[]{ProtocolType.BASIC, ProtocolType.SOAP});

        state.disconnected(ProtocolType.BASIC);
        state.connected(ProtocolType.BASIC);
        assertThat(state.getConnectedRoutes()).isEqualTo(new ProtocolType[]{ProtocolType.SOAP, ProtocolType.BASIC});

        state.disconnected(ProtocolType.BASIC);
        state.connected(ProtocolType.BASIC);
        assertThat(state.getConnectedRoutes()).isEqualTo(new ProtocolType[]{ProtocolType.SOAP, ProtocolType.BASIC});

        state.disconnected(ProtocolType.SOAP);
        state.connected(ProtocolType.SOAP);
        assertThat(state.getConnectedRoutes()).isEqualTo(new ProtocolType[]{ProtocolType.SOAP, ProtocolType.BASIC});

        state.disconnected(ProtocolType.SOAP);
        state.connected(ProtocolType.SOAP);
        assertThat(state.getConnectedRoutes()).isEqualTo(new ProtocolType[]{ProtocolType.BASIC, ProtocolType.SOAP});
    }
}
