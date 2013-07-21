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
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.openjst.protocols.basic.constants.ProtocolResponseStatus;
import org.openjst.protocols.basic.events.ConnectEvent;
import org.openjst.protocols.basic.events.ForwardAuthenticationResponseEvent;
import org.openjst.protocols.basic.events.ServerAuthenticationEvent;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.Packets;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.AuthClientRequestPacket;
import org.openjst.protocols.basic.pdu.packets.AuthResponsePacket;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;
import org.openjst.protocols.basic.server.ServerEventsProducer;
import org.openjst.protocols.basic.server.context.ForwardAuthenticationCallback;
import org.openjst.protocols.basic.server.context.ServerContext;
import org.openjst.protocols.basic.session.Session;
import org.openjst.protocols.basic.utils.LogUtils;
import org.openjst.protocols.basic.utils.NettyUtils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Sergey Grachev
 */
public class ServerSessionsHandler extends SimpleChannelHandler {

    private static final InternalLogger LOG =
            InternalLoggerFactory.getInstance(ServerSessionsHandler.class.getName());

    private static final long TIMEOUT = 30 * 1000; // 30 sec

    private final ServerEventsProducer eventsProducer;
    private final ServerContext serverContext;
    private final DefaultChannelGroup channelGroup;
    private final AtomicBoolean authComplete;
    private final AtomicBoolean authFailed;
    private final Object authMutex = new Object();
    private final Queue<MessageEvent> bufferedMessages = new LinkedList<MessageEvent>();
    private final CountDownLatch latch = new CountDownLatch(1);

