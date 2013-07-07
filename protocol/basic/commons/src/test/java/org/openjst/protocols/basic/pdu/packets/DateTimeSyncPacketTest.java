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

import org.openjst.commons.io.buffer.DataBufferException;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public class DateTimeSyncPacketTest {

    @Test(groups = "unit")
    public void encodeDecode() throws DataBufferException {
        final DateTimeSyncPacket packet = PacketsFactory.newDateTimeSyncPacket(
                (long) (Math.random() * Long.MAX_VALUE),
                (byte) (Math.random() * Byte.MAX_VALUE));

        final byte[] body = packet.encode();

        final DateTimeSyncPacket packet2 = new DateTimeSyncPacket();
        packet2.decode(body);

        assertThat(packet.getType()).isEqualTo(packet2.getType());
        assertThat(packet.getTimestamp()).isEqualTo(packet2.getTimestamp());
        assertThat(packet.getTimeZoneOffset()).isEqualTo(packet2.getTimeZoneOffset());
    }
}
