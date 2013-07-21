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
import org.openjst.protocols.basic.constants.ProtocolResponseStatus;
import org.openjst.protocols.basic.context.SendFuture;
import org.openjst.protocols.basic.events.DisconnectEvent;
import org.openjst.protocols.basic.events.RPCEvent;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.Packets;
import org.openjst.protocols.basic.pdu.packets.AuthResponsePacket;
import org.openjst.protocols.basic.pdu.packets.DeliveryResponsePacket;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;
import org.openjst.protocols.basic.pdu.packets.RPCPacket;
import org.openjst.protocols.basic.server.ServerEventsProducer;
import org.openjst.protocols.basic.server.context.ForwardAuthenticationCallback;
import org.openjst.protocols.basic.server.context.ServerContext;
import org.openjst.protocols.basic.session.Session;
import org.openjst.protocols.basic.utils.LogUtils;

public class ServerHandler extends SimpleChannelHandler {
    private static final InternalLogger LOG =
            InternalLoggerFactory.getInstance(ServerHandler.class.getName());

    private final ServerEventsProducer eventsProducer;
    private final ServerContext serverContext;

    public ServerHandler(final ServerEventsProducer eventsProducer, final ServerContext serverContext) {
        this.eventsProducer = eventsProducer;
        this.serverContext = serverContext;
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (e.getMessage() instanceof PDU) {
            final PDU packet = (PDU) e.getMessage();
            switch (packet.getType()) {
                case Packets.TYPE_AUTHENTICATION_RESPONSE: {
                    processAuthenticationResponse(ctx, (AuthResponsePacket) packet);
                    break;
                }
                case Packets.TYPE_DELIVERY_RESPONSE: {
                    processDeliveryResponse((DeliveryResponsePacket) packet);
                    break;
                }
                case Packets.TYPE_RPC: {
                    processRPC(ctx, (RPCPacket) packet);
                    break;
                }
                default: {
                    LOG.error("Unknown incoming packet"
                            + "\n\tChannel ID: " + e.getChannel().getId() + ", Remote IP: " + e.getRemoteAddress()
                            + "\n\tPacket: " + packet.toString());
                }
            }
        } else {
            super.messageReceived(ctx, e);
        }
    }

    private void processRPC(final ChannelHandlerContext ctx, final RPCPacket packet) {
        final Channel thisChannel = ctx.getChannel();

        if (packet.isRequireDeliveryReceipt()) {
            successResponse(thisChannel, packet.getPacketId());
        }

        final String recipientId = packet.getRecipientId();
        final Session session = serverContext.getSessionByChannel(thisChannel);
        eventsProducer.queue(new RPCEvent(session, packet.getRecipientId(), packet.getFormat(), packet.getData()));
//        if (recipientId == null || "".equals(recipientId.trim())) {
//        } else {
//            eventsProducer.queue(new ForwardRPCEvent(session, recipientId, packet.getFormat(), packet.getData()));
//        }

        // TODO forwarding ???

//        final Session session = serverContext.getSessionByChannel(thisChannel);
//        final Channel targetChannel;
//        if (session instanceof ServerSession) {
//            targetChannel = serverContext.getClientChannel(session.getAccountId(), request.getClientId());
//        } else {
//            targetChannel = serverContext.getServerChannel(session.getAccountId());
//        }
//
//        if (targetChannel == null) {
//            errorResponse(ctx, packetId, ProtocolResponseStatus.NO_FORWARD_SERVER, request.toString());
//            return;
//        }
//
//        request.setPacketId(serverContext.nextPacketId());
//        targetChannel.write(request);
//
//        LOG.info("Forward RPC packet to remote server"
//                + "\n\tChannel ID: " + thisChannel.getId() + ", Remote IP: " + thisChannel.getRemoteAddress()
//                + "\n\tServer channel ID: " + targetChannel.getId() + ", Server remote IP: " + targetChannel.getRemoteAddress()
//                + "\n\tPacket ID: " + request.getPacketId());
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        LOG.error("ServerHandler:", e.getCause());
        e.getChannel().close();
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        final Session session = serverContext.getSessionByChannel(ctx.getChannel());
        if (session != null) {
            eventsProducer.queue(new DisconnectEvent(session));
        }
        serverContext.unregisterChannel(ctx.getChannel());
        super.channelClosed(ctx, e);
    }

    private void processAuthenticationResponse(final ChannelHandlerContext ctx, final AuthResponsePacket packet) {
        final ForwardAuthenticationCallback callback = serverContext.removeForwardAuthentication(
                ctx.getChannel().getId(), packet.getPacketId());

        if (callback != null) {
            try {
                callback.done(packet);
            } catch (Exception e) {
                LOG.error("Error while process forward authentication response", e.getCause());
            }
        }
    }

    private void processDeliveryResponse(final DeliveryResponsePacket packet) {
        final SendFuture future = serverContext.removeSendFuture(packet.getPacketId());
        if (future != null) {
            future.done(packet.getStatus());
        }
    }

    private static void errorResponse(final ChannelHandlerContext ctx, final long packetId, final short status, final String data) {
        final Channel channel = ctx.getChannel();

        if (packetId != -1) {
            channel.write(PacketsFactory.newResponsePacket(packetId, status));
        }

        LOG.error(LogUtils.statusAsStringBuilder(status)
                .append("\n\tChannel ID: ").append(channel.getId()).append(", Remote IP: ").append(channel.getRemoteAddress())
                .append("\n\tData: ").append(data).toString());
    }

    private static void successResponse(final Channel channel, final long packetId) {
        channel.write(PacketsFactory.newResponsePacket(packetId, ProtocolResponseStatus.OK));
    }
}
