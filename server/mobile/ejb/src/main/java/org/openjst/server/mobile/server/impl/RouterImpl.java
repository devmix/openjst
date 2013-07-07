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

package org.openjst.server.mobile.server.impl;

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.commons.rpc.RPCRequest;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.mobile.rpc.RpcHandler;
import org.openjst.server.mobile.server.RouteResult;
import org.openjst.server.mobile.server.Router;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * @author Sergey Grachev
 */
@Singleton
@Startup
@LocalBean
public class RouterImpl implements Router {

    @Inject
    private RpcHandler rpcHandler;

    @Override
    public void activateClientRoute(final String accountId, final String clientId, final ProtocolType type) {
        // TODO
    }

    @Override
    public void activateServerRoute(final String accountId, final ProtocolType type) {
        // TODO
    }

    @Override
    public void deactivateClientRoute(final String accountId, final String clientId, final ProtocolType type) {
        // TODO
    }

    @Override
    public void deactivateServerRoute(final String accountId, final ProtocolType type) {
        // TODO
    }

    @Override
    public RouteResult rpcForwardToServer(final String accountId, final String clientId, final RPCMessageFormat format, final byte[] data) {
        // TODO store into DB, invoke
        return null;
    }

    @Override
    public RouteResult rpcForwardToClient(final String accountId, final String clientId, final RPCMessageFormat format, final byte[] data) {
        // TODO store into DB, invoke
        return null;
    }

    @Override
    public RouteResult rpcInvoke(final String accountId, final RPCMessageFormat format, final RPCRequest request) {
        // TODO invoke
        return null;
    }
}
