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

package org.openjst.server.commons.network.router;

import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.network.Actor;
import org.openjst.server.commons.network.Message;
import org.openjst.server.commons.network.Route;
import org.openjst.server.commons.network.Router;

import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class RouterFactory {

    private RouterFactory() {
    }

    public static <R extends Actor<?>, M extends Message<?>> Router<R, M> createRouterForClients(
            final Map<ProtocolType, Route<R, M>> routes
    ) {
        return new GenericClientsRouter<R, M>(routes);
    }

    public static <R extends Actor<?>, M extends Message<?>> Router<R, M> createRouterForServers(
            final Map<ProtocolType, Route<R, M>> routes
    ) {
        return new GenericServersRouter<R, M>(routes);
    }
}
