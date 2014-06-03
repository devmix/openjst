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

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public class HashMapGroupedProperties<G, K, V> extends HashMapProperties<K, V> implements GroupedProperties<G, K, V> {

    private final Map<G, HashMapGroupedProperties<G, K, V>> groups = new HashMap<G, HashMapGroupedProperties<G, K, V>>(0);
    private final Map<G, HashMapGroupedProperties<G, K, V>> immutable = Collections.unmodifiableMap(groups);
    private final GroupedProperties<G, K, V> parent;

    public HashMapGroupedProperties() {
        this(PropertiesFactory.<G, K, V>nullGroupedProperties());
    }

    public HashMapGroupedProperties(final GroupedProperties<G, K, V> parent) {
        this.parent = parent;
    }

    @Nullable
    @Override
    public GroupedProperties<G, K, V> group(final G group) {
        return groups.get(group);
    }

    @Override
    public GroupedProperties<G, K, V> ensureGroup(final G group) {
        synchronized (groups) {
            if (!groups.containsKey(group)) {
                groups.put(group, new HashMapGroupedProperties<G, K, V>(this));
            }
            return groups.get(group);
        }
    }

    @Override
    public GroupedProperties<G, K, V> back() {
        return parent;
    }

    @Override
    public GroupedProperties<G, K, V> back(final int count) {
        int i = count;
        GroupedProperties<G, K, V> result = this;
        while (i-- > 0) {
            final GroupedProperties<G, K, V> nextParent = result.back();
            if (nextParent instanceof NullGroupedProperties) {
                break;
            }
            result = nextParent;
        }
        return result;
    }

    @Override
    public boolean containsGroup(final G group) {
        return groups.containsKey(group);
    }

    @Override
    public int sizeGroups() {
        return groups.size();
    }

    @Override
    public boolean groupsEmpty() {
        return groups.isEmpty();
    }

    @Override
    public GroupedProperties<G, K, V> set(final K name, final V value) {
        super.set(name, value);
        return this;
    }

    @Override
    public Map<G, ? extends GroupedProperties<G, K, V>> getGroups() {
        return immutable;
    }
}
