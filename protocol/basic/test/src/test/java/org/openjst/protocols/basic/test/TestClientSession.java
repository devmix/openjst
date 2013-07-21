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

package org.openjst.protocols.basic.test;

import org.openjst.protocols.basic.session.Session;

/**
 * @author Sergey Grachev
 */
public final class TestClientSession implements Session {

    private final String accountId;
    private final String clientId;

    public TestClientSession(final String accountId, final String clientId) {
        this.accountId = accountId;
        this.clientId = clientId;
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "TestClientSession{" +
                "accountId='" + accountId + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}
