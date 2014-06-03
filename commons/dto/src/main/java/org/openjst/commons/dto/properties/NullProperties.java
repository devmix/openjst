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

import org.openjst.commons.dto.constants.Constants;

import java.util.Collections;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
class NullProperties implements Properties<Object, Object> {

    @Override
    public Object get(final Object name) {
        return Constants.object();
    }

    @Override
    public Properties<Object, Object> set(final Object name, final Object value) {
        return PropertiesFactory.nullProperties();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean contains(final Object name) {
        return false;
    }

    @Override
    public boolean empty() {
        return true;
    }

    @Override
    public Map<Object, Object> getProperties() {
        return Collections.emptyMap();
    }
}
