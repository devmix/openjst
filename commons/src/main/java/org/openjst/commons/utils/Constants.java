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

package org.openjst.commons.utils;

/**
 * @author Sergey Grachev
 */
public final class Constants {

    private static final byte[] ARRAYS_EMPTY_BYTES = new byte[0];
    private static final Class[] ARRAYS_EMPTY_CLASSES = new Class[0];
    private static final Object[] ARRAYS_EMPTY_OBJECTS = new Object[0];
    private static final String STRING_EMPTY = "";
    private static final Object OBJECT = new Object();

    private Constants() {
    }

    public static byte[] arraysEmptyBytes() {
        return ARRAYS_EMPTY_BYTES;
    }

    public static String stringsEmpty() {
        return STRING_EMPTY;
    }

    public static Class[] arraysEmptyClasses() {
        return ARRAYS_EMPTY_CLASSES;
    }

    public static Object[] arraysEmptyObjects() {
        return ARRAYS_EMPTY_OBJECTS;
    }

    public static Object object() {
        return OBJECT;
    }
}
