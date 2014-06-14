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

/**
 * @author Sergey Grachev
 */
public final class ValuesBuilder {

    private static final Property.Immutable NULL_VALUE = new NullValue();
    private static final Property.Values NULL_VALUES = new NullValues();

    private ValuesBuilder() {
    }

    public static Property.Immutable nullValue() {
        return NULL_VALUE;
    }

    public static Property.Values nullValues() {
        return NULL_VALUES;
    }

    public static Property.Values values() {
        return new DefaultValues(false);
    }

    /**
     * @param autoCreate if true and parameter doesn't exist then new parameter with value by default will be added
     */
    public static Property.Values values(final boolean autoCreate) {
        return new DefaultValues(autoCreate);
    }

    public static Property.Immutable immutable(final Property property, final Object value) {
        return new DefaultImmutable(property, value);
    }

    public static Property.Mutable mutable(final Property property, final Object value) {
        return new DefaultMutable(property, value);
    }

    public static boolean isNullValue(final Property.Immutable value) {
        return NULL_VALUE.equals(value);
    }

    public static boolean isNonNullValue(final Property.Immutable value) {
        return !NULL_VALUE.equals(value);
    }
}
