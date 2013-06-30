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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Sergey Grachev
 */
public class PacketInputBuffer {
    private DataInputStream dataStream;

    public PacketInputBuffer(final byte[] data) {
        this.dataStream = new DataInputStream(new ByteArrayInputStream(data));
    }

    public String readUTF() throws IOException {
        return dataStream.readUTF();
    }

    public byte readByte() throws IOException {
        return dataStream.readByte();
    }

    public long readLong() throws IOException {
        return dataStream.readLong();
    }

    public short readShort() throws IOException {
        return dataStream.readShort();
    }
}
