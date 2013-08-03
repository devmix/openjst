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

import org.openjst.server.commons.model.types.MessageDeliveryState;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.network.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Sergey Grachev
 */
abstract class AbstractRouter<A extends Actor<?>, M extends Message<?>> implements Router<A, M> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRouter.class);
    private static final ProtocolType[] EMPTY_PROTOCOL_TYPES = new ProtocolType[0];

    private final Map<ProtocolType, Route<A, M>> routes;
    private final Map<A, ActorRoutesState> states = new HashMap<A, ActorRoutesState>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public AbstractRouter(final Map<ProtocolType, Route<A, M>> routes) {
        this.routes = routes;
    }

    @Override
    public Set<A> getConnected() {
        final ReentrantReadWriteLock.ReadLock r = lock.readLock();
        r.lock();
        try {
            Set<A> result = null;
            for (final Map.Entry<A, ActorRoutesState> actor : states.entrySet()) {
                if (actor.getValue().isConnected()) {
                    if (result == null) {
                        result = new LinkedHashSet<A>();
                    }
                    result.add(actor.getKey());
                }
            }
            return result != null ? result : Collections.<A>emptySet();
        } finally {
            r.unlock();
        }
    }

    @Override
    public final DeliveryResult send(final A actor, final M message) {
        for (final ProtocolType protocolType : getActiveRoutes(actor)) {
            final Route<A, M> route = routes.get(protocolType);
            if (route != null) {
                return sendUsingRoute(route, actor, message);
            }
        }
        return DeliveryResult.as(MessageDeliveryState.NO_FORWARD_RECIPIENT, null);
    }

    @Override
    public boolean isConnected(final A actor) {
        return ensureRoutesExists(actor).isConnected();
    }

    @Override
    public void connected(final A actor, final ProtocolType protocolType) {
        ensureRoutesExists(actor).connected(protocolType);
    }

    @Override
    public void disconnected(final A actor, final ProtocolType protocolType) {
        ensureRoutesExists(actor).disconnected(protocolType);
    }

    private ActorRoutesState ensureRoutesExists(final A actor) {
        final ReentrantReadWriteLock.ReadLock r = lock.readLock();
        final ReentrantReadWriteLock.WriteLock w = lock.writeLock();

        ActorRoutesState state;
        r.lock();
        try {
            state = states.get(actor);
            if (state == null) {
                try {
                    r.unlock();
                    w.lock();
                    state = new ActorRoutesState();
                    states.put(actor, state);
                    r.lock();
                } finally {
                    w.unlock();
                }
            }
        } finally {
            r.unlock();
        }

        return state;
    }

    protected ProtocolType[] getActiveRoutes(final A actor) {
        final ReentrantReadWriteLock.ReadLock r = lock.readLock();

        r.lock();
        try {
            final ActorRoutesState state = states.get(actor);
            if (state != null) {
                return state.getConnectedRoutes();
            }
        } finally {
            r.unlock();
        }

        return EMPTY_PROTOCOL_TYPES;
    }

    protected abstract DeliveryResult sendUsingRoute(Route<A, M> route, A actor, M message);
}
