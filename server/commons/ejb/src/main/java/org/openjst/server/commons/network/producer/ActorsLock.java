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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
final class ActorsLock<A> {

    private final Map<A, Thread> locks = new HashMap<A, Thread>();

    public boolean tryLock(final A actor) {
        final Thread thread;
        synchronized (locks) {
            thread = locks.get(actor);
            if (thread == null || !thread.isAlive()) {
                locks.put(actor, Thread.currentThread());
                return true;
            }
        }
        return thread.equals(Thread.currentThread());
    }

    public boolean unlock(final A actor) {
        synchronized (locks) {
            final Thread thread = locks.get(actor);
            if (thread == null || !thread.isAlive() || thread.equals(Thread.currentThread())) {
                locks.remove(actor);
                return true;
            }
        }
        return false;
    }
}
