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

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.compression.ZlibDecoder;
import org.jboss.netty.handler.codec.compression.ZlibEncoder;
import org.jboss.netty.handler.codec.compression.ZlibWrapper;
import org.openjst.protocols.basic.client.handlers.ClientHandler;
import org.openjst.protocols.basic.client.handlers.ClientSessionHandler;
import org.openjst.protocols.basic.decoder.ProtocolDecoder;
import org.openjst.protocols.basic.encoder.ProtocolEncoder;

public class ClientPipelineFactory implements ChannelPipelineFactory {
    private final ClientHandler handler;
    private final ClientSessionHandler sessionHandler;

    public ClientPipelineFactory(final ClientHandler handler, final ClientSessionHandler sessionHandler) {
        this.handler = handler;
        this.sessionHandler = sessionHandler;
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("deflater", new ZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast("inflater", new ZlibDecoder(ZlibWrapper.GZIP));

        pipeline.addLast("decoder", new ProtocolDecoder());
        pipeline.addLast("encoder", new ProtocolEncoder());

        pipeline.addLast("handler-session", sessionHandler);
        pipeline.addLast("handler-common", handler);

        return pipeline;
    }
}
