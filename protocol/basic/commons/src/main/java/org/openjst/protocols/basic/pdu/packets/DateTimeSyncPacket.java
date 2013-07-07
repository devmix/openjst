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
public final class DateTimeSyncPacket extends AbstractPacket {

    private long timestamp;
    private byte timeZoneOffset;

    DateTimeSyncPacket() {
    }

    DateTimeSyncPacket(final long timestamp, final byte timeZoneOffset) {
        this.timestamp = timestamp;
        this.timeZoneOffset = timeZoneOffset;
    }

    public byte[] encode() throws DataBufferException {
        final ArrayDataOutputBuffer buf = new ArrayDataOutputBuffer();
        buf.writeVLQInt64(timestamp);
        buf.writeInt8(timeZoneOffset);
        return buf.toByteArray();
    }

    public void decode(final byte[] data) throws DataBufferException {
        final ArrayDataInputBuffer buf = new ArrayDataInputBuffer(data);
        this.timestamp = buf.readVLQInt64();
        this.timeZoneOffset = buf.readInt8();
    }

    public short getType() {
        return Packets.TYPE_DATETIME_SYNC;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte getTimeZoneOffset() {
        return timeZoneOffset;
    }

    @Override
    public String toString() {
        return "DateTimeSyncPacket{" +
                "timestamp=" + timestamp +
                ", timeZoneOffset=" + timeZoneOffset +
                "} " + super.toString();
    }
}
