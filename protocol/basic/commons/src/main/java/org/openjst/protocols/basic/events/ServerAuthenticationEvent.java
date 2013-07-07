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

import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.AuthClientRequestPacket;
import org.openjst.protocols.basic.session.Session;

/**
 * @author Sergey Grachev
 */
public final class ServerAuthenticationEvent {

    private final AbstractAuthPacket packet;
    private boolean requireForwarding = false;
    private Session session;

    public ServerAuthenticationEvent(final AbstractAuthPacket packet) {
        this.packet = packet;
    }

    public ServerAuthenticationEvent forward() {
        if (!(packet instanceof AuthClientRequestPacket)) {
            throw new IllegalStateException("Forwarding allowed only for AuthClientRequestPacket");
        }
        requireForwarding = true;
        return this;
    }

    public Session getSession() {
        return session;
    }

    public AbstractAuthPacket getPacket() {
        return packet;
    }

    public ServerAuthenticationEvent assignSession(final Session session) {
        this.session = session;
        return this;
    }

    public boolean isRequireForwarding() {
        return requireForwarding;
    }

    @Override
    public String toString() {
        return "ServerAuthenticationEvent{" +
                "packet=" + packet +
                ", requireForwarding=" + requireForwarding +
                ", session=" + session +
                '}';
    }
}
