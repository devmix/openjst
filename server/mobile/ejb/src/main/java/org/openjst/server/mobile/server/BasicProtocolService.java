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
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.server.mobile.rpc.DeliveryResult;

/**
 * @author Sergey Grachev
 */
public interface BasicProtocolService {

    DeliveryResult rpcForwardToServer(String accountId, String clientId, RPCMessageFormat format, byte[] data) throws ClientNotConnectedException;

    DeliveryResult rpcForwardToClient(String accountId, String clientId, RPCMessageFormat format, byte[] data) throws ClientNotConnectedException;

    DeliveryResult rpcInvoke(String accountId, RPCMessageFormat format, byte[] data) throws ClientNotConnectedException;
}
