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

import org.openjst.commons.conversion.encodings.VLQCode;
import org.openjst.commons.dto.constants.Constants;
import org.openjst.commons.io.utils.IOUtils;

import java.util.Arrays;

/**
 * Byte ordering big-endian
 *
 * @author Sergey Grachev
 */
public class ArrayDataOutputBuffer implements DataOutputBuffer {

    private static final int ENLARGE_BUFFER_BY = 16;

    private final int capacity;
    private byte[] buffer;
    private int offset;

    public ArrayDataOutputBuffer() {
        this.buffer = null;
        this.capacity = -1;
    }

    public ArrayDataOutputBuffer(final byte[] buffer) {
        this.buffer = buffer;
        this.capacity = buffer.length;
    }

    public ArrayDataOutputBuffer(final int capacity) {
        this.buffer = null;
        this.capacity = capacity;
    }

    public int writeInt8(final byte value) throws DataBufferException {
        ensureOutAvailable(1);
        buffer[offset++] = value;
        return 1;
    }

    public int writeInt16(final short value) throws DataBufferException {
        ensureOutAvailable(2);
        buffer[offset++] = (byte) ((value >>> 8) & 0xFF);
        buffer[offset++] = (byte) (value & 0xFF);
        return 2;
    }

    public int writeInt32(final int value) throws DataBufferException {
        ensureOutAvailable(4);
        buffer[offset++] = (byte) (value >>> 24);
        buffer[offset++] = (byte) (value >>> 16);
        buffer[offset++] = (byte) (value >>> 8);
        buffer[offset++] = (byte) value;
        return 4;
    }

    public int writeInt64(final long value) throws DataBufferException {
        ensureOutAvailable(8);
        buffer[offset++] = (byte) (value >>> 56);
        buffer[offset++] = (byte) (value >>> 48);
        buffer[offset++] = (byte) (value >>> 40);
        buffer[offset++] = (byte) (value >>> 32);
        buffer[offset++] = (byte) (value >>> 24);
        buffer[offset++] = (byte) (value >>> 16);
        buffer[offset++] = (byte) (value >>> 8);
        buffer[offset++] = (byte) value;
        return 8;
    }

    public int writeFloat32(final float value) throws DataBufferException {
        return writeInt32(Float.floatToIntBits(value));
    }

    public int writeFloat64(final double value) throws DataBufferException {
        return writeInt64(Double.doubleToLongBits(value));
    }

    public int writeBoolean8(final boolean value) throws DataBufferException {
        return writeInt8((byte) (value ? 1 : 0));
    }

    public int writeBytes(final byte[] value) throws DataBufferException {
        if (value.length == 0) {
            return 0;
        }

        ensureOutAvailable(value.length);

        System.arraycopy(value, 0, buffer, offset, value.length);

        offset += value.length;

        return value.length;
    }

    public int writeUtf8(final String value) throws DataBufferException {
        final int requiredSize = IOUtils.sizeOfWriteByteArray(value);

        ensureOutAvailable(4 + requiredSize);

        offset += IOUtils.writeUtf8(value, requiredSize, buffer, offset);

        return 4 + requiredSize;
    }

    public int writeObject(final Object value) throws DataBufferException {
        final byte[] data = IOUtils.writeObject(value);

        ensureOutAvailable(4 + data.length);

        writeInt32(data.length);
        writeBytes(data);

        return 4 + data.length;
    }

    public int writeVLQInt32(final int value) throws DataBufferException {
        final byte size = VLQCode.sizeOf(value);
        ensureOutAvailable(size);
        offset += VLQCode.encode(value, buffer, offset);
        return size;
    }

    public int writeVLQInt64(final long value) throws DataBufferException {
        final byte size = VLQCode.sizeOf(value);
        ensureOutAvailable(size);
        offset += VLQCode.encode(value, buffer, offset);
        return size;
    }

    public int writeVLQFloat32(final float value) throws DataBufferException {
        return writeVLQInt32(Float.floatToIntBits(value));
    }

    public int writeVLQFloat64(final double value) throws DataBufferException {
        return writeVLQInt64(Double.doubleToLongBits(value));
    }

    public void skip(final int length) throws DataBufferException {
        ensureOutAvailable(length);
        offset += length;
    }

    public byte[] toByteArray() {
        if (buffer == null) {
            return Constants.arraysEmptyBytes();
        }

        if (capacity < 0) {
            return Arrays.copyOf(buffer, offset);
        }

        return buffer;
    }

    private void ensureOutAvailable(final int size) throws DataBufferException {
        final int newUsedSpace = offset + size;
        if (newUsedSpace > outMaxCapacity()) {
            throw DataBufferException.newNotEnoughSpaceForWrite(capacity, offset, size);
        }

        if (buffer == null) {
            buffer = new byte[newUsedSpace];
        } else if (newUsedSpace > buffer.length) {
            buffer = Arrays.copyOf(buffer, Math.max(offset + ENLARGE_BUFFER_BY, newUsedSpace));
        }
    }

    private int outMaxCapacity() {
        return capacity > -1 ? capacity : Integer.MAX_VALUE;
    }
}
