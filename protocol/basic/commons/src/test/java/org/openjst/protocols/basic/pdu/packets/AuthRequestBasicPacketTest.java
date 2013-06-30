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

import org.openjst.commons.protocols.auth.SecretKeys;
import org.openjst.protocols.basic.pdu.beans.Parameter;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class AuthRequestBasicPacketTest {

    @Test(groups = "unit")
    public void testEncodeDecode() throws IOException {
        final List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter<Integer, String>(1, "1"));
        parameters.add(new Parameter<Byte, Byte>((byte) 1, (byte) 2));

        final AuthRequestBasicPacket packet = PacketsFactory.newAuthRequestBasicPacket(
                (long) (Math.random() * Long.MAX_VALUE),
                "account" + UUID.randomUUID().toString(),
                "client" + UUID.randomUUID().toString(),
                SecretKeys.PLAIN.encode("pass" + UUID.randomUUID().toString()),
                parameters
        );

        final byte[] body = packet.encode();

        final AuthRequestBasicPacket packet2 = new AuthRequestBasicPacket();
        packet2.decode(body);

        assertThat(packet2.getType()).isEqualTo(packet.getType());
        assertThat(packet2.getRequestId()).isEqualTo(packet.getRequestId());
        assertThat(packet2.getAccountId()).isEqualTo(packet.getAccountId());
        assertThat(packet2.getClientId()).isEqualTo(packet.getClientId());
        assertThat(packet2.getSecretKey()).isEqualTo(packet.getSecretKey());

        final List<Parameter> parameters2 = packet2.getParameters();
        assertThat(parameters2).isNotNull();
        //noinspection ConstantConditions
        assertThat(parameters2.size()).isEqualTo(parameters.size());
    }
}
