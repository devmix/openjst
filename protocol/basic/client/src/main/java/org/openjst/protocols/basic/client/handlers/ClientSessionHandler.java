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
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.openjst.protocols.basic.client.ClientEventsProducer;
import org.openjst.protocols.basic.client.context.ClientContext;
import org.openjst.protocols.basic.constants.ProtocolResponseStatus;
import org.openjst.protocols.basic.events.AuthenticationFailEvent;
import org.openjst.protocols.basic.events.ConnectEvent;
import org.openjst.protocols.basic.events.DisconnectEvent;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.Packets;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.AuthClientRequestPacket;
import org.openjst.protocols.basic.pdu.packets.AuthResponsePacket;
import org.openjst.protocols.basic.pdu.packets.AuthServerRequestPacket;
import org.openjst.protocols.basic.session.Session;
import org.openjst.protocols.basic.utils.LogUtils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Sergey Grachev
 */
public final class ClientSessionHandler extends SimpleChannelHandler {

    private static final InternalLogger LOG =
            InternalLoggerFactory.getInstance(ClientSessionHandler.class.getName());

    private static final long TIMEOUT = 30 * 1000; // 30 sec

    private final ClientEventsProducer eventsProducer;
    private final ClientContext clientContext;
    private final AtomicBoolean authComplete;
    private final AtomicBoolean authFailed;
    private final Object authMutex = new Object();
    private final Queue<MessageEvent> bufferedMessages = new LinkedList<MessageEvent>();
    private final CountDownLatch latch = new CountDownLatch(1);
    private final ChannelGroup channelGroup;
    private final AbstractAuthPacket authRequest;

    public ClientSessionHandler(final ClientEventsProducer eventsProducer, final ClientContext clientContext,
                                final ChannelGroup channelGroup, final AbstractAuthPacket authRequest) {
        this.eventsProducer = eventsProducer;
        this.clientContext = clientContext;
        this.channelGroup = channelGroup;
        this.authRequest = authRequest;
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
            if (!(e.getMessage() instanceof PDU) || ((PDU) msg).getType() != Packets.TYPE_AUTHENTICATION_RESPONSE) {
                this.errorResponse(ctx, ProtocolResponseStatus.AUTH_UNEXPECTED_PACKET, msg.toString());
                return;
            }

            final AuthResponsePacket packet = (AuthResponsePacket) msg;

            if (packet.getPacketId() != authRequest.getPacketId()) {
                this.errorResponse(ctx, ProtocolResponseStatus.AUTH_INCORRECT_HANDSHAKE,
                        "Expected: " + authRequest.getPacketId() + ", Packet: " + packet.toString());
                return;
            }

            if (packet.getResponseStatus() != ProtocolResponseStatus.OK) {
                this.errorResponse(ctx, packet.getResponseStatus(), packet.toString());
                return;
            }

            this.onAuthenticationSuccess(ctx, packet);
        }
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOG.info("Connected"
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
                                errorResponse(ctx, ProtocolResponseStatus.AUTH_TIMEOUT, null);
                            }
                        }
                    }
                }.start();
            }
        });

        ctx.sendDownstream(new DownstreamMessageEvent(ctx.getChannel(), f, authRequest, null));
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

    private void onAuthenticationSuccess(final ChannelHandlerContext ctx, final AuthResponsePacket packet) {
        this.authComplete.set(true);
        this.authFailed.set(false);
        this.latch.countDown();

        final Session session;
        switch (authRequest.getType()) {
            case Packets.TYPE_AUTHENTICATION_CLIENT: {
                session = new ClientSession((AuthClientRequestPacket) authRequest);
                break;
            }
            case Packets.TYPE_AUTHENTICATION_SERVER: {
                session = new ServerSession((AuthServerRequestPacket) authRequest);
                break;
            }
            default:
                errorResponse(ctx, ProtocolResponseStatus.AUTH_UNSUPPORTED, authRequest.toString());
                return;
        }
        this.clientContext.setSession(session);
        this.clientContext.setChannel(ctx.getChannel());

        eventsProducer.queue(new ConnectEvent(this.clientContext.getSession()));

        for (final MessageEvent message : this.bufferedMessages) {
            ctx.sendDownstream(message);
        }

        ctx.getPipeline().remove(this);
    }

    private void errorResponse(final ChannelHandlerContext ctx, final int status, final String data) {
        final Channel channel = ctx.getChannel();

        LOG.error(LogUtils.statusAsStringBuilder(status)
                .append("\n\tChannel ID: ").append(channel.getId()).append(", Remote IP: ").append(channel.getRemoteAddress())
                .append("\n\tData: ").append(data).toString());

        this.authComplete.set(true);
        this.authFailed.set(true);
        this.latch.countDown();
        ctx.getChannel().close();

        eventsProducer.queue(new AuthenticationFailEvent(status, authRequest));
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        eventsProducer.queue(new DisconnectEvent(clientContext.getSession()));

        if (!this.authComplete.get()) {
            this.errorResponse(ctx, ProtocolResponseStatus.AUTH_INTERRUPTED, "");
        }

        super.channelClosed(ctx, e);

        clientContext.reset();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        LOG.error("ClientSessionHandler", e.getCause());
        if (e.getChannel().isConnected()) {
            e.getChannel().close();
        } else {
            this.errorResponse(ctx, ProtocolResponseStatus.AUTH_INTERNAL_ERROR, e.toString());
        }
    }

    @Override
    public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        super.channelOpen(ctx, e);
        channelGroup.add(e.getChannel());
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
}

