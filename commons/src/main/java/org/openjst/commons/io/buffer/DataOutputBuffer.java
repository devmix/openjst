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
public interface DataOutputBuffer {

    int writeInt8(byte value) throws DataBufferException;

    int writeInt16(short value) throws DataBufferException;

    int writeInt32(int value) throws DataBufferException;

    int writeInt64(long value) throws DataBufferException;

    int writeFloat32(float value) throws DataBufferException;

    int writeFloat64(double value) throws DataBufferException;

    int writeBoolean8(boolean value) throws DataBufferException;

    int writeBytes(byte[] value) throws DataBufferException;

    int writeUtf8(String value) throws DataBufferException;

    int writeObject(Object value) throws DataBufferException;

    int writeVLQInt32(int value) throws DataBufferException;

    int writeVLQInt64(long value) throws DataBufferException;

    int writeVLQFloat32(float value) throws DataBufferException;

    int writeVLQFloat64(double value) throws DataBufferException;

    void skip(int length) throws DataBufferException;
}