    public ServerSessionsHandler(final ServerEventsProducer eventsProducer, final ServerContext serverContext,
                                 final DefaultChannelGroup channelGroup) {
        this.eventsProducer = eventsProducer;
        this.serverContext = serverContext;
        this.channelGroup = channelGroup;
        authComplete = new AtomicBoolean(false);
        authFailed = new AtomicBoolean(false);
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (checkAuth(ctx, e)) {
            return;
        }

        synchronized (this.authMutex) {
            if (checkAuth(ctx, e)) {
                return;
            }

            final Object msg = e.getMessage();

            if (!(e.getMessage() instanceof PDU)) {
                errorResponse(ctx, null, ProtocolResponseStatus.AUTH_UNEXPECTED_PACKET, msg.toString());
                return;
            }

            final PDU packet = (PDU) msg;
            switch (packet.getType()) {
                case Packets.TYPE_AUTHENTICATION_CLIENT:
                case Packets.TYPE_AUTHENTICATION_SERVER: {
                    processAuthenticate(ctx, new ServerAuthenticationEvent((AbstractAuthPacket) packet));
                    break;
                }
                default:
                    errorResponse(ctx, null, ProtocolResponseStatus.AUTH_UNSUPPORTED, packet.toString());
            }
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        LOG.error("ServerSessionsHandler exceptions:", e.getCause());
        if (e.getChannel().isConnected()) {
            e.getChannel().close();
        } else {
            errorResponse(ctx, null, ProtocolResponseStatus.AUTH_INTERNAL_ERROR, e.toString());
        }
        e.getChannel().close();
    }

    @Override
    public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        super.channelOpen(ctx, e);
        channelGroup.add(e.getChannel());
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOG.info("New incoming connection"
                + "\n\tChannel ID: " + e.getChannel().getId() + ", Remote IP: " + e.getChannel().getRemoteAddress());

        final ChannelFuture f = Channels.future(ctx.getChannel());
        f.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            latch.await(TIMEOUT, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e1) {
                            LOG.error("Authentication interrupted");
                            e1.printStackTrace();
                        }

                        if (authFailed.get() || authComplete.get()) {
                            return;
                        }

                        synchronized (authMutex) {
                            if (authFailed.get()) {
                                return;
                            }

                            if (!authComplete.get()) {
                                errorResponse(ctx, null, ProtocolResponseStatus.AUTH_TIMEOUT, null);
                            }
                        }
                    }
                }.start();
            }
        });

        super.channelConnected(ctx, e);
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        if (!this.authComplete.get()) {
            errorResponse(ctx, null, ProtocolResponseStatus.AUTH_INTERRUPTED, "");
        }
        super.channelClosed(ctx, e);
    }

    @Override
    public void writeRequested(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        synchronized (this.authMutex) {
            if (this.authFailed.get()) {
                return;
            }

            if (this.authComplete.get()) {
                super.writeRequested(ctx, e);
            } else {
                this.bufferedMessages.offer(e);
            }
        }
    }

    private boolean checkAuth(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (this.authFailed.get()) {
            return true;
        }

        if (this.authComplete.get()) {
            super.messageReceived(ctx, e);
            return true;
        }
        return false;
    }

    private void processAuthenticate(final ChannelHandlerContext ctx, final ServerAuthenticationEvent event) {
        final AbstractAuthPacket packet = event.getPacket();
        if (eventsProducer.onAuthenticate(event)) {
            if (event.isRequireForwarding()) {
                doForwarding(ctx, event, (AuthClientRequestPacket) packet);
            } else {
                doAuthorization(ctx, event, packet);
            }
        } else {
            errorResponse(ctx, packet.getPacketId(), ProtocolResponseStatus.AUTH_FAIL, packet.toString());
        }
    }

    private void doAuthorization(final ChannelHandlerContext ctx, final ServerAuthenticationEvent event, final AbstractAuthPacket packet) {
        this.authComplete.set(true);
        this.authFailed.set(false);

        final Channel channel = ctx.getChannel();
        final Session session = event.getSession();

        LOG.info("Authentication success"
                + "\n\tChannel ID: " + channel.getId() + ", Remote IP: " + channel.getRemoteAddress()
                + "\n\tSession: " + session);

        if (packet instanceof AuthClientRequestPacket) {
            serverContext.registerClientChannel(channel, session);
        } else {
            serverContext.registerServerChannel(channel, session);
        }

        NettyUtils.writeDownstream(ctx, PacketsFactory.newAuthResponsePacket(packet.getPacketId(), ProtocolResponseStatus.OK));

        eventsProducer.queue(new ConnectEvent(session));

        for (final MessageEvent message : this.bufferedMessages) {
            ctx.sendDownstream(message);
        }

        ctx.getPipeline().remove(this);
    }

    private void doForwarding(final ChannelHandlerContext ctx, final ServerAuthenticationEvent event, final AuthClientRequestPacket request) {
        final Channel thisChannel = ctx.getChannel();
        final Channel serverChannel = serverContext.getServerChannel(request.getAccountId());

        if (serverChannel == null) {
            errorResponse(ctx, request.getPacketId(), ProtocolResponseStatus.NO_FORWARD_RECIPIENT, request.toString());
            return;
        }

        final long originalPacketId = request.getPacketId();
        request.setPacketId(serverContext.nextPacketId());

        serverContext.createForwardAuthentication(serverChannel.getId(), request.getPacketId(), new ForwardAuthenticationCallback() {
            @Override
            public void done(final AuthResponsePacket response) {
                if (response.getResponseStatus() == ProtocolResponseStatus.OK) {
                    response.setPacketId(originalPacketId);
                    doAuthorization(ctx, event, response);
                } else {
                    errorResponse(ctx, null, ProtocolResponseStatus.AUTH_FAIL, response.toString());
                }
                eventsProducer.queue(new ForwardAuthenticationResponseEvent(event.getSession(), request, response));
            }
        });

        serverChannel.write(request);

        LOG.info("Forward authentication packet to remote server"
                + "\n\tChannel ID: " + thisChannel.getId() + ", Remote IP: " + thisChannel.getRemoteAddress()
                + "\n\tServer channel ID: " + serverChannel.getId() + ", Server remote IP: " + serverChannel.getRemoteAddress()
                + "\n\tPacket ID: " + request.getPacketId());
    }

    private void errorResponse(final ChannelHandlerContext ctx, final Long packetId, final short status, final String data) {
        final Channel channel = ctx.getChannel();

        LOG.error(LogUtils.statusAsStringBuilder(status)
                .append("\n\tChannel ID: ").append(channel.getId()).append(", Remote IP: ").append(channel.getRemoteAddress())
                .append("\n\tData: ").append(data).toString());

        if (this.authComplete.get()) {
            return;
        }

        this.authComplete.set(true);
        this.authFailed.set(true);

        if (packetId != null) {
            NettyUtils.writeDownstream(ctx, PacketsFactory.newAuthResponsePacket(packetId, status));
        } else {
            NettyUtils.writeDownstream(ctx, PacketsFactory.newAuthResponsePacket(-1, status));
        }

        channel.close();
    }
}

