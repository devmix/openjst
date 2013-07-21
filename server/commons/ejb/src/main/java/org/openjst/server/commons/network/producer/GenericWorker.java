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

package org.openjst.server.commons.network.producer;

import org.openjst.server.commons.model.types.MessageDeliveryState;
import org.openjst.server.commons.network.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
final class GenericWorker<P extends Persistence<A, M>, A extends Actor<?>, M extends Message<?>> implements MessageProducer.Worker<P, A, M> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericWorker.class);

    private static final int PRE_LOAD_MESSAGES = 4;

    protected final P persistence;
    protected final Router<A, M> router;

    private final ActorsLock<A> actorsLock;
    private final ActorsWatcher<A> actorsWatcher;
    private final ActorsType actorsType;
    private final Object wait = new Object();
    private volatile boolean awake = false;

    public GenericWorker(final P persistence, final Router<A, M> router,
                         final ActorsLock<A> actorsLock, final ActorsWatcher<A> actorsWatcher,
                         final ActorsType actorsType) {
        this.persistence = persistence;
        this.router = router;
        this.actorsLock = actorsLock;
        this.actorsWatcher = actorsWatcher;
        this.actorsType = actorsType;
    }

    @Override
    public void run() {
        LOG.debug("Run task {}", this.hashCode());

        while (Thread.currentThread().isAlive() && !Thread.currentThread().isInterrupted()) {
            awake = false;
            boolean hasDeliveredMessages = false;
            final Set<A> connected = router.getConnected();
            if (!connected.isEmpty()) {
                hasDeliveredMessages = sendMessagesOfActors(connected);
            }

            if (!hasDeliveredMessages) {
                synchronized (wait) {
                    try {
                        if (!awake) {
                            wait.wait();
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }
    }

    private boolean sendMessagesOfActors(final Set<A> connected) {
        final Set<A> actors = ActorsType.CLIENT.equals(actorsType)
                ? persistence.filterClientsWithNotDeliveredMessages(connected)
                : persistence.filterServersWithNotDeliveredMessages(connected);

        assert actors != null;

        boolean hasDeliveredMessages = false;
        A actor = actorsWatcher.poll();
        while (actor != null) {
            if (actorsLock.tryLock(actor)) {
                try {
                    final List<M> messages = ActorsType.CLIENT.equals(actorsType)
                            ? persistence.getNotDeliveredToClients(actor, PRE_LOAD_MESSAGES)
                            : persistence.getNotDeliveredToServers(actor, PRE_LOAD_MESSAGES);

                    assert messages != null;

                    hasDeliveredMessages |= sendActorMessages(actor, messages);
                } catch (Exception e) {
                    LOG.error("getNotDeliveredMessages", e);
                    continue;
                } finally {
                    actorsLock.unlock(actor);
                }
            }
            actor = actorsWatcher.poll();
        }

        return hasDeliveredMessages;
    }

    private boolean sendActorMessages(final A actor, final List<M> messages) {
        boolean hasDeliveredMessages = false;
        for (final M message : messages) {
            try {
                final DeliveryResult result = router.send(actor, message);

                assert result != null;

                if (result.isSuccess()) {
                    persistence.deliverySuccess(message);
                    hasDeliveredMessages = true;
                }

                persistence.deliveryFail(message, result.getState(), result.getMessage());
            } catch (Exception e) {
                LOG.error("send", e);
                persistence.deliveryFail(message, MessageDeliveryState.INTERNAL_SERVER_ERROR, e.getMessage());
                break;
            }
        }

        return hasDeliveredMessages;
    }

    @Override
    public void awake() {
        synchronized (wait) {
            awake = true;
            wait.notify();
        }
    }
}
