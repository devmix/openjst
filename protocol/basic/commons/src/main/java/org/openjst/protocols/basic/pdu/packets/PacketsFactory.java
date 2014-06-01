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

import org.openjst.commons.protocols.auth.SecretKey;
import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.protocols.basic.pdu.Packets;
import org.openjst.protocols.basic.pdu.beans.Parameter;

import javax.annotation.Nullable;
import java.util.Set;

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
            case Packets.TYPE_AUTHENTICATION_CLIENT:
                return new AuthClientRequestPacket();
            case Packets.TYPE_AUTHENTICATION_SERVER:
                return new AuthServerRequestPacket();
            case Packets.TYPE_AUTHENTICATION_RESPONSE:
                return new AuthResponsePacket();
            case Packets.TYPE_RPC:
                return new RPCPacket();
            case Packets.TYPE_DELIVERY_RESPONSE:
                return new DeliveryResponsePacket();
            default:
                throw new IllegalArgumentException("Unknown type of packet");
        }
    }

    public static AuthClientRequestPacket newAuthClientRequestPacket(
            final long packetId, final String accountId, final String clientId, @Nullable final SecretKey secretKey,
            @Nullable final Set<Parameter> parameters
    ) {
        return new AuthClientRequestPacket(packetId, accountId, clientId, secretKey, parameters);
    }

    public static AuthServerRequestPacket newAuthServerRequestPacket(
            final long packetId, @Nullable final SecretKey secretKey, @Nullable final Set<Parameter> parameters
    ) {
        return new AuthServerRequestPacket(packetId, secretKey, parameters);
    }

    public static RPCPacket newRPCPacket(
            final long packetId, final long timestamp, final String clientId, final RPCMessageFormat format, final byte[] data
    ) {
        return new RPCPacket(packetId, timestamp, clientId, format, data, true);
    }

    public static DateTimeSyncPacket newDateTimeSyncPacket(final long timestamp, final byte timeZoneOffset) {
        return new DateTimeSyncPacket(timestamp, timeZoneOffset);
    }

    public static AuthResponsePacket newAuthResponsePacket(final long packetId, final int status, @Nullable final Set<Parameter> parameters) {
        return new AuthResponsePacket(packetId, status, parameters);
    }

    public static AuthResponsePacket newAuthResponsePacket(final long packetId, final int status) {
        return new AuthResponsePacket(packetId, status);
    }

    public static DeliveryResponsePacket newResponsePacket(final long packetId, final int status) {
        return new DeliveryResponsePacket(packetId, status);
    }
}
