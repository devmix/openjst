/*
 * Copyright (C) 2013-2014 OpenJST Project
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

package org.openjst.commons.properties.values;

import org.openjst.commons.properties.Property;
import org.openjst.commons.properties.exceptions.PropertyNonWritableException;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
final class NullValues implements Property.Values {

    @Nullable
    @Override
    public Property.Immutable get(final Property property) {
        return ValuesBuilder.nullValue();
    }

    @Override
    public Property.Values put(final Property property, @Nullable final Object value) {
        throw new PropertyNonWritableException();
    }

    @Override
    public Property.Values put(final Map<Property, Object> values) {
        throw new PropertyNonWritableException();
    }

    @Override
    public Property.Values clear(final Property property) {
        throw new PropertyNonWritableException();
    }

    @Override
    public Map<String, Object> asRaw() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> asRaw(final Property... properties) {
        return Collections.emptyMap();
    }
}
