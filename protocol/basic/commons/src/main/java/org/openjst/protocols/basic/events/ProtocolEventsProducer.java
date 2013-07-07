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

package org.openjst.protocols.basic.events;

import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Sergey Grachev
 */
public abstract class ProtocolEventsProducer<T extends ProtocolEventsListener> implements Runnable {

    private static final InternalLogger LOG =
            InternalLoggerFactory.getInstance(ProtocolEventsProducer.class.getName());

    private final List<T> listeners = new ArrayList<T>(4);
    private final LinkedBlockingQueue<Event> eventsQueue = new LinkedBlockingQueue<Event>();
    private Thread eventsQueueThread;
    private volatile boolean stop;

    public void queue(final Event event) {
        if (stop) {
            throw new IllegalStateException("ProtocolEventsProducer stopped");
        }
        eventsQueue.offer(event);
    }

    public void start() {
        stop();
        synchronized (this) {
            stop = false;
            eventsQueueThread = new Thread(this);
            eventsQueueThread.start();
        }
    }

    public void stop() {
        synchronized (this) {
            stop = true;
            if (eventsQueueThread != null) {
                eventsQueueThread.interrupt();
                eventsQueueThread = null;
            }
        }
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                final Event event = eventsQueue.take();
                processEvent(event);
            } catch (Exception e) {
                LOG.error(this.getClass().getSimpleName(), e);
            }
        }
    }

    protected void processEvent(final Event event) {
        if (event instanceof ConnectEvent) {
            fireConnectEvent((ConnectEvent) event);
        } else if (event instanceof DisconnectEvent) {
            fireDisconnectEvent((DisconnectEvent) event);
        } else if (event instanceof RPCEvent) {
            fireRPCEvent((RPCEvent) event);
        }
    }

    private void fireRPCEvent(final RPCEvent event) {
        for (final Object listener : getListeners()) {
            try {
                ((ProtocolEventsListener) listener).onRPC(event);
            } catch (Exception e) {
                LOG.error("onRPC", e);
            }
        }
    }

    private void fireDisconnectEvent(final DisconnectEvent event) {
        for (final Object listener : getListeners()) {
            try {
                ((ProtocolEventsListener) listener).onDisconnect(event);
            } catch (Exception e) {
                LOG.error("onDisconnect", e);
            }
        }
    }

    private void fireConnectEvent(final ConnectEvent event) {
        for (final Object listener : getListeners()) {
            try {
                ((ProtocolEventsListener) listener).onConnect(event);
            } catch (Exception e) {
                LOG.error("onConnect", e);
            }
        }
    }

    public void addListener(final T listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(final T listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public synchronized Object[] getListeners() {
        //noinspection unchecked
        return listeners.toArray();
    }
}