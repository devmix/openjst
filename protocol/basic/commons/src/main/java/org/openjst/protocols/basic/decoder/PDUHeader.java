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

package org.openjst.protocols.basic.decoder;

/**
 * @author Sergey Grachev
 */
public class PDUHeader {
    private byte version = 0; // 1 byte
    private short flags = 0; // 2 byte, reserved
    private short type = 0; // 2 byte
    private int length = 0; // 4 byte
    // RESERVED - 5 bytes, reserved
    private short crc16 = 0; // 2 byte

    public byte getVersion() {
        return version;
    }

    public short getFlags() {
        return flags;
    }

    public short getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public short getCRC16() {
        return crc16;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public void setFlags(short flags) {
        this.flags = flags;
    }

    public void setType(short type) {
        this.type = type;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setCRC16(short crc16) {
        this.crc16 = crc16;
    }
}
