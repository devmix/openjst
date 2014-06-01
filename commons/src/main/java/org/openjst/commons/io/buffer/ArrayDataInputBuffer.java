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

import org.openjst.commons.utils.Constants;
import org.openjst.commons.utils.IOUtils;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Byte ordering big-endian
 *
 * @author Sergey Grachev
 */
public class ArrayDataInputBuffer implements DataInputBuffer {

    private final byte[] buffer;
    private final int bufferSize;
    private int offset;

    public ArrayDataInputBuffer(final byte[] buffer) {
        this(buffer, 0);
    }

    public ArrayDataInputBuffer(final byte[] buffer, final int offset) {
        this(buffer, offset, buffer.length);
    }

    public ArrayDataInputBuffer(final byte[] buffer, final int offset, final int length) {
        this.buffer = Arrays.copyOf(buffer, buffer.length);
        this.offset = offset;
        this.bufferSize = Math.min(buffer.length, offset + length);
    }

    public byte readInt8() throws DataBufferException {
        ensureInAvailable(1);
        return (byte) (buffer[offset++] & 0xFF);
    }

    public short readInt16() throws DataBufferException {
        ensureInAvailable(2);
        return (short) (((buffer[offset++] & 0xFF) << 8) + (buffer[offset++] & 0xFF));
    }

    public int readInt32() throws DataBufferException {
        ensureInAvailable(4);
        return (((buffer[offset++] & 0xFF) << 24) + ((buffer[offset++] & 0xFF) << 16) + ((buffer[offset++] & 0xFF) << 8) + (buffer[offset++] & 0xFF));
    }

    public long readUInt32() throws DataBufferException {
        return ((buffer[offset++] << 24) + (buffer[offset++] << 16) + (buffer[offset++] << 8) + buffer[offset++]);
    }

    public long readInt64() throws DataBufferException {
        ensureInAvailable(8);
        return (((long) buffer[offset++] << 56) +
                ((long) (buffer[offset++] & 255) << 48) +
                ((long) (buffer[offset++] & 255) << 40) +
                ((long) (buffer[offset++] & 255) << 32) +
                ((long) (buffer[offset++] & 255) << 24) +
                ((buffer[offset++] & 255) << 16) +
                ((buffer[offset++] & 255) << 8) +
                (buffer[offset++] & 255));
    }

    public float readFloat32() throws DataBufferException {
        return Float.intBitsToFloat(readInt32());
    }

    public double readFloat64() throws DataBufferException {
        return Double.longBitsToDouble(readInt64());
    }

    public boolean readBoolean8() throws DataBufferException {
        return readInt8() != 0;
    }

    public byte[] readBytes(final int length) throws DataBufferException {
        if (length == 0) {
            return Constants.arraysEmptyBytes();
        }

        ensureInAvailable(length);

        final byte[] result = new byte[length];
        System.arraycopy(buffer, offset, result, 0, length);

        offset += length;

        return result;
    }

    @Nullable
    public String readUtf8() throws DataBufferException {
        final int length = readInt32();
        if (length == 0) {
            return null;
        }

        ensureInAvailable(length);

        final String result = IOUtils.readUtf8(buffer, offset, length);
        offset += length;
        return result;
    }

    public Object readObject() throws DataBufferException {
        final int length = readInt32();

        ensureInAvailable(length);

        final Object object = IOUtils.readObject(buffer, offset, length);
        offset += length;
        return object;
    }

    public int readVLQInt32() throws DataBufferException {
        byte tmp = readInt8();
        if (tmp >= 0) {
            return tmp;
        }
        int result = tmp & 0x7f;
        if ((tmp = readInt8()) >= 0) {
            result |= tmp << 7;
        } else {
            result |= (tmp & 0x7f) << 7;
            if ((tmp = readInt8()) >= 0) {
                result |= tmp << 14;
            } else {
                result |= (tmp & 0x7f) << 14;
                if ((tmp = readInt8()) >= 0) {
                    result |= tmp << 21;
                } else {
                    result |= (tmp & 0x7f) << 21;
                    result |= (tmp = readInt8()) << 28;
                    if (tmp < 0) {
                        // Discard upper 32 bits.
                        for (int i = 0; i < 5; i++) {
                            if (readInt8() >= 0) {
                                return result;
                            }
                        }
                        throw DataBufferException.newMalformedPackedInt();
                    }
                }
            }
        }
        return result;
    }

    public long readVLQInt64() throws DataBufferException {
        int shift = 0;
        long result = 0;
        while (shift < 64) {
            final byte b = readInt8();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
            shift += 7;
        }
        throw DataBufferException.newMalformedPackedInt();
    }

    public float readVLQFloat32() throws DataBufferException {
        return Float.intBitsToFloat(readVLQInt32());
    }

    public double readVLQFloat64() throws DataBufferException {
        return Double.longBitsToDouble(readVLQInt64());
    }

    private void ensureInAvailable(final int size) throws DataBufferException {
        if (offset + size > bufferSize) {
            throw DataBufferException.newNotEnoughDataForRead(offset, bufferSize, size);
        }
    }
}
