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
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public interface GroupedProperties<G, K, V> extends Properties<K, V> {

    /**
     * Get an existing group
     */
    @Nullable
    GroupedProperties<G, K, V> group(G group);

    /**
     * Like {@link #group(Object)}, but if group does not exists, it will be created.
     */
    GroupedProperties<G, K, V> ensureGroup(G group);

    GroupedProperties<G, K, V> back();

    GroupedProperties<G, K, V> back(int count);

    boolean containsGroup(G group);

    int sizeGroups();

    boolean groupsEmpty();

    @Override
    GroupedProperties<G, K, V> set(K name, V value);

    Map<G, ? extends GroupedProperties<G, K, V>> getGroups();
}
