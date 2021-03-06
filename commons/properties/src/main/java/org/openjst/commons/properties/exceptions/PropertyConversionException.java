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

package org.openjst.commons.properties.exceptions;

import org.openjst.commons.properties.Property;

import javax.annotation.Nullable;

/**
 * @author Sergey Grachev
 */
public final class PropertyConversionException extends PropertyException {

    private static final long serialVersionUID = -204359682433777102L;

    private final Property.Type from;
    private final Property.Type to;
    private final Object value;
    private final Class clazz;

    public PropertyConversionException(final Property.Type from, final Property.Type to, @Nullable final Object value) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.clazz = null;
    }

    public PropertyConversionException(final Class clazz, final Property.Type to) {
        this.from = null;
        this.to = to;
        this.value = null;
        this.clazz = clazz;
    }

    @Nullable
    public Property.Type getFrom() {
        return from;
    }

    public Property.Type getTo() {
        return to;
    }

    @Nullable
    public Class getClazz() {
        return clazz;
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PropertyConversionException{" +
                "from=" + from +
                ", to=" + to +
                ", value=" + value +
                ", clazz=" + clazz +
                '}';
    }
}
