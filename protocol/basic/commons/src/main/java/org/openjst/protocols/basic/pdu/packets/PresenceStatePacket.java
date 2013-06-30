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

import org.openjst.protocols.basic.pdu.PacketInputBuffer;
import org.openjst.protocols.basic.pdu.PacketOutputBuffer;
import org.openjst.protocols.basic.pdu.Packets;

import java.io.IOException;

/**
 * @author Sergey Grachev
 */
public final class PresenceStatePacket extends AbstractAuditedPacket {

    public static final byte STATE_OFFLINE = 0;
    public static final byte STATE_ONLINE = 1;

    private byte state;
    private String data;

    PresenceStatePacket() {
    }

    PresenceStatePacket(final long uuid, final long timestamp, final byte state, final String data) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.state = state;
        this.data = data;
    }

    public byte[] encode() {
        final PacketOutputBuffer buf = new PacketOutputBuffer();
        buf.writeLong(uuid);
        buf.writeLong(timestamp);
        buf.writeByte(state);
        buf.writeUTF(data);
        return buf.toByteArray();
    }

    public void decode(final byte[] data) throws IOException {
        final PacketInputBuffer buf = new PacketInputBuffer(data);
        uuid = buf.readLong();
        timestamp = buf.readLong();
        state = buf.readByte();
        this.data = buf.readUTF();
    }

    public short getType() {
        return Packets.TYPE_PRESENCE_STATE;
    }

    public byte getState() {
        return state;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "PresenceStatePacket{" +
                "state=" + state +
                ", data='" + data + '\'' +
                '}';
    }
}
