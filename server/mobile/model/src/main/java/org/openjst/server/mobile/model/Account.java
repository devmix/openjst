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

package org.openjst.server.mobile.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.openjst.server.commons.model.AbstractIdEntity;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;

/**
 * @author Sergey Grachev
 */
@NamedQueries({
        @NamedQuery(name = Queries.Account.FIND_BY_ID,
                query = "select e from Account e where e.id = :id"),

        @NamedQuery(name = Queries.Account.FIND_BY_AUTH_ID,
                query = "select e from Account e where e.authId = :authId"),

        @NamedQuery(name = Queries.Account.FIND_ACCOUNT_BY_API_KEY,
                query = "select new org.openjst.server.mobile.model.dto.AccountAuthenticationObj(" +
                        "   e.id, e.authId" +
                        ")from Account e where e.apiKey = :apiKey"),

        @NamedQuery(name = Queries.Account.GET_LIST_OF,
                query = "select e from Account e"),

        @NamedQuery(name = Queries.Account.GET_COUNT_OF,
                query = "select count(e.id) from Account e"),

        @NamedQuery(name = Queries.Account.FIND_SYSTEM,
                query = "select e from Account e where e.system = true")
})
@Entity
@Table(name = Account.TABLE)
public class Account extends AbstractIdEntity {

    public static final String TABLE = "account";
    public static final String COLUMN_AUTH_ID = "auth_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SYSTEM = "system";
    public static final String COLUMN_API_KEY = "api_key";

    @NotEmpty
    @Size(min = 1, max = 255)
    @Pattern(regexp = "[A-Za-z0-9\\-_]*")
    @Column(name = COLUMN_AUTH_ID, unique = true, length = 255, nullable = false)
    private String authId;

    @NotEmpty
    @Size(min = 1, max = 255)
    @Column(name = COLUMN_NAME, length = 255, nullable = false)
    private String name;

    @Column(name = COLUMN_SYSTEM, nullable = false)
    private boolean system = false;

    @Column(name = COLUMN_API_KEY, length = 255, unique = true)
    private String apiKey;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.REMOVE)
    private Collection<User> users;

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(final String authId) {
        this.authId = authId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(final Collection<User> users) {
        this.users = users;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(final boolean systemAccount) {
        this.system = systemAccount;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }
}
