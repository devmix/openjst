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

package org.openjst.protocols.basic.pdu.packets;

import org.openjst.protocols.basic.pdu.beans.Parameter;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
@Test
public class AuthResponsePacketTest {
    public void encodeDecode() throws IOException {
        final List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter<Integer, String>(1, "1"));
        parameters.add(new Parameter<Byte, Byte>((byte) 1, (byte) 2));

        final AuthResponsePacket packet = PacketsFactory.newAuthResponsePacket(
                (long) (Math.random() * Long.MAX_VALUE),
                "account" + (short) (Math.random() * Short.MAX_VALUE),
                "client" + (int) (Math.random() * Short.MAX_VALUE),
                (int) (Math.random() * Short.MAX_VALUE),
                parameters);

        final byte[] body = packet.encode();

        final AuthResponsePacket packet2 = new AuthResponsePacket();
        packet2.decode(body);

        assertThat(packet.getType()).isEqualTo(packet2.getType());
        assertThat(packet.getRequestId()).isEqualTo(packet2.getRequestId());
        assertThat(packet.getAccountId()).isEqualTo(packet2.getAccountId());
        assertThat(packet.getClientId()).isEqualTo(packet2.getClientId());
        assertThat(packet.getErrorCode()).isEqualTo(packet2.getErrorCode());

        final List<Parameter> parameters2 = packet2.getParameters();
        assertThat(parameters2).isNotNull();
        //noinspection ConstantConditions
        assertThat(parameters2.size()).isEqualTo(parameters.size());
    }
}
