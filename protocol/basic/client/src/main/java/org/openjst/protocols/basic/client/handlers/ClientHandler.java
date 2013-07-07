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
import org.openjst.protocols.basic.client.context.ClientContext;
import org.openjst.protocols.basic.constants.ProtocolResponseStatus;
import org.openjst.protocols.basic.context.SendFuture;
import org.openjst.protocols.basic.events.ClientAuthenticationEvent;
import org.openjst.protocols.basic.events.DisconnectEvent;
import org.openjst.protocols.basic.events.RPCEvent;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.Packets;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.DeliveryResponsePacket;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;
import org.openjst.protocols.basic.pdu.packets.RPCPacket;

import static org.openjst.protocols.basic.pdu.packets.PacketsFactory.newAuthResponsePacket;

public class ClientHandler extends SimpleChannelUpstreamHandler {
    private static final InternalLogger LOG =
            InternalLoggerFactory.getInstance(ClientHandler.class.getName());

    private final ClientEventsProducer eventsProducer;
    private final ClientContext clientContext;

    public ClientHandler(final ClientEventsProducer eventsProducer, final ClientContext clientContext) {
        this.eventsProducer = eventsProducer;
        this.clientContext = clientContext;
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (e.getMessage() instanceof PDU) {
            final PDU packet = (PDU) e.getMessage();
            switch (packet.getType()) {
                case Packets.TYPE_RPC: {
                    processRPC(ctx, (RPCPacket) packet);
                    break;
                }
                case Packets.TYPE_AUTHENTICATION_CLIENT:
                case Packets.TYPE_AUTHENTICATION_SERVER: {
                    sendAuthenticationResponse((AbstractAuthPacket) packet);
                    break;
                }
                case Packets.TYPE_DELIVERY_RESPONSE: {
                    processDeliveryResponse((DeliveryResponsePacket) packet);
                    break;
                }
                default:
                    LOG.error("Unknown incoming packet"
                            + "\n\tChannel ID: " + e.getChannel().getId() + ", Remote IP: " + e.getRemoteAddress()
                            + "\n\tPacket: " + packet.toString());
            }
        } else {
            super.messageReceived(ctx, e);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        LOG.error("ClientHandler", e.getCause());
        e.getChannel().close();
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        eventsProducer.queue(new DisconnectEvent(clientContext.getSession()));
        clientContext.reset();
        super.channelClosed(ctx, e);
    }

    private void sendAuthenticationResponse(final AbstractAuthPacket authPacket) {
        final AbstractAuthPacket authResponse;
        final ClientAuthenticationEvent event = new ClientAuthenticationEvent(authPacket);
        if (eventsProducer.onAuthenticate(event)) {
            authResponse = newAuthResponsePacket(authPacket.getPacketId(), event.getStatus(), event.getParameters());
        } else {
            authResponse = newAuthResponsePacket(authPacket.getPacketId(), ProtocolResponseStatus.AUTH_FAIL, null);
        }
        clientContext.getChannel().write(authResponse);
    }

    private void processRPC(final ChannelHandlerContext ctx, final RPCPacket packet) {
        if (packet.isRequireDeliveryReceipt()) {
            successResponse(ctx.getChannel(), packet.getPacketId());
        }
        eventsProducer.queue(new RPCEvent(clientContext.getSession(),
                packet.getClientId(), packet.getFormat(), packet.getData()));
    }

    private void processDeliveryResponse(final DeliveryResponsePacket packet) {
        final SendFuture future = clientContext.removeSendFuture(packet.getPacketId());
        if (future != null) {
            future.done(packet.getStatus());
        }
    }

    private static void successResponse(final Channel channel, final long packetId) {
        channel.write(PacketsFactory.newResponsePacket(packetId, ProtocolResponseStatus.OK));
    }
}
