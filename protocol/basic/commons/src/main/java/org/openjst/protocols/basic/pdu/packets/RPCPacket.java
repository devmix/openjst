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

/**
 * @author Sergey Grachev
 */
public final class RPCPacket extends AbstractAuditedPacket {

    public static final short FLAG_NO_DELIVERY_RECEIPT = 0x01;

    private String clientId;
    private RPCMessageFormat format;
    private byte[] data;
    private boolean requireDeliveryReceipt;

    RPCPacket() {
    }

    RPCPacket(final long packetId, final long timestamp, final String clientId, final RPCMessageFormat format, final byte[] data,
              final boolean requireDeliveryReceipt) {
        this.requireDeliveryReceipt = requireDeliveryReceipt;
        this.packetId = packetId;
        this.clientId = clientId;
        this.timestamp = timestamp;
        this.format = format;
        this.data = data;
    }

    public byte[] encode() throws DataBufferException {
        final ArrayDataOutputBuffer buf = new ArrayDataOutputBuffer();
        int flags = 0;
        if (!requireDeliveryReceipt) {
            flags |= FLAG_NO_DELIVERY_RECEIPT;
        }
        buf.writeVLQInt32(flags);
        buf.writeVLQInt64(packetId);
        buf.writeVLQInt64(timestamp);
        buf.writeUtf8(clientId);
        buf.writeInt8(formatToInt(format));
        buf.writeVLQInt32(data.length);
        buf.writeBytes(data);
        return buf.toByteArray();
    }

    public void decode(final byte[] data) throws DataBufferException {
        final ArrayDataInputBuffer buf = new ArrayDataInputBuffer(data);
        final int flags = buf.readVLQInt32();
        this.packetId = buf.readVLQInt64();
        this.timestamp = buf.readVLQInt64();
        this.clientId = buf.readUtf8();
        this.format = intToFormat(buf.readInt8());
        final int dataLength = buf.readVLQInt32();
        this.data = buf.readBytes(dataLength);
        requireDeliveryReceipt = (flags & FLAG_NO_DELIVERY_RECEIPT) == 0;
    }

    public short getType() {
        return Packets.TYPE_RPC;
    }

    protected RPCMessageFormat intToFormat(final byte format) {
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

    protected static byte formatToInt(final RPCMessageFormat format) {
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

    public String getClientId() {
        return clientId;
    }

    public RPCMessageFormat getFormat() {
        return format;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isRequireDeliveryReceipt() {
        return requireDeliveryReceipt;
    }

    @Override
    public String toString() {
        return "RPCPacket{" +
                "clientId='" + clientId + '\'' +
                ", format=" + format +
                "} " + super.toString();
    }

    public void setRequireDeliveryReceipt(boolean requireDeliveryReceipt) {
        this.requireDeliveryReceipt = requireDeliveryReceipt;
    }
}
