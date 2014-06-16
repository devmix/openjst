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

/**
 * @author Sergey Grachev
 */
final class NullValue implements Property.Immutable {

    private static final NullProperty PROPERTY = new NullProperty();

    @Override
    public Property property() {
        return PROPERTY;
    }

    @Override
    public boolean isNonNull() {
        return false;
    }

    @Nullable
    @Override
    public Object get() {
        return null;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public byte asByte() {
        return 0;
    }

    @Override
    public short asShort() {
        return 0;
    }

    @Override
    public int asInt() {
        return 0;
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public float asFloat() {
        return 0;
    }

    @Override
    public double asDouble() {
        return 0;
    }

    @Override
    public char asChar() {
        return 0;
    }

    @Nullable
    @Override
    public String asString() {
        return null;
    }

    @Nullable
    @Override
    public LocalTime asTime() {
        return null;
    }

    @Override
    public <T extends Enum<T>> T asEnum(final Class<T> clazz) {
        return null;
    }
}
