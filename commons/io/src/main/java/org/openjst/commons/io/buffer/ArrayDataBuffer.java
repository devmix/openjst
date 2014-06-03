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
 * @author Sergey Grachev
 */
public class ArrayDataBuffer implements DataBuffer {

    private static final int ENLARGE_BUFFER_BY = 16;

    private final int bufferSize;
    private final int startOffset;
    private byte[] buffer;
    private int offset;
    private int endOffset;

    public ArrayDataBuffer() {
        this(null, 0, Integer.MAX_VALUE);
    }

    public ArrayDataBuffer(final byte[] buffer) {
        this(buffer, 0);
    }

    public ArrayDataBuffer(final byte[] buffer, final int offset) {
        this(buffer, offset, buffer == null ? Integer.MAX_VALUE : buffer.length);
    }

    public ArrayDataBuffer(final byte[] buffer, final int offset, final int length) {
        this.startOffset = offset;
        this.offset = offset;
        if (buffer != null) {
            this.buffer = Arrays.copyOf(buffer, buffer.length);
            this.endOffset = Math.min(buffer.length, offset + length);
            this.bufferSize = this.endOffset;
        } else {
            this.endOffset = 0;
            this.bufferSize = -1;
        }
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

    public int writeInt8(final byte value) throws DataBufferException {
        ensureOutAvailable(1);
        buffer[offset++] = value;
        updateEndOffset();
        return 1;
    }

    public int writeInt16(final short value) throws DataBufferException {
        ensureOutAvailable(2);
        buffer[offset++] = (byte) ((value >>> 8) & 0xFF);
        buffer[offset++] = (byte) (value & 0xFF);
        updateEndOffset();
        return 2;
    }

    public int writeInt32(final int value) throws DataBufferException {
        ensureOutAvailable(4);
        buffer[offset++] = (byte) (value >>> 24);
        buffer[offset++] = (byte) (value >>> 16);
        buffer[offset++] = (byte) (value >>> 8);
        buffer[offset++] = (byte) value;
        updateEndOffset();
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
        updateEndOffset();
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
        updateEndOffset();

        return value.length;
    }

    public int writeUtf8(final String value) throws DataBufferException {
        final int requiredSize = IOUtils.sizeOfWriteByteArray(value);

        ensureOutAvailable(4 + requiredSize);

        offset += IOUtils.writeUtf8(value, requiredSize, buffer, offset);
        updateEndOffset();

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
        updateEndOffset();
        return size;
    }

    public int writeVLQInt64(final long value) throws DataBufferException {
        final byte size = VLQCode.sizeOf(value);
        ensureOutAvailable(size);
        offset += VLQCode.encode(value, buffer, offset);
        updateEndOffset();
        return size;
    }

    public int writeVLQFloat32(final float value) throws DataBufferException {
        return writeVLQInt32(Float.floatToIntBits(value));
    }

    public int writeVLQFloat64(final double value) throws DataBufferException {
        return writeVLQInt64(Double.doubleToLongBits(value));
    }

    public void skip(final int length) throws DataBufferException {
        seek(offset + length);
    }

    public int getOffset() {
        return offset - startOffset;
    }

    public int seek(final int offset) {
        this.offset = Math.max(startOffset, startOffset + offset);
        return this.offset;
    }

    public int seekEnd() {
        offset = endOffset;
        return offset;
    }

    public int getSize() {
        return endOffset - startOffset;
    }

    public byte[] toByteArray() {
        if (buffer == null) {
            return Constants.arraysEmptyBytes();
        }

        if (this.endOffset + 1 == buffer.length) {
            return buffer;
        }
        return Arrays.copyOf(buffer, this.endOffset);
    }

    private void ensureInAvailable(final int size) throws DataBufferException {
        if (offset + size > inMaxCapacity()) {
            throw DataBufferException.newNotEnoughDataForRead(offset, endOffset, size);
        }
    }

    private void ensureOutAvailable(final int size) throws DataBufferException {
        final int newUsedSpace = offset + size;
        if (newUsedSpace > outMaxCapacity()) {
            throw DataBufferException.newNotEnoughSpaceForWrite(bufferSize, offset, size);
        }

        if (buffer == null) {
            buffer = new byte[newUsedSpace];
        } else if (newUsedSpace > buffer.length) {
            buffer = Arrays.copyOf(buffer, Math.max(offset + ENLARGE_BUFFER_BY, newUsedSpace));
        }
    }

    private int outMaxCapacity() {
        return bufferSize > -1 ? bufferSize : Integer.MAX_VALUE;
    }

    private int inMaxCapacity() {
        return bufferSize > -1 ? bufferSize : endOffset + 1;
    }

    private void updateEndOffset() {
        if (offset > endOffset) {
            endOffset = offset;
        }
    }
}
