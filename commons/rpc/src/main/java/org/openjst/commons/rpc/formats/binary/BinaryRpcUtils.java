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

package org.openjst.commons.rpc.formats.binary;

/**
 * @author Sergey Grachev
 */
final class BinaryRpcUtils {

    public static final int TAG_RESERVED_MIN = 0;
    public static final int TAG_RESERVED_CLOSE = 1;
    public static final int TAG_RESERVED_MAX = 16;

    public static final int TAG_REQUEST = 17;
    public static final int TAG_RESPONSE = 18;
    public static final int TAG_RESPONSE_ERROR = 19;
    public static final int TAG_OBJECT = 20;
    public static final int TAG_METHOD = 21;
    public static final int TAG_PARAMETERS = 22;
    public static final int TAG_ERROR = 23;
    public static final int TAG_ERROR_CODE = 24;
    public static final int TAG_ERROR_STRING = 25;
    public static final int TAG_ERROR_OBJECT = 26;

    public static final int TAG_VALUE_NIL = 50;
    public static final int TAG_VALUE_FLOAT32 = 51;
    public static final int TAG_VALUE_FLOAT64 = 52;
    public static final int TAG_VALUE_INT8 = 53;
    public static final int TAG_VALUE_INT16 = 54;
    public static final int TAG_VALUE_INT32 = 55;
    public static final int TAG_VALUE_INT64 = 56;
    public static final int TAG_VALUE_BOOLEAN8 = 57;
    public static final int TAG_VALUE_UTF8 = 58;
    public static final int TAG_VALUE_DATE = 59;
    public static final int TAG_VALUE_ARRAY = 60;
    public static final int TAG_VALUE_BINARY = 61;
    public static final int TAG_VALUE_MAP = 62;
    public static final int TAG_VALUE_MAP_ENTRY = 63;
    public static final int TAG_VALUE_MAP_VALUE = 64;
    public static final int TAG_VALUE_MAP_KEY = 65;

    private BinaryRpcUtils() {
    }
}
