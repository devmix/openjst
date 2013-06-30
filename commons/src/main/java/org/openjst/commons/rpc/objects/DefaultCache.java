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

package org.openjst.commons.rpc.objects;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
final class DefaultCache {

    private static final int MAX_CACHE_SIZE = 100;

    private final Map<CacheKey, DefaultMethod> cache = new LinkedHashMap<CacheKey, DefaultMethod>(MAX_CACHE_SIZE, 0.75f, true) {
        private static final long serialVersionUID = -8889920501567950889L;

        @Override
        protected boolean removeEldestEntry(final Map.Entry<CacheKey, DefaultMethod> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    public DefaultMethod get(final CacheKey key) {
        synchronized (cache) {
            return cache.get(key);
        }
    }

    public DefaultMethod put(final CacheKey key, final DefaultMethod method) {
        synchronized (cache) {
            return cache.put(key, method);
        }
    }

    public static final class CacheKey {

        public final int hashCode;
        public final Class clazz;
        public final String methodName;
        public final Class[] types;

        public CacheKey(final Class clazz, final String methodName, final Class[] types) {
            this.clazz = clazz;
            this.methodName = methodName;
            this.types = Arrays.copyOf(types, types.length);
            this.hashCode = calculateHashCode();
        }

        private int calculateHashCode() {
            int result = clazz != null ? clazz.hashCode() : 0;
            result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
            result = 31 * result + (types != null ? Arrays.hashCode(types) : 0);
            return result;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final CacheKey cacheKey = (CacheKey) o;
            return !(clazz != null ? !clazz.equals(cacheKey.clazz) : cacheKey.clazz != null)
                    && !(methodName != null ? !methodName.equals(cacheKey.methodName) : cacheKey.methodName != null)
                    && Arrays.equals(types, cacheKey.types);
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }
    }
}
