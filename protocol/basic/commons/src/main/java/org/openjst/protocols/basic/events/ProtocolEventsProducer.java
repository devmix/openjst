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

import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.RPCPacket;
import org.openjst.protocols.basic.sessions.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * // TODO create normal events dispatcher (thread and events queue)
 *
 * @author Sergey Grachev
 */
public abstract class ProtocolEventsProducer<T extends ProtocolEventsListener> {
    protected final List<T> listeners = new ArrayList<T>(4);

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

    public void onConnect(final Session session) {
        for (final Object listener : getListeners()) {
            try {
                //noinspection unchecked
                ((T) listener).onConnect(session);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onAuthorizationFail(final int errorCode, final AbstractAuthPacket authRequest) {
        for (final Object listener : getListeners()) {
            try {
                //noinspection unchecked
                ((T) listener).onAuthorizationFail(errorCode, authRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onDisconnect(final Session session) {
        for (final Object listener : getListeners()) {
            try {
                //noinspection unchecked
                ((T) listener).onDisconnect(session);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onRPC(final Session session, final RPCPacket packet) {
        for (final Object listener : getListeners()) {
            try {
                //noinspection unchecked
                ((T) listener).onRPC(session, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}