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

package org.openjst.protocols.basic.client;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.openjst.protocols.basic.client.handlers.ClientHandler;
import org.openjst.protocols.basic.client.handlers.ClientSessionHandler;
import org.openjst.protocols.basic.client.session.ClientSessionStorage;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class Client {
    private final String host;
    private final int port;
    private final ClientEventsProducer eventsProducer;
    private final ClientSessionStorage clientSessionStorage;
    private final ChannelGroup channelGroup = new DefaultChannelGroup(this + "-channelGroup");

    private Channel channel;
    private ClientBootstrap bootstrap;

    public Client(final String host, final int port) {
        this.host = host;
        this.port = port;

        eventsProducer = new ClientEventsProducer();
        clientSessionStorage = new ClientSessionStorage();
    }

    public void addListener(final ClientEventsListener listener) {
        eventsProducer.addListener(listener);
    }

    public void removeListener(final ClientEventsListener listener) {
        eventsProducer.removeListener(listener);
    }

    public void sendPacket(final PDU packet) throws ClientNotConnectedException {
        if (!isConnected()) {
            throw new ClientNotConnectedException();
        }

        channel.write(packet);
    }

    public boolean isConnected() {
        return channel != null && channel.isConnected();
    }

    public synchronized boolean connect(final AbstractAuthPacket authRequest) {
        disconnect();

        /*
           NioClientSocketChannelFactory is not works on Android, seems like a bug in Apache Harmony

           11-22 12:42:25.534: WARN/lientSocketPipelineSink(1129): Unexpected exceptions in the selector loop.
               java.nio.channels.ClosedSelectorException
               at org.apache.harmony.nio.internal.SelectorImpl.closeCheck(SelectorImpl.java:204)
               at org.apache.harmony.nio.internal.SelectorImpl.selectInternal(SelectorImpl.java:236)
               at org.apache.harmony.nio.internal.SelectorImpl.select(SelectorImpl.java:224)
               at org.jboss.netty.channel.socket.nio.NioClientSocketPipelineSink$Boss.run(NioClientSocketPipelineSink.java:239)
               at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1068)
               at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:561)
               at java.lang.Thread.run(Thread.java:1096)
        */
        final ChannelFactory clientFactory = new NioClientSocketChannelFactory(
                new OrderedMemoryAwareThreadPoolExecutor(1, 400000000, 2000000000, 60, TimeUnit.SECONDS),
                new OrderedMemoryAwareThreadPoolExecutor(4, 400000000, 2000000000, 60, TimeUnit.SECONDS),
                4);

        bootstrap = new ClientBootstrap(clientFactory);
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        bootstrap.setOption("receiveBufferSize", 1048576);
        bootstrap.setOption("connectTimeoutMillis", 100);
        bootstrap.setPipelineFactory(new ClientPipelineFactory(
                new ClientHandler(eventsProducer, clientSessionStorage),
                new ClientSessionHandler(eventsProducer, clientSessionStorage, channelGroup, authRequest)
        ));

        final ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        if (!future.awaitUninterruptibly().isSuccess()) {
            bootstrap.releaseExternalResources();
            return false;
        }

        channelGroup.add(channel = future.getChannel());

        return channel.isConnected();
    }

    public synchronized void disconnect() {
        channelGroup.close().awaitUninterruptibly();

        if (this.bootstrap != null) {
            this.bootstrap.releaseExternalResources();
            this.bootstrap = null;
        }

        clientSessionStorage.resetSession();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
