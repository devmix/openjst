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

package org.openjst.protocols.basic.context;

import org.jboss.netty.channel.ChannelFuture;

/**
 * @author Sergey Grachev
 */
public final class SendFuture {

    private static final long TIMEOUT = 10000;

    private final AbstractContext ctx;
    private final ChannelFuture channelFuture;
    private final long packetId;
    private int responseStatus = -1;
    private boolean done = false;
    private boolean fail = false;

    public SendFuture(final AbstractContext ctx, final ChannelFuture channelFuture, final long packetId) {
        this.ctx = ctx;
        this.channelFuture = channelFuture;
        this.packetId = packetId;
    }

    public SendFuture await() {
        try {
            channelFuture.await();
            synchronized (this) {
                if (!done) {
                    this.wait(TIMEOUT);
                }
            }
        } catch (InterruptedException ignore) {
        } finally {
            ctx.removeSendFuture(this);
        }

        if (responseStatus == -1) {
            this.fail = true;
        }

        return this;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public long getPacketId() {
        return packetId;
    }

    public void done(final int responseStatus) {
        if (done) {
            return;
        }
        this.responseStatus = responseStatus;
        synchronized (this) {
            done = true;
            notifyAll();
        }
        ctx.removeSendFuture(this);
    }

    public boolean isDone() {
        return done;
    }

    public boolean isFail() {
        return fail;
    }
}
