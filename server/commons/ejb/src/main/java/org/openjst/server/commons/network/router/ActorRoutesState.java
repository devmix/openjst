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

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
final class ActorRoutesState {

    private final Route[] routes;
    private final Set<ProtocolType> connected;

    public ActorRoutesState() {
        final ProtocolType[] availableProtocols = ProtocolType.values();
        connected = new LinkedHashSet<ProtocolType>(availableProtocols.length);
        routes = new Route[availableProtocols.length];
        for (int i = 0; i < availableProtocols.length; i++) {
            final ProtocolType type = availableProtocols[i];
            routes[i] = new Route(type, 0);
        }
    }

    public void connected(final ProtocolType type) {
        synchronized (routes) {
            changePriority(type, true);
            connected.add(type);
        }
    }

    public void disconnected(final ProtocolType type) {
        synchronized (routes) {
            changePriority(type, false);
            connected.remove(type);
        }
    }

    public ProtocolType[] getConnectedRoutes() {
        final ProtocolType[] result;
        synchronized (routes) {
            result = new ProtocolType[connected.size()];
            int i = 0;
            for (final Route route : routes) {
                if (connected.contains(route.type)) {
                    result[i++] = route.type;
                }
            }
        }
        return result;
    }

    private void changePriority(final ProtocolType type, final boolean increase) {
        for (final Route route : routes) {
            if (route.type.equals(type)) {
                if (increase) {
                    route.priority += 1;
                } else {
                    route.priority -= 2;
                }
                break;
            }
        }

        Arrays.sort(routes, new Comparator<Route>() {
            @Override
            public int compare(final Route o1, final Route o2) {
                return o2.priority - o1.priority;
            }
        });
    }

    public boolean isConnected() {
        synchronized (routes) {
            return !connected.isEmpty();
        }
    }

    private static final class Route {
        private final ProtocolType type;
        private int priority;

        public Route(final ProtocolType type, final int priority) {
            this.type = type;
            this.priority = priority;
        }
    }
}
