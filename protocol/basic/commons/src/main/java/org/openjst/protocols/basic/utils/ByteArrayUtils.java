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

package org.openjst.protocols.basic.utils;

/**
 * @author Sergey Grachev
 */
public final class ByteArrayUtils {

    private ByteArrayUtils() {
    }

    public static byte[] fromLong(final long value) {
        return new byte[]{
                (byte) (value >>> 56),
                (byte) (value >>> 48),
                (byte) (value >>> 40),
                (byte) (value >>> 32),
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    public static byte[] fromInt(final int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    public static byte[] fromShort(final short value) {
        return new byte[]{
                (byte) (value >>> 8),
                (byte) value};
    }

    public static long toLong(final byte[] value) {
        return (long) value[0] << 56 |
                (long) value[1] << 48 |
                (long) value[2] << 40 |
                (long) value[3] << 32 |
                (long) value[4] << 24 |
                (long) value[5] << 16 |
                (long) value[6] << 8 |
                (long) value[7];
    }
}
