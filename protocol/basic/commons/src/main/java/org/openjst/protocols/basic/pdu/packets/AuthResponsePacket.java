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

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.io.buffer.ArrayDataInputBuffer;
import org.openjst.commons.io.buffer.ArrayDataOutputBuffer;
import org.openjst.commons.io.buffer.DataBufferException;
import org.openjst.protocols.basic.pdu.Packets;
import org.openjst.protocols.basic.pdu.beans.Parameter;
import org.openjst.protocols.basic.utils.PacketUtils;

import java.util.Set;

/**
 * @author Sergey Grachev
 */
public final class AuthResponsePacket extends AbstractAuthPacket {

    private int responseStatus;

    AuthResponsePacket() {
    }

    AuthResponsePacket(final long packetId, final int responseStatus, @Nullable final Set<Parameter> parameters) {
        this.packetId = packetId;
        this.parameters = parameters;
        this.responseStatus = responseStatus;
    }

    AuthResponsePacket(final long uuid, final int responseStatus) {
        this(uuid, responseStatus, null);
    }

    public byte[] encode() throws DataBufferException {
        final ArrayDataOutputBuffer buf = new ArrayDataOutputBuffer();
        buf.writeVLQInt64(packetId);
        buf.writeVLQInt32(responseStatus);
        PacketUtils.writeParameters(buf, parameters);
        return buf.toByteArray();
    }

    public void decode(final byte[] data) throws DataBufferException {
        final ArrayDataInputBuffer buf = new ArrayDataInputBuffer(data);
        this.packetId = buf.readVLQInt64();
        this.responseStatus = buf.readVLQInt32();
        parameters = PacketUtils.readParameters(buf);
    }

    public short getType() {
        return Packets.TYPE_AUTHENTICATION_RESPONSE;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    @Override
    public String toString() {
        return "AuthResponsePacket{" +
                "responseStatus=" + responseStatus +
                "} " + super.toString();
    }
}
