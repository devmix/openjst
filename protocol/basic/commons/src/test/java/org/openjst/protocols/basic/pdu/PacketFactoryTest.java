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

package org.openjst.protocols.basic.pdu;

import org.openjst.protocols.basic.pdu.packets.*;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
@Test
public class PacketFactoryTest {
    public void test() {
        assertThat(PacketsFactory.newOfType(Packets.TYPE_AUTHENTICATION_CLIENT)).isInstanceOf(AuthClientRequestPacket.class);
        assertThat(PacketsFactory.newOfType(Packets.TYPE_AUTHENTICATION_SERVER)).isInstanceOf(AuthServerRequestPacket.class);
        assertThat(PacketsFactory.newOfType(Packets.TYPE_AUTHENTICATION_RESPONSE)).isInstanceOf(AuthResponsePacket.class);
        assertThat(PacketsFactory.newOfType(Packets.TYPE_DATETIME_SYNC)).isInstanceOf(DateTimeSyncPacket.class);
        assertThat(PacketsFactory.newOfType(Packets.TYPE_RPC)).isInstanceOf(RPCPacket.class);
    }
}
