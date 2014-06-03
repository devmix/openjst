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

package org.openjst.commons.cache;

import org.openjst.commons.dto.tuples.Pair;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
final class CacheOnDemandLRU<K, V, P> implements CacheOnDemand<K, V, P> {

    private final Object lock = new Object();
    private final Listener<K, V, P> listener;
    private final boolean allowNulls;
    private final Map<K, V> cache;

    CacheOnDemandLRU(final Listener<K, V, P> listener, final int size) {
        this(listener, size, false);
    }

    CacheOnDemandLRU(final Listener<K, V, P> listener, final int size, final boolean allowNulls) {
        if (listener == null) {
            throw new IllegalArgumentException("Callback can't be null");
        }

        this.allowNulls = allowNulls;
        this.listener = listener;
        this.cache = new LinkedHashMap<K, V>(size + 1, 0.75f, true) {
            private static final long serialVersionUID = -8446183480233559215L;

            public boolean removeEldestEntry(final Map.Entry eldest) {
                return size() > size;
            }
        };
    }

    @Override
    public V get(final K key, @Nullable final P userData) {
        if (key == null) {
            throw new IllegalArgumentException("Key can't be null");
        }

        V e;
        synchronized (lock) {
            e = cache.get(key);
            if (e == null && (!allowNulls || !cache.containsKey(key))) {
                e = listener.onFetch(key, userData);
                if (e != null || allowNulls) {
                    cache.put(key, e);
                }
            }
        }

        return e;
    }

    @Override
    public V get(final K key) {
        return get(key, null);
    }

    @Override
    public void put(final K key, final V value) {
        synchronized (lock) {
            cache.put(key, value);
        }
    }

    @Override
    public void put(final List<Pair<K, V>> list) {
        synchronized (lock) {
            for (final Pair<K, V> entry : list) {
                cache.put(entry.first(), entry.second());
            }
        }
    }

    @Override
    public boolean contains(final K key) {
        synchronized (lock) {
            return cache.containsKey(key);
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            cache.clear();
        }
    }
}
