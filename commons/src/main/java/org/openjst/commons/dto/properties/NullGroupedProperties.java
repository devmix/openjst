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

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
class NullGroupedProperties extends NullProperties implements GroupedProperties<Object, Object, Object> {

    @Nullable
    @Override
    public GroupedProperties<Object, Object, Object> group(final Object group) {
        return PropertiesFactory.nullGroupedProperties();
    }

    @Override
    public GroupedProperties<Object, Object, Object> ensureGroup(final Object group) {
        return PropertiesFactory.nullGroupedProperties();
    }

    @Nullable
    @Override
    public GroupedProperties<Object, Object, Object> back() {
        return PropertiesFactory.nullGroupedProperties();
    }

    @Override
    public GroupedProperties<Object, Object, Object> back(final int count) {
        return PropertiesFactory.nullGroupedProperties();
    }

    @Override
    public boolean containsGroup(final Object group) {
        return false;
    }

    @Override
    public int sizeGroups() {
        return 0;
    }

    @Override
    public boolean groupsEmpty() {
        return true;
    }

    @Override
    public GroupedProperties<Object, Object, Object> set(final Object name, final Object value) {
        return PropertiesFactory.nullGroupedProperties();
    }

    @Override
    public Map<Object, ? extends GroupedProperties<Object, Object, Object>> getGroups() {
        return Collections.emptyMap();
    }
}
