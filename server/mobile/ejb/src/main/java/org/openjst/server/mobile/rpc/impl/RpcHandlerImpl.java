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

package org.openjst.server.mobile.rpc.impl;

import org.openjst.commons.rpc.*;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.commons.rpc.objects.RPCObjectsFactory;
import org.openjst.protocols.basic.session.Session;
import org.openjst.server.mobile.rpc.RpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.PermitAll;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * @author Sergey Grachev
 */
@Startup
@Singleton
@PermitAll
public class RpcHandlerImpl implements RpcHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RpcHandler.class);

    private RPCContext context;

    @PostConstruct
    private void create() {
        context = RPCObjectsFactory.newContext(true);
    }

    @PreDestroy
    private void destroy() {
        context = null;
    }

    @Override
    public void invoke(final Session session, final RPCMessageFormat format, final byte[] data) {
        final RPCMessage message;
        try {
            message = format.newFormatter(true).read(data);
        } catch (RPCException e) {
            LOG.error("Read RPC message", e);
            return;
        }

        if (message instanceof RPCRequest) {
            processRequest(session, (RPCRequest) message);
        } else if (message instanceof RPCResponse) {
            processResponse(session, (RPCResponse) message);
        } else {
            LOG.warn("Unknown RPC message", message);
        }
    }

    private void processRequest(final Session session, final RPCRequest request) {
        LOG.info("processRequest {}", request);
        // TODO
    }

    private void processResponse(final Session session, final RPCResponse response) {
        LOG.info("processResponse {}", response);
        // TODO
    }
}
