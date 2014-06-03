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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.openjst.commons.security.checksum.CRC16;
import org.openjst.protocols.basic.constants.ProtocolBasicConstants;
import org.openjst.protocols.basic.exceptions.CorruptedPacketException;
import org.openjst.protocols.basic.exceptions.UnsupportedProtocolVersionException;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;

public class ProtocolDecoder extends ReplayingDecoder<DecoderState> {

    private PDUHeader packet;

    public ProtocolDecoder() {
        reset();
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final DecoderState state) throws Exception {
        switch (state) {
            case VERSION:
                packet.setVersion((byte) buffer.readUnsignedByte());
                checkpoint(DecoderState.FLAGS);
            case FLAGS:
                packet.setFlags(buffer.readShort());
                checkpoint(DecoderState.TYPE);
            case TYPE:
                packet.setType(buffer.readShort());
                checkpoint(DecoderState.LENGTH);
            case LENGTH:
                packet.setLength(buffer.readInt());
                checkpoint(DecoderState.RESERVED);
            case RESERVED:
                buffer.skipBytes(5);
                checkpoint(DecoderState.CRC16);
            case CRC16:
                packet.setCRC16(buffer.readShort());
                checkpoint(DecoderState.DATA);
            case DATA:
                final byte[] data;
                if (packet.getLength() > 0) {
                    if (buffer.readableBytes() < packet.getLength()) {
                        throw new IndexOutOfBoundsException();
                    }
                    data = new byte[packet.getLength()];
                    buffer.readBytes(data, 0, data.length);
                } else {
                    data = null;
                }
                try {
                    return decode(packet, data);
                } finally {
                    this.reset();
                }
            default:
                throw new Exception("Unknown decoding state: " + state);
        }
    }

    private void reset() {
        checkpoint(DecoderState.VERSION);
        this.packet = new PDUHeader();
    }

    private static PDU decode(final PDUHeader header, final byte[] data) throws UnsupportedProtocolVersionException, CorruptedPacketException {
        if (header.getVersion() != ProtocolBasicConstants.VERSION) {
            throw new UnsupportedProtocolVersionException(header.getVersion());
        }

        final PDU packet = PacketsFactory.newOfType((byte) header.getType());

        if (data != null) {
            if (CRC16.checksum(data) != header.getCRC16()) {
                throw new CorruptedPacketException("Checksum of packet data is incorrect");
            }

            try {
                packet.decode(data);
            } catch (Exception e) {
                throw new CorruptedPacketException("Can't decode incoming packet", e);
            }
        }

        return packet;
    }
}
