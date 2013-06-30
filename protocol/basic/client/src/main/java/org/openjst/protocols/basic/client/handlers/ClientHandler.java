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

package org.openjst.protocols.basic.client.handlers;

import org.jboss.netty.channel.*;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.openjst.protocols.basic.client.ClientEventsProducer;
import org.openjst.protocols.basic.client.session.ClientSessionStorage;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.Packets;
import org.openjst.protocols.basic.pdu.packets.RPCPacket;

public class ClientHandler extends SimpleChannelUpstreamHandler {
    private static final InternalLogger LOG =
            InternalLoggerFactory.getInstance(ClientHandler.class.getName());

    private final ClientEventsProducer eventsProducer;
    private final ClientSessionStorage clientSessionStorage;

    public ClientHandler(final ClientEventsProducer eventsProducer, final ClientSessionStorage clientSessionStorage) {
        this.eventsProducer = eventsProducer;
        this.clientSessionStorage = clientSessionStorage;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof PDU) {
            final PDU packet = (PDU) e.getMessage();
            switch (packet.getType()) {
                case Packets.TYPE_RPC_METHOD:
                    eventsProducer.onRPC(clientSessionStorage.getSession(), (RPCPacket) packet);
                    break;

                default:
                    LOG.error("Unknown incoming packet"
                            + "\n\tChannel ID: " + e.getChannel().getId() + ", Remote IP: " + e.getRemoteAddress()
                            + "\n\tClass: " + packet.getClass().getSimpleName()
                            + "\n\tPacket: " + packet.toString()
                    );
            }
        } else {
            super.messageReceived(ctx, e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        LOG.error("ClientHandler exceptions:" + e.toString());
        e.getChannel().close();
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        eventsProducer.onDisconnect(clientSessionStorage.getSession());
        clientSessionStorage.resetSession();
        super.channelClosed(ctx, e);
    }
}
