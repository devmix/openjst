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

package org.openjst.server.mobile.jaas;

import java.security.Principal;

/**
 * @author Sergey Grachev
 */
public class MobilePrincipal implements Principal {

    private final String name;
    private final String account;
    private final String user;
    private final Long userId;

    public MobilePrincipal(final String name, final String account, final String user, final Long userId) {
        this.name = name;
        this.account = account;
        this.user = user;
        this.userId = userId;
    }

    public MobilePrincipal(final String name) {
        this(name, null, null, null);
    }

    @Override
    public String getName() {
        return name;
    }

    public String getAccount() {
        return account;
    }

    public String getUser() {
        return user;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MobilePrincipal that = (MobilePrincipal) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MobilePrincipal{" +
                "name='" + name + '\'' +
                ", account='" + account + '\'' +
                ", user='" + user + '\'' +
                ", userId=" + userId +
                '}';
    }
}
