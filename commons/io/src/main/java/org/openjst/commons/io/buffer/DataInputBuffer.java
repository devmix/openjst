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
public interface DataInputBuffer {

    byte readInt8() throws DataBufferException;

    short readInt16() throws DataBufferException;

    int readInt32() throws DataBufferException;

    long readUInt32() throws DataBufferException;

    long readInt64() throws DataBufferException;

    float readFloat32() throws DataBufferException;

    double readFloat64() throws DataBufferException;

    boolean readBoolean8() throws DataBufferException;

    byte[] readBytes(int length) throws DataBufferException;

    String readUtf8() throws DataBufferException;

    Object readObject() throws DataBufferException;

    int readVLQInt32() throws DataBufferException;

    long readVLQInt64() throws DataBufferException;

    float readVLQFloat32() throws DataBufferException;

    double readVLQFloat64() throws DataBufferException;
}
