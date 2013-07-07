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

package org.openjst.protocols.basic.server.context;

import org.openjst.protocols.basic.session.ClientSession;
import org.openjst.protocols.basic.session.ServerSession;
import org.openjst.protocols.basic.session.Session;

/**
 * @author Sergey Grachev
 */
final class SessionKey {

    private final byte type;
    private final String accountId;
    private final String clientId;

    public SessionKey(final String accountId, final String clientId) {
        this.type = 0;
        this.accountId = accountId;
        this.clientId = clientId;
    }

    public SessionKey(final String accountId) {
        this.type = 1;
        this.accountId = accountId;
        this.clientId = null;
    }

    public SessionKey(final Session session) {
        if (session instanceof ClientSession) {
            this.type = 0;
            this.accountId = session.getAccountId();
            this.clientId = session.getClientId();
        } else if (session instanceof ServerSession) {
            this.type = 1;
            this.accountId = session.getAccountId();
            this.clientId = null;
        } else {
            throw new IllegalArgumentException("Unsupported session type " + session.getClass().getName());
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SessionKey that = (SessionKey) o;
        return type == that.type
                && !(accountId != null ? !accountId.equals(that.accountId) : that.accountId != null)
                && !(clientId != null ? !clientId.equals(that.clientId) : that.clientId != null);
    }

    @Override
    public int hashCode() {
        int result = (int) type;
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        return result;
    }
}
