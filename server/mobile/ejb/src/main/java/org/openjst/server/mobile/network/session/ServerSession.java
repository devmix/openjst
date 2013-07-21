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

package org.openjst.server.mobile.network.session;

import org.openjst.protocols.basic.session.Session;
import org.openjst.server.commons.network.Actor;

/**
 * @author Sergey Grachev
 */
public final class ServerSession implements Actor<Long>, Session {

    private final String accountId;
    private final String secretKey;
    private final Long accountPersistenceId;

    public ServerSession(final Long accountPersistenceId, final String accountId, final String secretKey) {
        this.accountPersistenceId = accountPersistenceId;
        this.accountId = accountId;
        this.secretKey = secretKey;
    }

    @Override
    public Long getId() {
        return accountPersistenceId;
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    @Override
    public String getClientId() {
        return null;
    }

    public Long getAccountPersistenceId() {
        return accountPersistenceId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ServerSession that = (ServerSession) o;
        return !(accountPersistenceId != null ? !accountPersistenceId.equals(that.accountPersistenceId) : that.accountPersistenceId != null);
    }

    @Override
    public int hashCode() {
        return accountPersistenceId != null ? accountPersistenceId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ServerSession{" +
                "accountId='" + accountId + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", accountPersistenceId=" + accountPersistenceId +
                '}';
    }
}
