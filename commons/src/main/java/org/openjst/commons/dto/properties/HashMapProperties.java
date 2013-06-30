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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public class HashMapProperties<K, V> implements Properties<K, V> {

    private final Map<K, V> data = new HashMap<K, V>(0);

    @Override
    public V property(final K name) {
        return data.get(name);
    }

    @Override
    public Properties<K, V> withProperty(final K name, final V value) {
        data.put(name, value);
        return this;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public String toString() {
        return "Properties{" +
                "data=" + data +
                '}';
    }
}
