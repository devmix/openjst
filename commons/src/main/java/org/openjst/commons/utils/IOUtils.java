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

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.io.buffer.DataBufferException;

import java.io.*;

/**
 * @author Sergey Grachev
 */
public final class IOUtils {

    private IOUtils() {
    }

    public static int sizeOfWriteByteArray(final String value) throws DataBufferException {
        if (value == null || value.length() == 0) {
            return 0;
        }

        int resultLength = 0;
        for (int i = 0, length = value.length(); i < length; i++) {
            final int c = value.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                resultLength++;
            } else if (c > 0x07FF) {
                resultLength += 3;
            } else {
                resultLength += 2;
            }
        }

        return resultLength;
    }

    public static int writeUtf8(final String value, final int sizeAsByteArray, final byte[] buffer, final int offset) throws DataBufferException {
        int index = offset;
        buffer[index++] = (byte) ((sizeAsByteArray >>> 24) & 0xFF);
        buffer[index++] = (byte) ((sizeAsByteArray >>> 16) & 0xFF);
        buffer[index++] = (byte) ((sizeAsByteArray >>> 8) & 0xFF);
        buffer[index++] = (byte) (sizeAsByteArray & 0xFF);

        if (value == null || value.length() == 0) {
            return 4;
        }

        final int stringLength = value.length();
        int i;
        for (i = 0; i < stringLength; i++) {
            final int c = value.charAt(i);
            if (!((c >= 0x0001) && (c <= 0x007F))) {
                break;
            }
            buffer[index++] = (byte) c;
        }

        for (; i < stringLength; i++) {
            final int c = value.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                buffer[index++] = (byte) c;

            } else if (c > 0x07FF) {
                buffer[index++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                buffer[index++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                buffer[index++] = (byte) (0x80 | (c & 0x3F));
            } else {
                buffer[index++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                buffer[index++] = (byte) (0x80 | (c & 0x3F));
            }
        }

        return index - offset;
    }

    /**
     * Read UTF-8 string
     */
    public static String readUtf8(final byte[] buffer, final int offset, final int length) throws DataBufferException {
        final char[] result = new char[length];
        int realSize = 0;

        final int offsetEnd = Math.min(buffer.length, offset + length);
        int index = offset;

        int c;
        while (index < offsetEnd) {
            c = (int) buffer[index] & 0xFF;
            if (c > 127) {
                break;
            }
            index++;
            result[realSize++] = (char) c;
        }

        int char2, char3;
        while (index < offsetEnd) {
            c = (int) buffer[index] & 0xFF;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7: {
                    /* 0xxxxxxx*/
                    index++;
                    result[realSize++] = (char) c;
                    break;
                }
                case 12:
                case 13: {
                    /* 110x xxxx   10xx xxxx*/
                    index += 2;
                    if (index > offsetEnd) {
                        throw DataBufferException.newNested(new UTFDataFormatException("malformed input: partial character at end"));
                    }

                    char2 = (int) buffer[index - 1];
                    if ((char2 & 0xC0) != 0x80) {
                        throw DataBufferException.newNested(new UTFDataFormatException("malformed input around byte " + index));
                    }

                    result[realSize++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
                    break;
                }
                case 14: {
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    index += 3;
                    if (index > offsetEnd) {
                        throw DataBufferException.newNested(new UTFDataFormatException("malformed input: partial character at end"));
                    }
                    char2 = (int) buffer[index - 2];
                    char3 = (int) buffer[index - 1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
                        throw DataBufferException.newNested(new UTFDataFormatException("malformed input around byte " + (index - 1)));
                    }
                    result[realSize++] = (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | (char3 & 0x3F));
                    break;
                }
                default: {
                    /* 10xx xxxx,  1111 xxxx */
                    throw DataBufferException.newNested(new UTFDataFormatException("malformed input around byte " + index));
                }
            }
        }

        return new String(result, 0, realSize);
    }

    public static byte[] writeObject(final Object value) throws DataBufferException {
        if (!(value instanceof Serializable)) {
            throw DataBufferException.newObjectCanNotBeSerialized(value);
        }

        final byte[] data;
        try {
            final ByteArrayOutputStream temp = new ByteArrayOutputStream();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(temp);
            objectOutputStream.writeObject(value);
            data = temp.toByteArray();
        } catch (IOException e) {
            throw DataBufferException.newNested(e);
        }

        return data;
    }

    @Nullable
    public static Object readObject(final byte[] buffer, final int offset, final int length) throws DataBufferException {
        if (buffer.length == 0) {
            return null;
        }

        try {
            final ByteArrayInputStream temp = new ByteArrayInputStream(buffer, offset, length);
            final ObjectInputStream stream = new ObjectInputStream(temp);
            return stream.readObject();
        } catch (IOException e) {
            throw DataBufferException.newNested(e);
        } catch (ClassNotFoundException e) {
            throw DataBufferException.newNested(e);
        }
    }

    /**
     * See <a href="https://developers.google.com/protocol-buffers/docs/overview">Google, Protocol Buffers, Encode</a>
     */
    public static int encodeZigZag(final int value) {
        return (value << 1) ^ (value >> 31);
    }

    /**
     * See <a href="https://developers.google.com/protocol-buffers/docs/overview">Google, Protocol Buffers, Encode</a>
     */
    public static long encodeZigZag(final long value) {
        return (value << 1) ^ (value >> 63);
    }
}
