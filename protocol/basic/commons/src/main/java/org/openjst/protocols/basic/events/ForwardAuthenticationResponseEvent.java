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

import org.openjst.protocols.basic.pdu.packets.AuthClientRequestPacket;
import org.openjst.protocols.basic.pdu.packets.AuthResponsePacket;
import org.openjst.protocols.basic.session.Session;

/**
 * @author Sergey Grachev
 */
public final class ForwardAuthenticationResponseEvent implements Event {

    private final Session session;
    private final AuthClientRequestPacket request;
    private final AuthResponsePacket response;

    public ForwardAuthenticationResponseEvent(final Session session, final AuthClientRequestPacket request, final AuthResponsePacket response) {
        this.session = session;
        this.request = request;
        this.response = response;
    }

    public Session getSession() {
        return session;
    }

    public AuthClientRequestPacket getRequest() {
        return request;
    }

    public AuthResponsePacket getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "ForwardAuthenticationResponseEvent{" +
                "session=" + session +
                ", request=" + request +
                ", response=" + response +
                '}';
    }
}
