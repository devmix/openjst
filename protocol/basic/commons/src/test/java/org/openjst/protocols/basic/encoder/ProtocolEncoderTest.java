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

package org.openjst.protocols.basic.encoder;

import org.jboss.netty.buffer.ChannelBuffer;
import org.openjst.commons.security.checksum.CRC16;
import org.openjst.protocols.basic.constants.ProtocolBasicConstants;
import org.openjst.protocols.basic.pdu.packets.DateTimeSyncPacket;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
@Test
public class ProtocolEncoderTest {
    public void test() throws Exception {
        final ProtocolEncoder encoder = new ProtocolEncoder();
        final DateTimeSyncPacket packet = PacketsFactory.newDateTimeSyncPacket(123, (byte) 2);
        final ChannelBuffer buffer = (ChannelBuffer) encoder.encode(null, null, packet);

        int i = 0;
        final byte[] body = packet.encode();
        final byte[] buf = buffer.array();
        assertThat(buf[i++]).isEqualTo(ProtocolBasicConstants.VERSION); // version
        assertThat(buf[i++] << 8 | buf[i++]).isEqualTo(0); // flags
        assertThat(buf[i++] << 8 | buf[i++]).isEqualTo(packet.getType()); // type
        assertThat(buf[i++] << 24 | buf[i++] << 16 | buf[i++] << 8 | buf[i++]).isEqualTo(body.length); // length

        for (int j = 0; j < ProtocolEncoder.RESERVED.length; j++) {
            assertThat(buf[i + j]).isEqualTo(ProtocolEncoder.RESERVED[j]);
        }
        i += ProtocolEncoder.RESERVED.length;

        assertThat(buf[i++] << 8 | buf[i++]).isEqualTo(CRC16.checksum(body)); // checksum
        assertThat(Arrays.copyOfRange(buf, i, buf.length)).isEqualTo(body); // message
    }
}
