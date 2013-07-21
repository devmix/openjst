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
public final class ClientSession implements Actor<Long>, Session {

    private final String accountId;
    private final String clientId;
    private Long accountPersistenceId;
    private Long clientPersistenceId;

    public ClientSession(final Long accountPersistenceId, final Long clientPersistenceId, final String accountId, final String clientId) {
        this.accountPersistenceId = accountPersistenceId;
        this.clientPersistenceId = clientPersistenceId;
        this.accountId = accountId;
        this.clientId = clientId;
    }

    public ClientSession(final String accountId, final String clientId) {
        this(null, null, accountId, clientId);
    }

    public String getAccountId() {
        return accountId;
    }

    public String getClientId() {
        return clientId;
    }

    public Long getClientPersistenceId() {
        return clientPersistenceId;
    }

    public void setClientPersistenceId(final Long clientPersistenceId) {
        this.clientPersistenceId = clientPersistenceId;
    }

    public Long getAccountPersistenceId() {
        return accountPersistenceId;
    }

    public void setAccountPersistenceId(final Long accountPersistenceId) {
        this.accountPersistenceId = accountPersistenceId;
    }

    @Override
    public Long getId() {
        return clientPersistenceId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ClientSession session = (ClientSession) o;
        return !(clientPersistenceId != null ? !clientPersistenceId.equals(session.clientPersistenceId) : session.clientPersistenceId != null);
    }

    @Override
    public int hashCode() {
        return clientPersistenceId != null ? clientPersistenceId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClientSession{" +
                "accountId='" + accountId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", accountPersistenceId=" + accountPersistenceId +
                ", clientPersistenceId=" + clientPersistenceId +
                '}';
    }
}
