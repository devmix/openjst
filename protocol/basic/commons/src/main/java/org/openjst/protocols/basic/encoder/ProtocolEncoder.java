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
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.openjst.commons.io.buffer.DataBufferException;
import org.openjst.commons.security.checksum.CRC16;
import org.openjst.protocols.basic.constants.ProtocolBasicConstants;
import org.openjst.protocols.basic.pdu.PDU;

public class ProtocolEncoder extends OneToOneEncoder {
    public static final byte[] RESERVED = new byte[]{0, 0, 0, 0, 0};

    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (msg instanceof PDU) {
            return encodePacket((PDU) msg);
        } else {
            return msg;
        }
    }

    public static ChannelBuffer encodePacket(final PDU packet) throws DataBufferException {
        final byte[] msgBody = packet.encode();

        final ChannelBuffer buffer = ChannelBuffers.buffer(16 + msgBody.length);
        buffer.writeByte(ProtocolBasicConstants.VERSION);
        buffer.writeShort(0);
        buffer.writeShort(packet.getType());
        buffer.writeInt(msgBody.length);
        buffer.writeBytes(RESERVED);
        buffer.writeShort(CRC16.checksum(msgBody));
        if (msgBody.length > 0) {
            buffer.writeBytes(msgBody);
        }

        return buffer;
    }
}
