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

package org.openjst.server.mobile.model.dto;

/**
 * @author Sergey Grachev
 */
public final class ClientAuthenticationObj {

    private final Long accountId;
    private final Long clientId;
    private final String password;
    private final byte[] salt;

    public ClientAuthenticationObj(final Long accountId, final Long clientId, final String password, final byte[] salt) {

        this.accountId = accountId;
        this.clientId = clientId;
        this.password = password;
        this.salt = salt;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getSalt() {
        return salt;
    }

    @Override
    public String toString() {
        return "ClientAuthenticationObj{" +
                "accountId=" + accountId +
                ", clientId=" + clientId +
                '}';
    }
}
