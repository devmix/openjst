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

package org.openjst.protocols.basic.decoder;

import org.apache.commons.lang3.ArrayUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.openjst.commons.io.buffer.DataBufferException;
import org.openjst.commons.security.checksum.CRC16;
import org.openjst.protocols.basic.constants.ProtocolBasicConstants;
import org.openjst.protocols.basic.encoder.ProtocolEncoder;
import org.openjst.protocols.basic.exceptions.CorruptedPacketException;
import org.openjst.protocols.basic.exceptions.UnsupportedProtocolVersionException;
import org.openjst.protocols.basic.pdu.packets.DateTimeSyncPacket;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;
import org.openjst.protocols.basic.utils.ByteArrayUtils;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

/**
 * @author Sergey Grachev
 */
@Test
public class ProtocolDecoderTest {
    public void testSuccess() throws Exception {
        final DateTimeSyncPacket packet = PacketsFactory.newDateTimeSyncPacket(
                (long) (Math.random() * Long.MAX_VALUE),
                (byte) (Math.random() * Byte.MAX_VALUE));

        final ChannelBuffer buffer = createChannelBuffer(packet);
        final ProtocolDecoder decoder = new ProtocolDecoder();
        final DateTimeSyncPacket packet2 = (DateTimeSyncPacket) decoder.decode(null, null, buffer, DecoderState.VERSION);

        assertThat(packet.getType()).isEqualTo(packet2.getType());
        assertThat(packet.getTimestamp()).isEqualTo(packet2.getTimestamp());
        assertThat(packet.getTimeZoneOffset()).isEqualTo(packet2.getTimeZoneOffset());
    }

    public void testFailCRC() throws Exception {
        final DateTimeSyncPacket packet = PacketsFactory.newDateTimeSyncPacket(
                (long) (Math.random() * Long.MAX_VALUE),
                (byte) (Math.random() * Byte.MAX_VALUE));

        final byte[] buf = encodePacket(packet);
        buf[14] = (byte) ~buf[14];
        final ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(buf);
        final ProtocolDecoder decoder = new ProtocolDecoder();
        try {
            //noinspection NullableProblems
            decoder.decode(null, null, buffer, DecoderState.VERSION);
            throw fail("CRC is not checked correctly");
        } catch (CorruptedPacketException e) {
            // ok
        }
    }

    public void testFailVersion() throws Exception {
        final DateTimeSyncPacket packet = PacketsFactory.newDateTimeSyncPacket(
                (long) (Math.random() * Long.MAX_VALUE),
                (byte) (Math.random() * Byte.MAX_VALUE));

        final byte[] buf = encodePacket(packet);
        buf[0] = ~ProtocolBasicConstants.VERSION;
        final ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(buf);
        final ProtocolDecoder decoder = new ProtocolDecoder();
        try {
            //noinspection NullableProblems
            decoder.decode(null, null, buffer, DecoderState.VERSION);
            throw fail("Version is not checked correctly");
        } catch (UnsupportedProtocolVersionException e) {
            // ok
        }
    }

    private ChannelBuffer createChannelBuffer(final DateTimeSyncPacket packet) throws DataBufferException {
        return ChannelBuffers.wrappedBuffer(encodePacket(packet));
    }

    private byte[] encodePacket(final DateTimeSyncPacket packet) throws DataBufferException {
        final byte[] body = packet.encode();
        byte[] buf = new byte[]{ProtocolBasicConstants.VERSION};
        buf = ArrayUtils.addAll(buf, ByteArrayUtils.fromShort((short) 0)); // flags
        buf = ArrayUtils.addAll(buf, ByteArrayUtils.fromShort(packet.getType()));
        buf = ArrayUtils.addAll(buf, ByteArrayUtils.fromInt(body.length));
        buf = ArrayUtils.addAll(buf, ProtocolEncoder.RESERVED);
        buf = ArrayUtils.addAll(buf, ByteArrayUtils.fromShort(CRC16.checksum(body)));
        buf = ArrayUtils.addAll(buf, body);
        return buf;
    }
}
