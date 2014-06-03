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
import org.openjst.commons.security.auth.SecretKey;
import org.openjst.commons.security.auth.SecretKeys;
import org.openjst.protocols.basic.pdu.Packets;
import org.openjst.protocols.basic.pdu.beans.Parameter;
import org.openjst.protocols.basic.utils.PacketUtils;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public class AuthServerRequestPacket extends AbstractAuthPacket {

    private SecretKey secretKey;

    AuthServerRequestPacket() {
    }

    AuthServerRequestPacket(final long packetId, final SecretKey secretKey, @Nullable final Set<Parameter> parameters) {
        this.parameters = parameters;
        this.packetId = packetId;
        this.secretKey = secretKey;
    }

    public byte[] encode() throws DataBufferException {
        final ArrayDataOutputBuffer buf = new ArrayDataOutputBuffer();
        buf.writeVLQInt64(packetId);
        buf.writeInt8((byte) secretKey.getType().getId());
        if (!SecretKeys.NONE.equals(secretKey.getType())) {
            buf.writeVLQInt32(secretKey.get().length);
            buf.writeBytes(secretKey.get());
        }
        PacketUtils.writeParameters(buf, parameters);
        return buf.toByteArray();
    }

    public void decode(final byte[] data) throws DataBufferException {
        final ArrayDataInputBuffer buf = new ArrayDataInputBuffer(data);
        this.packetId = buf.readVLQInt64();
        final SecretKeys secretKeyType = SecretKeys.valueById(buf.readInt8());
        if (!SecretKeys.NONE.equals(secretKeyType)) {
            final int secretKeyLength = buf.readVLQInt32();
            this.secretKey = secretKeyType.create(buf.readBytes(secretKeyLength));
        } else {
            this.secretKey = secretKeyType.create(null);
        }
        parameters = PacketUtils.readParameters(buf);
    }

    public short getType() {
        return Packets.TYPE_AUTHENTICATION_SERVER;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    @Override
    public String toString() {
        return "AuthServerRequestPacket{" +
                "} " + super.toString();
    }
}
