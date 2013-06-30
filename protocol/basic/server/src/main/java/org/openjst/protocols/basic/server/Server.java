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

package org.openjst.protocols.basic.server;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ServerChannelFactory;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.server.session.ServerSessionStorage;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Server {
    private static final AtomicLong SESSION_ID = new AtomicLong(0);

    private final String host;
    private final int port;
    private final ServerEventsProducer eventsProducer;
    private final ServerPipelineFactory serverPipelineFactory;
    private final ServerSessionStorage serverSessionStorage;

    private DefaultChannelGroup channelGroup;
    private ServerBootstrap bootstrap;
    private boolean started = false;

    public Server(final String host, final int port) {
        this.host = host;
        this.port = port;
        this.eventsProducer = new ServerEventsProducer();
        this.channelGroup = new DefaultChannelGroup(this + "-channelGroup");
        this.serverSessionStorage = new ServerSessionStorage();
        this.serverPipelineFactory = new ServerPipelineFactory(eventsProducer, serverSessionStorage, channelGroup);
    }

    public static long nextSessionID() {
        return SESSION_ID.incrementAndGet();
    }

    public void addListener(final ServerEventsListener listener) {
        eventsProducer.addListener(listener);
    }

    public void removeListener(final ServerEventsListener listener) {
        eventsProducer.removeListener(listener);
    }

    public void sendPacket(final String accountId, final String clientId, final PDU packet) throws ClientNotConnectedException {
        final Channel channel = serverSessionStorage.getChannel(accountId, clientId);
        if (channel == null || !channel.isConnected()) {
            throw new ClientNotConnectedException();
        }

        channel.write(packet);
    }

    public void sendPacketSafe(final String accountId, final String userId, final PDU packet) throws ClientNotConnectedException {
        final Channel channel = serverSessionStorage.getChannel(accountId, userId);
        if (channel != null && channel.isConnected()) {
            channel.write(packet);
        }
    }

    public boolean start() {
        stop();

        final ServerChannelFactory serverFactory = new NioServerSocketChannelFactory(
                new OrderedMemoryAwareThreadPoolExecutor(1, 400000000, 2000000000, 60, TimeUnit.SECONDS),
                new OrderedMemoryAwareThreadPoolExecutor(4, 400000000, 2000000000, 60, TimeUnit.SECONDS),
                4);

        bootstrap = new ServerBootstrap(serverFactory);
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setPipelineFactory(serverPipelineFactory);

        final Channel channel = bootstrap.bind(new InetSocketAddress(this.host, this.port));

        started = channel.isBound();

        if (!started) {
            this.stop();
            return false;
        }

        this.channelGroup.add(channel);

        return started;
    }

    public void stop() {
        this.channelGroup.close().awaitUninterruptibly();

        if (this.bootstrap != null) {
            this.bootstrap.releaseExternalResources();
            this.bootstrap = null;
        }

        serverSessionStorage.reset();
    }

    public boolean isStarted() {
        return started;
    }

    public synchronized void disconnectClient(String accountId, String userId) {
        final Channel channel = serverSessionStorage.getChannel(accountId, userId);
        if (channel != null) {
            channel.close().awaitUninterruptibly();
        }
    }
}
