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

import org.openjst.commons.io.buffer.DataBufferException;
import org.openjst.protocols.basic.pdu.beans.Parameter;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public class AuthResponsePacketTest {

    @Test(groups = "unit")
    public void encodeDecode() throws DataBufferException {
        final Set<Parameter> parameters = new HashSet<Parameter>();
        parameters.add(Parameter.newParameter(1, "1"));
        parameters.add(Parameter.newParameter((byte) 1, (byte) 2));

        final AuthResponsePacket packet = PacketsFactory.newAuthResponsePacket(
                (long) (Math.random() * Long.MAX_VALUE),
                (int) (Math.random() * Short.MAX_VALUE),
                parameters);

        final byte[] body = packet.encode();

        final AuthResponsePacket packet2 = new AuthResponsePacket();
        packet2.decode(body);

        assertThat(packet.getType()).isEqualTo(packet2.getType());
        assertThat(packet.getPacketId()).isEqualTo(packet2.getPacketId());
        assertThat(packet.getResponseStatus()).isEqualTo(packet2.getResponseStatus());

        final Set<Parameter> parameters2 = packet2.getParameters();
        assertThat(parameters2).isNotNull();
        //noinspection ConstantConditions
        assertThat(parameters2.size()).isEqualTo(parameters.size());
    }
}
