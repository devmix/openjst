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

package org.openjst.protocols.basic.events;

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.protocols.basic.session.Session;

import java.util.Arrays;

/**
 * @author Sergey Grachev
 */
public final class ForwardRPCEvent implements Event {

    private final Session session;
    private final RPCMessageFormat format;
    private final byte[] data;

    public ForwardRPCEvent(final Session session, final RPCMessageFormat format, final byte[] data) {
        this.session = session;
        this.format = format;
        this.data = Arrays.copyOf(data, data.length);
    }

    public Session getSession() {
        return session;
    }

    public RPCMessageFormat getFormat() {
        return format;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ForwardRPCEvent{" +
                "clientId='" + session + '\'' +
                ", format=" + format +
                ", data=" + data +
                '}';
    }
}
