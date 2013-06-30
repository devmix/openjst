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

import org.openjst.commons.io.buffer.ArrayDataInputBuffer;
import org.openjst.commons.io.buffer.ArrayDataOutputBuffer;
import org.openjst.commons.io.buffer.DataBufferException;
import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.protocols.basic.pdu.Packets;

import java.io.IOException;

/**
 * @author Sergey Grachev
 */
public final class RPCPacket extends AbstractAuditedPacket {

    private RPCMessageFormat format;
    private byte[] data;

    RPCPacket() {
    }

    RPCPacket(final long uuid, final long timestamp, final RPCMessageFormat format, final byte[] data) {
        this.format = format;
        this.data = data;
        this.timestamp = timestamp;
        this.uuid = uuid;
    }

    public byte[] encode() {
        final ArrayDataOutputBuffer buf = new ArrayDataOutputBuffer();
        try {
            buf.writeVLQInt64(uuid);
            buf.writeVLQInt64(timestamp);
            buf.writeInt8(formatToInt(format));
            buf.writeVLQInt32(data.length);
            buf.writeBytes(data);
        } catch (DataBufferException ignore) {
            ignore.printStackTrace();
        }
        return buf.toByteArray();
    }

    public void decode(final byte[] data) throws IOException {
        final ArrayDataInputBuffer buf = new ArrayDataInputBuffer(data);
        try {
            this.uuid = buf.readVLQInt64();
            this.timestamp = buf.readVLQInt64();
            this.format = intToFormat(buf.readInt8());
            final int dataLength = buf.readVLQInt32();
            this.data = buf.readBytes(dataLength);
        } catch (DataBufferException ignore) {
            ignore.printStackTrace();
        }
    }

    public short getType() {
        return Packets.TYPE_RPC_METHOD;
    }

    private RPCMessageFormat intToFormat(final byte format) {
        switch (format) {
            case 0:
                return RPCMessageFormat.BINARY;
            case 1:
                return RPCMessageFormat.JSON;
            case 2:
                return RPCMessageFormat.XML;
        }
        return RPCMessageFormat.UNKNOWN;
    }

    private static byte formatToInt(final RPCMessageFormat format) {
        switch (format) {
            case BINARY:
                return 0;
            case JSON:
                return 1;
            case XML:
                return 2;
        }
        return -1;
    }

    public RPCMessageFormat getFormat() {
        return format;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "RPCPacket{" +
                "format=" + format +
                ", data=" + data +
                '}';
    }
}
