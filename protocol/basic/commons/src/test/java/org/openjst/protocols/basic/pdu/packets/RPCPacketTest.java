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

package org.openjst.protocols.basic.pdu.packets;

import org.fest.assertions.Assertions;
import org.openjst.commons.rpc.RPCMessageFormat;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class RPCPacketTest {

    @Test(groups = "unit")
    public void testEncodeDecode() throws IOException {
        final RPCPacket packet = PacketsFactory.newRPCPacket(
                (long) (Math.random() * Long.MAX_VALUE),
                (long) (Math.random() * Long.MAX_VALUE),
                RPCMessageFormat.XML,
                new byte[]{(byte) Math.random(), (byte) Math.random()});

        final byte[] body = packet.encode();

        final RPCPacket packet2 = new RPCPacket();
        packet2.decode(body);

        assertThat(packet2.getType()).isEqualTo(packet.getType());
        assertThat(packet2.getUUID()).isEqualTo(packet.getUUID());
        assertThat(packet2.getTimestamp()).isEqualTo(packet.getTimestamp());
        Assertions.assertThat(packet2.getFormat()).isEqualTo(packet.getFormat());
        assertThat(packet2.getData()).isEqualTo(packet.getData());
    }
}
