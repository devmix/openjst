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

package org.openjst.protocols.basic.utils;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.openjst.protocols.basic.pdu.PDU;

/**
 * @author Sergey Grachev
 */
public final class NettyUtils {

    private NettyUtils() {
    }

    public static void writeDownstream(final ChannelHandlerContext ctx, final PDU packet) {
        ctx.sendDownstream(new DownstreamMessageEvent(ctx.getChannel(), Channels.future(ctx.getChannel()), packet, null));
    }
}
