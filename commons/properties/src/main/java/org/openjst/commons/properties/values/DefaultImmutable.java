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
import org.openjst.commons.properties.restrictions.validators.Validators;

import javax.annotation.Nullable;
import java.io.Serializable;

import static org.openjst.commons.properties.Caches.nullAsOf;
import static org.openjst.commons.properties.Caches.typeOf;
import static org.openjst.commons.properties.converters.Converters.basic;

/**
 * Only for internal use, don't override this class
 *
 * @author Sergey Grachev
 */
public class DefaultImmutable implements Property.Immutable, Serializable {

    private static final long serialVersionUID = -5649676429384442223L;

    protected final Property property;
    protected Object value;
    protected boolean initialized;

    public DefaultImmutable(final Property property, @Nullable final Object value) {
        this.property = property;
        value(validateAndNormalize(value));
        initialized = true;
    }

    @Nullable
    protected Object validateAndNormalize(final Object value) {
        final Object normalized = value == null ? null : basic().asOf(typeOf(property), value);
        Validators.standard().validate(property, normalized);
        return normalized;
    }

    @Nullable
    protected Object value() {
        return this.value == null ? nullAsOf(basic(), property) : value;
    }

    protected void value(@Nullable final Object value) {
        this.value = value;
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
        return basic().asBoolean(typeOf(property), value());
    }

    @Override
    public byte asByte() {
        return basic().asByte(typeOf(property), value());
    }

    @Override
    public short asShort() {
        return basic().asShort(typeOf(property), value());
    }

    @Override
    public int asInt() {
        return basic().asInt(typeOf(property), value());
    }

    @Override
    public long asLong() {
        return basic().asLong(typeOf(property), value());
    }

    @Override
    public float asFloat() {
        return basic().asFloat(typeOf(property), value());
    }

    @Override
    public double asDouble() {
        return basic().asDouble(typeOf(property), value());
    }

    @Override
    public char asChar() {
        return basic().asChar(typeOf(property), value());
    }

    @Nullable
    @Override
    public String asString() {
        return basic().asString(typeOf(property), value());
    }

    @Nullable
    @Override
    public LocalTime asTime() {
        return basic().asTime(typeOf(property), value());
    }

    @Override
    public <T extends Enum<T>> T asEnum(final Class<T> clazz) {
        return basic().asEnum(typeOf(property), value(), clazz);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
