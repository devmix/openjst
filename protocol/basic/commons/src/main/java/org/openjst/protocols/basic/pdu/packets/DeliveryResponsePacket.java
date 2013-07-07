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
import org.openjst.protocols.basic.pdu.Packets;

/**
 * @author Sergey Grachev
 */
public final class DeliveryResponsePacket extends AbstractPacket {

    private int status;

    DeliveryResponsePacket() {
    }

    public DeliveryResponsePacket(final long packetId, final int status) {
        this.status = status;
        this.packetId = packetId;
    }

    @Override
    public short getType() {
        return Packets.TYPE_DELIVERY_RESPONSE;
    }

    public byte[] encode() throws DataBufferException {
        final ArrayDataOutputBuffer buf = new ArrayDataOutputBuffer();
        buf.writeVLQInt64(packetId);
        buf.writeVLQInt32(status);
        return buf.toByteArray();
    }

    public void decode(final byte[] data) throws DataBufferException {
        final ArrayDataInputBuffer buf = new ArrayDataInputBuffer(data);
        this.packetId = buf.readVLQInt64();
        this.status = buf.readVLQInt32();
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "DeliveryResponsePacket{" +
                "status=" + status +
                "} " + super.toString();
    }
}
