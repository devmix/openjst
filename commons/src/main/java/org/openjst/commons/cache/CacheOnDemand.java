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
import java.util.List;

/**
 * @author Sergey Grachev
 */
public interface CacheOnDemand<K, V, P> {

    V get(K key);

    V get(K key, @Nullable P userData);

    void put(K key, V value);

    void put(List<Pair<K, V>> list);

    boolean contains(K key);

    void clear();

    interface Listener<K, V, P> {
        @Nullable
        V onFetch(K key, @Nullable P userData);
    }
}
