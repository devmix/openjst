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

package org.openjst.protocols.basic.pdu;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Sergey Grachev
 */
public class PacketOutputBuffer {
    private DataOutputStream dataStream;
    private ByteArrayOutputStream bytesStream;

    public PacketOutputBuffer() {
        this.bytesStream = new ByteArrayOutputStream();
        this.dataStream = new DataOutputStream(this.bytesStream);
    }

    public byte[] toByteArray() {
        return bytesStream.toByteArray();
    }

    public void write(final byte[] data) {
        try {
            dataStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeByte(final int value) {
        try {
            dataStream.writeByte(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInt(final int value) {
        try {
            dataStream.writeInt(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeUTF(final String value) {
        try {
            dataStream.writeUTF(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLong(final long value) {
        try {
            dataStream.writeLong(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeShort(final short value) {
        try {
            dataStream.writeShort(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
