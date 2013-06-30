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
import org.jetbrains.annotations.Nullable;
import org.openjst.protocols.basic.messages.ProtocolErrorCodes;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.Packets;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.AuthRequestBasicPacket;
import org.openjst.protocols.basic.pdu.packets.AuthResponsePacket;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;
import org.openjst.protocols.basic.server.ServerEventsProducer;
import org.openjst.protocols.basic.server.session.ServerSession;
import org.openjst.protocols.basic.server.session.ServerSessionStorage;

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
    private final ServerSessionStorage serverSessionStorage;
    private final DefaultChannelGroup channelGroup;
    private final AtomicBoolean authComplete;
    private final AtomicBoolean authFailed;
    private final Object authMutex = new Object();
    private final Queue<MessageEvent> bufferedMessages = new LinkedList<MessageEvent>();
    private final CountDownLatch latch = new CountDownLatch(1);

    public ServerSessionsHandler(final ServerEventsProducer eventsProducer, final ServerSessionStorage serverSessionStorage,
                                 final DefaultChannelGroup channelGroup) {
        this.eventsProducer = eventsProducer;
        this.serverSessionStorage = serverSessionStorage;
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
                this.fireAuthFailed(ctx, null, ProtocolErrorCodes.AUTH_UNEXPECTED_PACKET, "Unauthorized access, waiting for auth response"
                        + "\n\tChannel ID: " + e.getChannel().getId() + ", Remote IP: " + e.getRemoteAddress()
                        + "\n\tPacket: " + msg.toString());
                return;
            }

            final PDU packet = (PDU) msg;

            switch (packet.getType()) {
                case Packets.TYPE_AUTH_BASIC:
                    authenticate((AuthRequestBasicPacket) packet, ctx);
                    break;

                default:
                    fireAuthFailed(ctx, null, ProtocolErrorCodes.AUTH_UNEXPECTED_PACKET, "Unauthorized access or unsupported authentication method."
                            + "\n\tChannel ID: " + e.getChannel().getId() + ", Remote IP: " + e.getRemoteAddress()
                            + "\n\tPacket: " + packet.toString());
            }

            for (final MessageEvent message : this.bufferedMessages) {
                ctx.sendDownstream(message);
            }

            ctx.getPipeline().remove(this);

            this.fireAuthSucceeded(ctx);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        LOG.error("ServerSessionsHandler exceptions:" + e.toString());
        if (e.getChannel().isConnected()) {
            e.getChannel().close();
        } else {
            this.fireAuthFailed(ctx, null, ProtocolErrorCodes.AUTH_INTERNAL_ERROR, "Session handler exceptions:" + e.toString());
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
        System.out.println("New incoming connection"
                + "\n\tChannel ID: " + e.getChannel().getId() + ", Remote IP: " + ctx.getChannel().getRemoteAddress()
        );

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
                                fireAuthFailed(ctx, null, ProtocolErrorCodes.AUTH_TIMEOUT, "Connection authentication timer: authentication completed");
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
            this.fireAuthFailed(ctx, null, ProtocolErrorCodes.AUTH_INTERRUPTED, "");
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

    private void fireAuthSucceeded(final ChannelHandlerContext ctx) {
        this.authComplete.set(true);
        this.authFailed.set(false);
    }

    private void fireAuthFailed(final ChannelHandlerContext ctx, @Nullable final AbstractAuthPacket packet, final short errorCode,
                                final String message) {
        if (this.authComplete.get()) {
            return;
        }

        this.authComplete.set(true);
        this.authFailed.set(true);

        if (packet != null) {
            writeDownstream(ctx, PacketsFactory.newAuthResponsePacket(
                    packet.getRequestId(), packet.getAccountId(), packet.getClientId(), errorCode));
        } else {
            writeDownstream(ctx, PacketsFactory.newAuthResponsePacket(-1, "", "", errorCode));
        }

        ctx.getChannel().close();

        LOG.error(message);
    }

    private void writeDownstream(final ChannelHandlerContext ctx, final AuthResponsePacket packet) {
        ctx.sendDownstream(new DownstreamMessageEvent(ctx.getChannel(), Channels.future(ctx.getChannel()), packet, null));
    }

    private void authenticate(final AuthRequestBasicPacket packet, final ChannelHandlerContext ctx) {
        final Channel channel = ctx.getChannel();
        final ServerSession session = eventsProducer.onTryAuthenticate(packet);
        if (session != null) {
            session.setChannelId(channel.getId());
            serverSessionStorage.registerChannel(channel, session);
            writeDownstream(ctx, PacketsFactory.newAuthResponsePacket(
                    packet.getRequestId(), packet.getAccountId(), packet.getClientId(), ProtocolErrorCodes.OK));
            eventsProducer.onConnect(session);
            LOG.info("Authentication success"
                    + "\n\tChannel ID: " + channel.getId() + ", Remote IP: " + channel.getRemoteAddress()
                    + "\n\tAccount ID: " + packet.getAccountId() + ", Client ID: " + packet.getClientId());
        } else {
            fireAuthFailed(ctx, packet, ProtocolErrorCodes.AUTH_FAIL, "Authentication failed"
                    + "\n\tChannel ID: " + channel.getId() + ", Remote IP: " + channel.getRemoteAddress()
                    + "\n\tAccount ID: " + packet.getAccountId() + ", Client ID: " + packet.getClientId());
        }
    }
}

