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

/**
 * @author Sergey Grachev
 */
public final class AuthenticationFailEvent implements Event {

    private final int status;
    private final AbstractAuthPacket authRequest;

    public AuthenticationFailEvent(final int status, final AbstractAuthPacket authRequest) {
        this.status = status;
        this.authRequest = authRequest;
    }

    public int getStatus() {
        return status;
    }

    public AbstractAuthPacket getAuthRequest() {
        return authRequest;
    }

    @Override
    public String toString() {
        return "AuthenticationFailEvent{" +
                "status=" + status +
                ", authRequest=" + authRequest +
                '}';
    }
}
