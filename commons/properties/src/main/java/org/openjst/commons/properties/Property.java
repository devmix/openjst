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

package org.openjst.commons.properties;

import org.joda.time.LocalTime;
import org.openjst.commons.properties.restrictions.Restriction;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public interface Property {

    Property[] NO_DEPENDENCIES = new Property[0];

    String key();

    Type type();

    Restriction restriction();

    Property[] requires();

    @Nullable
    Object defaultValue();

    interface Values {

        /**
         * @return value of parameter,
         * {@link org.openjst.commons.properties.values.ValuesBuilder#nullValue() nullValue} or new parameter with
         * value by default ({@link org.openjst.commons.properties.values.ValuesBuilder#values(boolean) values})
         */
        Immutable of(Property property);

        Values put(Property property, @Nullable Object value);

        Values put(Map<Property, Object> values);

        Values clear(Property property);

        Map<String, Object> asRaw();

        Map<String, Object> asRaw(Property... properties);
    }

    interface Immutable {

        Property property();

        boolean isNonNull();

        @Nullable
        Object get();

        boolean asBoolean();

        byte asByte();

        short asShort();

        int asInt();

        long asLong();

        float asFloat();

        double asDouble();

        char asChar();

        @Nullable
        String asString();

        @Nullable
        LocalTime asTime();
    }

    interface Mutable extends Immutable {

        Mutable set(@Nullable Object value);
    }

    interface Converter {

        @Nullable
        Object asOf(Type type, @Nullable Object value);

        boolean asBoolean(Type type, @Nullable Object value);

        byte asByte(Type type, @Nullable Object value);

        short asShort(Type type, @Nullable Object value);

        int asInt(Type type, @Nullable Object value);

        long asLong(Type type, @Nullable Object value);

        float asFloat(Type type, @Nullable Object value);

        double asDouble(Type type, @Nullable Object value);

        char asChar(Type type, @Nullable Object value);

        @Nullable
        String asString(Type type, @Nullable Object value);

        @Nullable
        LocalTime asTime(Type type, @Nullable Object value);
    }

    enum Type {
        NULL,
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        CHAR,
        STRING,
        TIME
    }
}
