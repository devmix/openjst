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

package org.openjst.server.mobile.network;

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.network.Actor;

/**
 * @author Sergey Grachev
 */
public interface NetworkService {

    void clientConnected(Actor<Long> actor, ProtocolType protocolType, String host);

    void clientDisconnected(Actor<Long> actor, ProtocolType protocolType);

    void clientRPCForwardTo(Long accountId, Long clientId, RPCMessageFormat format, byte[] data);

    void clientRPCForwardBroadcast(Long accountId, RPCMessageFormat format, byte[] data, boolean onlyOnline);

    void serverConnected(Actor<Long> actor, ProtocolType protocolType, String remoteHost);

    void serverDisconnect(Actor<Long> actor, ProtocolType protocolType);

    void serverRPCForwardTo(Long accountId, Long clientId, RPCMessageFormat format, byte[] data);
}
