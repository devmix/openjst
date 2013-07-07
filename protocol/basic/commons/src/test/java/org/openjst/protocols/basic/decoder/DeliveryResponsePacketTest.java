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

import org.jboss.netty.buffer.ChannelBuffer;
import org.openjst.protocols.basic.encoder.ProtocolEncoder;
import org.openjst.protocols.basic.pdu.packets.DeliveryResponsePacket;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;
import org.testng.annotations.Test;

/**
 * @author Sergey Grachev
 */
public final class DeliveryResponsePacketTest {

    @Test(groups = "unit")
    public void testEncodeDecode() throws Exception {
        for (int i = 0; i < 1000000; i++) {
            final DeliveryResponsePacket packet = PacketsFactory.newResponsePacket(i, 0);
            final ChannelBuffer buf = ProtocolEncoder.encodePacket(packet);
            final ProtocolDecoder decoder = new ProtocolDecoder();
            final DeliveryResponsePacket packet2 = (DeliveryResponsePacket) decoder.decode(null, null, buf, DecoderState.VERSION);
//            System.out.println(packet.encode().length);
        }
    }
}
