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

package org.openjst.protocols.basic.server.handlers;

import org.jboss.netty.channel.*;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.Packets;
import org.openjst.protocols.basic.pdu.packets.PresenceStatePacket;
import org.openjst.protocols.basic.pdu.packets.RPCPacket;
import org.openjst.protocols.basic.server.ServerEventsProducer;
import org.openjst.protocols.basic.server.session.ServerSessionStorage;
import org.openjst.protocols.basic.sessions.Session;

public class ServerHandler extends SimpleChannelUpstreamHandler {
    private static final InternalLogger LOG =
            InternalLoggerFactory.getInstance(ServerHandler.class.getName());

    private final ServerEventsProducer eventsProducer;
    private final ServerSessionStorage serverSessionStorage;

    public ServerHandler(final ServerEventsProducer eventsProducer, final ServerSessionStorage serverSessionStorage) {
        this.eventsProducer = eventsProducer;
        this.serverSessionStorage = serverSessionStorage;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof PDU) {
            final PDU packet = (PDU) e.getMessage();
            switch (packet.getType()) {
                case Packets.TYPE_RPC_METHOD:
                    eventsProducer.onRPC(serverSessionStorage.getSessionByChannel(e.getChannel()), (RPCPacket) packet);
                    break;

                case Packets.TYPE_PRESENCE_STATE:
                    eventsProducer.onPresenceState((PresenceStatePacket) packet);
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
        LOG.error(e.toString());
        e.getChannel().close();
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        final Session session = serverSessionStorage.getSessionByChannel(ctx.getChannel());
        if (session != null) {
            eventsProducer.onDisconnect(session);
        }
        serverSessionStorage.unregisterChannel(ctx.getChannel());
        super.channelClosed(ctx, e);
    }
}
