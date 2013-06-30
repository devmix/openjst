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

package org.openjst.commons.io.buffer;

/**
 * @author Sergey Grachev
 */
public final class DataBufferException extends Exception {

    private static final long serialVersionUID = 3167081666045375248L;

    private DataBufferException(final String message) {
        super(message);
    }

    public DataBufferException(final Throwable cause) {
        super(cause);
    }

    public static DataBufferException newNotEnoughSpaceForWrite(final int capacity, final int used, final int size) {
        return new DataBufferException(String.format("Not enough space for write: capacity %d, used %d, required %d", capacity, used, size));
    }

    public static DataBufferException newNested(final Exception e) {
        return new DataBufferException(e);
    }

    public static DataBufferException newObjectCanNotBeSerialized(final Object obj) {
        return new DataBufferException(String.format("Object '%s' can't be serialized", obj));
    }

    public static DataBufferException newNotEnoughDataForRead(final int index, final int bufferSize, final int size) {
        return new DataBufferException(String.format("Not enough data for read: current index %d, buffer size %d, required %d", index, bufferSize, size));
    }

    public static DataBufferException newMalformedPackedInt() {
        return new DataBufferException(String.format("Malformed packed integer"));
    }
}
