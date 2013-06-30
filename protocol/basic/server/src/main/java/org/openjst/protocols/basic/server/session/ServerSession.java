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

package org.openjst.protocols.basic.server.session;

import org.openjst.protocols.basic.sessions.Session;

/**
 * @author Sergey Grachev
 */
public class ServerSession implements Session {

    private final String accountId;
    private final String clientId;

    private int channelId;

    public ServerSession(final String accountId, final String clientId) {
        this.accountId = accountId;
        this.clientId = clientId;
    }

    public ServerSession(final ServerSession session) {
        this.accountId = session.getAccountId();
        this.clientId = session.getClientId();
    }

    public int getChannelId() {
        return channelId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setChannelId(final int channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        return "ServerSession{" +
                "accountId='" + accountId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", channelId=" + channelId +
                '}';
    }
}
