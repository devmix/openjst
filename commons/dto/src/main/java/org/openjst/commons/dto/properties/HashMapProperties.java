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

package org.openjst.commons.dto.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public class HashMapProperties<K, V> implements Properties<K, V> {

    private final Map<K, V> properties = new HashMap<K, V>(0);
    private final Map<K, V> immutable = Collections.unmodifiableMap(properties);

    @Override
    public V get(final K name) {
        return properties.get(name);
    }

    @Override
    public Properties<K, V> set(final K name, final V value) {
        properties.put(name, value);
        return this;
    }

    @Override
    public int size() {
        return properties.size();
    }

    @Override
    public boolean contains(final K name) {
        return properties.containsKey(name);
    }

    @Override
    public boolean empty() {
        return properties.isEmpty();
    }

    @Override
    public Map<K, V> getProperties() {
        return immutable;
    }

    @Override
    public String toString() {
        return "Properties{" +
                "data=" + properties +
                '}';
    }
}
