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
import org.openjst.protocols.basic.client.session.ClientSession;
import org.openjst.protocols.basic.client.session.ClientSessionStorage;
import org.openjst.protocols.basic.messages.ProtocolErrorCodes;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.Packets;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.AuthResponsePacket;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Sergey Grachev
 */
public class ClientSessionHandler extends SimpleChannelHandler {
    private static final InternalLogger LOG =
            InternalLoggerFactory.getInstance(ClientSessionHandler.class.getName());

    private static final long TIMEOUT = 30 * 1000; // 30 sec

    private final ClientEventsProducer eventsProducer;
    private ClientSessionStorage clientSessionStorage;
    private final AtomicBoolean authComplete;
    private final AtomicBoolean authFailed;
    private final Object authMutex = new Object();
    private final Queue<MessageEvent> bufferedMessages = new LinkedList<MessageEvent>();
    private final CountDownLatch latch = new CountDownLatch(1);
    private final ChannelGroup channelGroup;
    private final AbstractAuthPacket authRequest;

    public ClientSessionHandler(final ClientEventsProducer eventsProducer, final ClientSessionStorage clientSessionStorage,
                                final ChannelGroup channelGroup, final AbstractAuthPacket authRequest) {
        this.eventsProducer = eventsProducer;
        this.clientSessionStorage = clientSessionStorage;
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
            if (!(e.getMessage() instanceof PDU) || ((PDU) msg).getType() != Packets.TYPE_AUTH_RESPONSE) {
                this.fireAuthFailed(ctx, ProtocolErrorCodes.AUTH_UNEXPECTED_PACKET, "Unauthorized access, waiting for auth response"
                        + "\n\tChannel ID: " + e.getChannel().getId() + ", Remote IP: " + e.getRemoteAddress()
                        + "\n\tPacket: " + msg.toString());
                return;
            }

            final AuthResponsePacket packet = (AuthResponsePacket) msg;

            if (packet.getRequestId() != authRequest.getRequestId()) {
                this.fireAuthFailed(ctx, ProtocolErrorCodes.AUTH_INCORRECT_HANDSHAKE, "Server request ID not match to client request ID."
                        + "\n\tChannel ID: " + e.getChannel().getId() + ", Remote IP: " + e.getRemoteAddress()
                        + "\n\tClient ID: " + authRequest.getRequestId() + ", Packet: " + packet.toString());
                return;
            }

            if (packet.getErrorCode() != ProtocolErrorCodes.OK) {
                this.fireAuthFailed(ctx, packet.getErrorCode(), "Server return error."
                        + "\n\tChannel ID: " + e.getChannel().getId() + ", Remote IP: " + e.getRemoteAddress()
                        + "\n\tPacket: " + packet.toString());
                return;
            }

            System.out.println("Authentication success");

            for (final MessageEvent message : this.bufferedMessages) {
                ctx.sendDownstream(message);
            }

            ctx.getPipeline().remove(this);

            this.fireAuthSucceeded(ctx, packet);
        }
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        System.out.println("Connected to " + e.getChannel().getRemoteAddress());
        System.out.println("Channel ID: " + e.getChannel().getId());

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
                                fireAuthFailed(ctx, ProtocolErrorCodes.AUTH_TIMEOUT, "Connection authentication timer: authentication completed");
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

    private void fireAuthSucceeded(final ChannelHandlerContext ctx, final AuthResponsePacket packet) {
        this.authComplete.set(true);
        this.authFailed.set(false);
        this.latch.countDown();

        clientSessionStorage.setSession(new ClientSession(packet.getAccountId(), packet.getClientId()));
        eventsProducer.onConnect(clientSessionStorage.getSession());
    }

    private void fireAuthFailed(final ChannelHandlerContext ctx, final int errorCode, final String message) {
        this.authComplete.set(true);
        this.authFailed.set(true);
        this.latch.countDown();
        ctx.getChannel().close();

        eventsProducer.onAuthorizationFail(errorCode, authRequest);

        LOG.error(message);
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        eventsProducer.onDisconnect(clientSessionStorage.getSession());

        if (!this.authComplete.get()) {
            this.fireAuthFailed(ctx, ProtocolErrorCodes.AUTH_INTERRUPTED, "");
        }

        super.channelClosed(ctx, e);

        clientSessionStorage.resetSession();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        LOG.error("ClientSessionHandler exceptions:" + e.toString());
        if (e.getChannel().isConnected()) {
            e.getChannel().close();
        } else {
            this.fireAuthFailed(ctx, ProtocolErrorCodes.AUTH_INTERNAL_ERROR, "Session handler exceptions:" + e.toString());
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

