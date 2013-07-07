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

package org.openjst.server.mobile.server;

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.commons.rpc.RPCRequest;
import org.openjst.server.commons.model.types.ProtocolType;

/**
 * @author Sergey Grachev
 */
public interface Router {

    void activateClientRoute(String accountId, String clientId, ProtocolType type);

    void activateServerRoute(String accountId, ProtocolType type);

    void deactivateClientRoute(String accountId, String clientId, ProtocolType type);

    void deactivateServerRoute(String accountId, ProtocolType type);

    RouteResult rpcForwardToServer(String accountId, String clientId, RPCMessageFormat format, byte[] data);

    RouteResult rpcForwardToClient(String accountId, String clientId, RPCMessageFormat format, byte[] data);

    RouteResult rpcInvoke(String accountId, RPCMessageFormat format, RPCRequest request);

}
