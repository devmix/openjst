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
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ServerChannelFactory;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.protocols.basic.context.SendFuture;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.PDU;
import org.openjst.protocols.basic.pdu.packets.PacketsFactory;
import org.openjst.protocols.basic.server.context.ServerContext;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class Server {

    private static final InternalLogger LOG =
            InternalLoggerFactory.getInstance(Server.class.getName());

    private final String host;
    private final int clientsPort;
    private final int serversPort;
    private final ServerEventsProducer eventsProducer;
    private final ServerPipelineFactory serverPipelineFactory;
    private final ServerContext serverContext;
    private final DefaultChannelGroup channelGroup;

    private ServerBootstrap bootstrap;
    private boolean clientsHandlerStarted = false;
    private boolean serversHandlerStarted = false;

    public Server(final String host, final int clientsPort, final int serversPort) {
        this.host = host;
        this.clientsPort = clientsPort;
        this.serversPort = serversPort;
        this.eventsProducer = new ServerEventsProducer();
        this.channelGroup = new DefaultChannelGroup(this + "-channelGroup");
        this.serverContext = new ServerContext();
        this.serverPipelineFactory = new ServerPipelineFactory(eventsProducer, serverContext, channelGroup);
    }

    public void addListener(final ServerEventsListener listener) {
        eventsProducer.addListener(listener);
    }

    public void removeListener(final ServerEventsListener listener) {
        eventsProducer.removeListener(listener);
    }

    private SendFuture sendPacket(final Channel channel, final PDU packet) throws ClientNotConnectedException {
        if (channel == null || !channel.isConnected()) {
            throw new ClientNotConnectedException();
        }
        return serverContext.createSendFuture(channel.write(packet), packet);
    }

    public void sendClientPacketSafe(final String accountId, final String userId, final PDU packet) throws ClientNotConnectedException {
        final Channel channel = serverContext.getClientChannel(accountId, userId);
        if (channel != null && channel.isConnected()) {
            channel.write(packet);
        }
    }

    public SendFuture rpcInvokeOnServer(final String accountId, final RPCMessageFormat format, final byte[] data) throws ClientNotConnectedException {
        final PDU packet = PacketsFactory.newRPCPacket(serverContext.nextPacketId(), System.currentTimeMillis(), null, format, data);
        return sendPacket(serverContext.getServerChannel(accountId), packet);
    }

    public SendFuture rpcInvokeOnClient(final String accountId, final String clientId, final RPCMessageFormat format, final byte[] data) throws ClientNotConnectedException {
        final PDU packet = PacketsFactory.newRPCPacket(serverContext.nextPacketId(), System.currentTimeMillis(), null, format, data);
        return sendPacket(serverContext.getClientChannel(accountId, clientId), packet);
    }

    public SendFuture rpcForwardToServer(final String accountId, final String clientId, final RPCMessageFormat format, final byte[] data) throws ClientNotConnectedException {
        final PDU packet = PacketsFactory.newRPCPacket(serverContext.nextPacketId(), System.currentTimeMillis(), clientId, format, data);
        return sendPacket(serverContext.getServerChannel(accountId), packet);
    }

    public SendFuture rpcForwardToClient(final String accountId, final String clientId, final RPCMessageFormat format, final byte[] data) throws ClientNotConnectedException {
        final PDU packet = PacketsFactory.newRPCPacket(
                serverContext.nextPacketId(), System.currentTimeMillis(), clientId, format, data);
        return sendPacket(serverContext.getClientChannel(accountId, clientId), packet);
    }

    public boolean start() {
        stop();

        final ServerChannelFactory serverFactory = new NioServerSocketChannelFactory(
                new OrderedMemoryAwareThreadPoolExecutor(2, 400000000, 2000000000, 60, TimeUnit.SECONDS),
                new OrderedMemoryAwareThreadPoolExecutor(4, 400000000, 2000000000, 60, TimeUnit.SECONDS),
                4);

        bootstrap = new ServerBootstrap(serverFactory);
        bootstrap.setOption("reuseAddress", true);
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setPipelineFactory(serverPipelineFactory);

        bindClientsListener();
        bindServersListener();

        if (!clientsHandlerStarted && !serversHandlerStarted) {
            this.stop();
            return false;
        }

        eventsProducer.start();

        return true;
    }

    private void bindClientsListener() {
        try {
            final Channel clientsChannel = bootstrap.bind(new InetSocketAddress(this.host, this.clientsPort));
            clientsHandlerStarted = clientsChannel.isBound();
            if (clientsHandlerStarted) {
                this.channelGroup.add(clientsChannel);
            }
        } catch (ChannelException ignore) {
        }
        if (clientsHandlerStarted) {
            LOG.info("Bind clients listener on " + clientsPort + " port");
        } else {
            LOG.error("Can't bind clients listener on " + clientsPort + " port");
        }
    }

    private void bindServersListener() {
        try {
            final Channel serversChannel = bootstrap.bind(new InetSocketAddress(this.host, this.serversPort));
            serversHandlerStarted = serversChannel.isBound();
            if (serversHandlerStarted) {
                this.channelGroup.add(serversChannel);
            }
        } catch (ChannelException ignore) {
        }
        if (serversHandlerStarted) {
            LOG.info("Bind servers listener on " + serversPort + " port");
        } else {
            LOG.error("Can't bind servers listener on " + serversPort + " port");
        }
    }

    public void stop() {
        this.channelGroup.close().awaitUninterruptibly();

        if (this.bootstrap != null) {
            this.bootstrap.releaseExternalResources();
            this.bootstrap = null;
        }

        eventsProducer.stop();
        serverContext.reset();
    }

    public boolean isStarted() {
        return clientsHandlerStarted || serversHandlerStarted;
    }

    public synchronized void disconnectClient(final String accountId, final String userId) {
        final Channel channel = serverContext.getClientChannel(accountId, userId);
        if (channel != null) {
            channel.close().awaitUninterruptibly();
        }
    }
}
