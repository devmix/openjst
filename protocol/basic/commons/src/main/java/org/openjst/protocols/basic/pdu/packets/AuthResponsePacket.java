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

import java.io.IOException;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class AuthResponsePacket extends AbstractAuthPacket {

    private int errorCode;

    AuthResponsePacket() {
    }

    AuthResponsePacket(final long requestId, final String accountId, final String clientId, final int errorCode,
                       @Nullable final List<Parameter> parameters) {
        this.accountId = accountId;
        this.clientId = clientId;
        this.parameters = parameters;
        this.requestId = requestId;
        this.errorCode = errorCode;
    }

    AuthResponsePacket(final long requestId, final String accountId, final String clientId, final int errorCode) {
        this(requestId, accountId, clientId, errorCode, null);
    }

    public byte[] encode() {
        final ArrayDataOutputBuffer buf = new ArrayDataOutputBuffer();
        try {
            buf.writeVLQInt64(requestId);
            buf.writeUtf8(accountId);
            buf.writeUtf8(clientId);
            buf.writeVLQInt32(errorCode);
            PacketUtils.writeParameters(buf, parameters);
        } catch (DataBufferException ignore) {
            ignore.printStackTrace();
        }
        return buf.toByteArray();
    }

    public void decode(final byte[] data) throws IOException {
        final ArrayDataInputBuffer buf = new ArrayDataInputBuffer(data);
        try {
            this.requestId = buf.readVLQInt64();
            this.accountId = buf.readUtf8();
            this.clientId = buf.readUtf8();
            this.errorCode = buf.readVLQInt32();
            parameters = PacketUtils.readParameters(buf);
        } catch (DataBufferException ignore) {
            ignore.printStackTrace();
        }
    }

    public short getType() {
        return Packets.TYPE_AUTH_RESPONSE;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "AuthResponsePacket{" +
                "errorCode=" + errorCode +
                "} " + super.toString();
    }
}
