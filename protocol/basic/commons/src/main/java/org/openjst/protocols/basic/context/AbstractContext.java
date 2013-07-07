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

package org.openjst.protocols.basic.context;

import org.jboss.netty.channel.ChannelFuture;
import org.openjst.protocols.basic.pdu.PDU;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sergey Grachev
 */
public abstract class AbstractContext {

    protected final AtomicLong packetIdSequence = new AtomicLong(0);
    protected final Map<Long, SendFuture> sendFutureMap = new ConcurrentHashMap<Long, SendFuture>();

    public void reset() {
        sendFutureMap.clear();
    }

    public long nextPacketId() {
        return packetIdSequence.getAndIncrement();
    }

    public SendFuture createSendFuture(final ChannelFuture channelFuture, final PDU packet) {
        final SendFuture future = new SendFuture(this, channelFuture, packet.getPacketId());
        sendFutureMap.put(packet.getPacketId(), future);
        return future;
    }

    public void removeSendFuture(final SendFuture sendFuture) {
        sendFutureMap.remove(sendFuture.getPacketId());
    }

    public SendFuture removeSendFuture(final long packetId) {
        return sendFutureMap.remove(packetId);
    }
}
