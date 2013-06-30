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
import org.openjst.commons.protocols.auth.SecretKey;
import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.protocols.basic.pdu.Packets;
import org.openjst.protocols.basic.pdu.beans.Parameter;

import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class PacketsFactory {
    private PacketsFactory() {
    }

    public static AbstractPacket newOfType(final byte type) {
        switch (type) {
            case Packets.TYPE_DATETIME_SYNC:
                return new DateTimeSyncPacket();
            case Packets.TYPE_PING:
                return new PingPacket();
            case Packets.TYPE_AUTH_BASIC:
                return new AuthRequestBasicPacket();
            case Packets.TYPE_AUTH_RESPONSE:
                return new AuthResponsePacket();
            case Packets.TYPE_PRESENCE_STATE:
                return new PresenceStatePacket();
            case Packets.TYPE_RPC_METHOD:
                return new RPCPacket();
            default:
                throw new IllegalArgumentException("Unknown type of packet");
        }
    }

    public static AuthRequestBasicPacket newAuthRequestBasicPacket(final long requestId,
                                                                   final String accountId, final String clientId,
                                                                   @Nullable final SecretKey secretKey,
                                                                   @Nullable final List<Parameter> parameters) {
        return new AuthRequestBasicPacket(requestId, accountId, clientId, secretKey, parameters);
    }

    public static RPCPacket newRPCPacket(final long uuid, final long timestamp, final RPCMessageFormat format, final byte[] data) {
        return new RPCPacket(uuid, timestamp, format, data);
    }

    public static DateTimeSyncPacket newDateTimeSyncPacket(final long timestamp, final byte timeZoneOffset) {
        return new DateTimeSyncPacket(timestamp, timeZoneOffset);
    }

    public static AuthResponsePacket newAuthResponsePacket(final long requestId, final String accountId, final String clientId,
                                                           final int errorCode, @Nullable final List<Parameter> parameters) {
        return new AuthResponsePacket(requestId, accountId, clientId, errorCode, parameters);
    }

    public static AuthResponsePacket newAuthResponsePacket(final long requestId, final String accountId, final String clientId,
                                                           final int errorCode) {
        return new AuthResponsePacket(requestId, accountId, clientId, errorCode);
    }

    public static PresenceStatePacket newPresenceStatePacket(final long uuid, final long timestamp, final byte state, final String data) {
        return new PresenceStatePacket(uuid, timestamp, state, data);
    }
}
