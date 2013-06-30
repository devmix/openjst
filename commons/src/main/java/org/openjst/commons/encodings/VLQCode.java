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

package org.openjst.commons.encodings;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Sergey Grachev
 */
public final class VLQCode {

    private VLQCode() {
    }

    public static byte sizeOf(final int value) {
        if ((value & (0xffffffff << 7)) == 0) {
            return 1;
        }
        if ((value & (0xffffffff << 14)) == 0) {
            return 2;
        }
        if ((value & (0xffffffff << 21)) == 0) {
            return 3;
        }
        if ((value & (0xffffffff << 28)) == 0) {
            return 4;
        }
        return 5;
    }

    public static int encode(final int value, final byte[] buffer, final int offset) {
        if (buffer == null || buffer.length == 0) {
            throw new IllegalArgumentException("Destination buffer is null or empty");
        }

        if (offset > buffer.length) {
            throw new IllegalArgumentException("Destination index is greater that length of buffer");
        }

        int index = offset;
        int temp = value;
        while (true) {
            if ((temp & ~0x7F) == 0) {
                buffer[index++] = (byte) temp;
                break;
            }
            buffer[index++] = (byte) ((temp & 0x7F) | 0x80);
            temp >>>= 7;
        }

        return index - offset;
    }

    public static int encode(final int value, final OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream is null");
        }

        int count = 0;
        int temp = value;
        while (true) {
            if ((temp & ~0x7F) == 0) {
                outputStream.write(temp);
                count++;
                break;
            }
            outputStream.write((temp & 0x7F) | 0x80);
            count++;
            temp >>>= 7;
        }

        return count;
    }

    public static byte sizeOf(final long value) {
        if ((value & (0xffffffffffffffffL << 7)) == 0) {
            return 1;
        }
        if ((value & (0xffffffffffffffffL << 14)) == 0) {
            return 2;
        }
        if ((value & (0xffffffffffffffffL << 21)) == 0) {
            return 3;
        }
        if ((value & (0xffffffffffffffffL << 28)) == 0) {
            return 4;
        }
        if ((value & (0xffffffffffffffffL << 35)) == 0) {
            return 5;
        }
        if ((value & (0xffffffffffffffffL << 42)) == 0) {
            return 6;
        }
        if ((value & (0xffffffffffffffffL << 49)) == 0) {
            return 7;
        }
        if ((value & (0xffffffffffffffffL << 56)) == 0) {
            return 8;
        }
        if ((value & (0xffffffffffffffffL << 63)) == 0) {
            return 9;
        }
        return 10;
    }

    public static int encode(final long value, final byte[] buffer, final int offset) {
        if (buffer == null || buffer.length == 0) {
            throw new IllegalArgumentException("Destination buffer is null or empty");
        }

        if (offset > buffer.length) {
            throw new IllegalArgumentException("Destination index is greater that length of buffer");
        }

        int index = offset;
        long temp = value;
        while (true) {
            if ((temp & ~0x7FL) == 0) {
                buffer[index++] = (byte) temp;
                break;
            }
            buffer[index++] = (byte) (((int) temp & 0x7F) | 0x80);
            temp >>>= 7;
        }
        return index - offset;
    }

    public static int encode(final long value, final OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream is null");
        }

        int count = 0;
        long temp = value;
        while (true) {
            if ((temp & ~0x7F) == 0) {
                outputStream.write((int) temp);
                count++;
                break;
            }
            outputStream.write((int) ((temp & 0x7F) | 0x80));
            count++;
            temp >>>= 7;
        }

        return count;
    }
}
