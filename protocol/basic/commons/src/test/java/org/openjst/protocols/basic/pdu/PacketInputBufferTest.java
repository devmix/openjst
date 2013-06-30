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

import org.testng.annotations.Test;

/**
 * @author Sergey Grachev
 */
@Test
public class PacketInputBufferTest {

    public void test() {
        final PacketInputBuffer inputBuffer = new PacketInputBuffer(new byte[]{
                0x32, // byte
                0x01, 0x03, // short
                0x04, 0x05, 0x06, 0x07, // int
                0x08, 0x09, 0x01, 0x02, 0x01, 0x03, 0x04, 0x05 // long
        });

    }
}
