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

package org.openjst.commons.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author Sergey Grachev
 */
public final class ResourcesLock<T> {

    private final Map<T, Semaphore> locked = new HashMap<T, Semaphore>();

    public void lock(final T obj) {
        Semaphore semaphore;
        synchronized (locked) {
            semaphore = locked.get(obj);
            if (semaphore == null) {
                locked.put(obj, semaphore = new Semaphore(1));
            }
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean lock(final T obj, final long timeout) throws InterruptedException {
        Semaphore semaphore;
        synchronized (locked) {
            semaphore = locked.get(obj);
            if (semaphore == null) {
                locked.put(obj, semaphore = new Semaphore(1));
            }
        }
        return semaphore.tryAcquire(timeout, TimeUnit.MILLISECONDS);
    }

    public void unlock(final T obj) {
        synchronized (locked) {
            final Semaphore semaphore = locked.get(obj);
            if (semaphore != null) {
                semaphore.release(1);
                locked.remove(obj);
            }
        }
    }

    public void unlockAll() {
        synchronized (locked) {
            locked.clear();
        }
    }
}
