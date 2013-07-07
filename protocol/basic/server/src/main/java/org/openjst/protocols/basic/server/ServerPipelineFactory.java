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

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.compression.ZlibDecoder;
import org.jboss.netty.handler.codec.compression.ZlibWrapper;
import org.openjst.protocols.basic.decoder.ProtocolDecoder;
import org.openjst.protocols.basic.encoder.ProtocolEncoder;
import org.openjst.protocols.basic.handlers.ZlibEncoderFast;
import org.openjst.protocols.basic.server.context.ServerContext;
import org.openjst.protocols.basic.server.handlers.ServerHandler;
import org.openjst.protocols.basic.server.handlers.ServerSessionsHandler;

public class ServerPipelineFactory implements ChannelPipelineFactory {
    private final ServerEventsProducer eventsProducer;
    private final ServerContext serverContext;
    private final DefaultChannelGroup channelGroup;

    public ServerPipelineFactory(final ServerEventsProducer eventsProducer, final ServerContext serverContext,
                                 final DefaultChannelGroup channelGroup) {
        this.eventsProducer = eventsProducer;
        this.serverContext = serverContext;
        this.channelGroup = channelGroup;
    }

    public ChannelPipeline getPipeline() throws Exception {
        final ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("inflater", new ZlibDecoder(ZlibWrapper.GZIP));
        pipeline.addLast("deflater", new ZlibEncoderFast(ZlibWrapper.GZIP));

        pipeline.addLast("decoder", new ProtocolDecoder());
        pipeline.addLast("encoder", new ProtocolEncoder());

        pipeline.addLast("handler-sessions", new ServerSessionsHandler(eventsProducer, serverContext, channelGroup));
        pipeline.addLast("handler-common", new ServerHandler(eventsProducer, serverContext));

        return pipeline;
    }
}
