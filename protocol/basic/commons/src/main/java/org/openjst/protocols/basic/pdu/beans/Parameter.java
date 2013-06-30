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

package org.openjst.protocols.basic.pdu.beans;

import org.jetbrains.annotations.Nullable;

/**
 * @author Sergey Grachev
 */
public final class Parameter<K, V> {

    public final K key;
    public final V value;

    public Parameter(final K key, final V value) {
        if (Type.UNKNOWN.equals(Type.valueOfObject(key))) {
            throw new IllegalArgumentException("Unsupported class of key " + key);
        }

        if (Type.UNKNOWN.equals(Type.valueOfObject(value))) {
            throw new IllegalArgumentException("Unsupported class of value " + key);
        }

        this.key = key;
        this.value = value;
    }

    public static enum Type {
        UNKNOWN(-1),
        // byte,
        INT8(0),
        // short
        INT16(1),
        // int
        INT32(2),
        // long
        INT64(3),
        // float
        FLOAT32(4),
        // double
        FLOAT64(5),
        // boolean
        BOOLEAN(6),
        // string
        STRING(7);

        private final int tag;

        Type(final int tag) {
            this.tag = tag;
        }

        public int getTag() {
            return tag;
        }

        public static Type valueOfObject(@Nullable final Object o) {
            if (o == null) {
                return UNKNOWN;
            }
            final Class clazz = o.getClass();
            if (clazz == Long.class || clazz == long.class) {
                return INT64;
            } else if (clazz == Integer.class || clazz == int.class) {
                return INT32;
            } else if (clazz == Short.class || clazz == short.class) {
                return INT16;
            } else if (clazz == Byte.class || clazz == byte.class) {
                return INT8;
            } else if (clazz == Float.class || clazz == float.class) {
                return FLOAT32;
            } else if (clazz == Double.class || clazz == double.class) {
                return FLOAT64;
            } else if (clazz == Boolean.class || clazz == boolean.class) {
                return BOOLEAN;
            } else if (clazz == String.class) {
                return STRING;
            }
            return UNKNOWN;
        }

        public static Type valueOfTag(final byte tag) {
            switch (tag) {
                case 0:
                    return INT8;
                case 1:
                    return INT16;
                case 2:
                    return INT32;
                case 3:
                    return INT64;
                case 4:
                    return FLOAT32;
                case 5:
                    return FLOAT64;
                case 6:
                    return BOOLEAN;
                case 7:
                    return STRING;
            }
            return UNKNOWN;
        }
    }
}
