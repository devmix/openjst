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

import org.joda.time.LocalTime;
import org.openjst.commons.properties.Property;

import javax.annotation.Nullable;
import java.io.Serializable;

import static org.openjst.commons.properties.converters.Converters.standard;

/**
 * Only for internal use, don't override this class
 *
 * @author Sergey Grachev
 */
class DefaultImmutable implements Property.Immutable, Serializable {

    private static final long serialVersionUID = -5649676429384442223L;

    protected final Property property;
    protected Object value;
    protected boolean initialized;

    public DefaultImmutable(final Property property, @Nullable final Object value) {
        this.property = property;
        value(value);
        initialized = true;
    }

    @Nullable
    protected Object value() {
        return this.value == null ? property.defaultValue() : value;
    }

    protected void value(@Nullable final Object value) {
        if (value == null) {
            this.value = null;
        } else {
            this.value = standard().asOf(property.type(), value);
        }
    }

    @Override
    public Property property() {
        return property;
    }

    @Override
    public boolean isNonNull() {
        return value() != null;
    }

    @Nullable
    @Override
    public Object get() {
        return value();
    }

    @Override
    public boolean asBoolean() {
        return standard().asBoolean(property.type(), value());
    }

    @Override
    public byte asByte() {
        return standard().asByte(property.type(), value());
    }

    @Override
    public short asShort() {
        return standard().asShort(property.type(), value());
    }

    @Override
    public int asInt() {
        return standard().asInt(property.type(), value());
    }

    @Override
    public long asLong() {
        return standard().asLong(property.type(), value());
    }

    @Override
    public float asFloat() {
        return standard().asFloat(property.type(), value());
    }

    @Override
    public double asDouble() {
        return standard().asDouble(property.type(), value());
    }

    @Override
    public char asChar() {
        return standard().asChar(property.type(), value());
    }

    @Nullable
    @Override
    public String asString() {
        return standard().asString(property.type(), value());
    }

    @Nullable
    @Override
    public LocalTime asTime() {
        return standard().asTime(property.type(), value());
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
