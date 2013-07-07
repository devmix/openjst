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
import org.openjst.server.commons.model.types.LanguageCode;
import org.openjst.server.commons.model.types.RoleType;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Arrays;

/**
 * @author Sergey Grachev
 */
@NamedQueries({
        @NamedQuery(name = Queries.User.FIND_BY_ACCOUNT_AND_NAME,
                query = "select e from User e join fetch e.account" +
                        " where e.authId = :clientAuthId and e.account.authId = :accountAuthId"),

        @NamedQuery(name = Queries.User.FIND_BY_AUTH_ID,
                query = "select e from User e join fetch e.account where e.authId = :authId"),

        @NamedQuery(name = Queries.User.FIND_BY_ID,
                query = "select e from User e join fetch e.account where e.id = :id"),

        @NamedQuery(name = Queries.User.FIND_SYSTEM,
                query = "select e from User e where e.system = true"),

        @NamedQuery(name = Queries.User.FIND_SECRET_KEY_OF,
                query = "select e.id, e.password, e.passwordSalt from User e where e.authId = :user and e.account.authId = :account"),

        @NamedQuery(name = Queries.User.FIND_USER_ROLE,
                query = "select e.role from User e where e.id = :userId"),

        @NamedQuery(name = Queries.User.GET_LIST_OF,
                query = "select e from User e join fetch e.account where e.account.id = :accountId"),

        @NamedQuery(name = Queries.User.GET_COUNT_OF,
                query = "select count(e.id) from User e where e.account.id = :accountId")
})
@Entity
@Table(name = User.TABLE,
        uniqueConstraints = {@UniqueConstraint(columnNames = {User.COLUMN_ACCOUNT_ID, User.COLUMN_AUTH_ID})}
)
public class User extends AbstractIdEntity {

    public static final String TABLE = "user";
    public static final String COLUMN_ACCOUNT_ID = "account_id";
    public static final String COLUMN_AUTH_ID = "auth_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PASSWORD_SALT = "password_salt";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_LANGUAGE = "language";
    public static final String COLUMN_SYSTEM = "system";

    public static final int DEFAULT_SALT_SIZE = 32;

    @JoinColumn(name = COLUMN_ACCOUNT_ID, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Account account;

    @NotEmpty
    @Size(min = 1, max = 255)
    @Pattern(regexp = "[A-Za-z0-9\\-_]*")
    @Column(name = COLUMN_AUTH_ID, length = 255, nullable = false)
    private String authId;

    @NotEmpty
    @Size(min = 1, max = 255)
    @Column(name = COLUMN_NAME, length = 255, nullable = false)
    private String name;

    @Column(name = COLUMN_SYSTEM, nullable = false)
    private boolean system = false;

    @NotEmpty
    @Size(min = 1, max = 255)
    @Column(name = COLUMN_PASSWORD, length = 255, nullable = false)
    private String password;

    @NotEmpty
    @Size(min = 8, max = DEFAULT_SALT_SIZE)
    @Column(name = COLUMN_PASSWORD_SALT, length = 32, nullable = false)
    private byte[] passwordSalt;

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_ROLE, length = 8, nullable = false)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_LANGUAGE, length = 6, nullable = false)
    private LanguageCode language;

    public Account getAccount() {
        return account;
    }

    public void setAccount(final Account account) {
        this.account = account;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(final RoleType type) {
        this.role = type;
    }

    public LanguageCode getLanguage() {
        return language;
    }

    public void setLanguage(final LanguageCode language) {
        this.language = language;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(final boolean system) {
        this.system = system;
    }

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(final byte[] passwordSalt) {
        this.passwordSalt = Arrays.copyOf(passwordSalt, passwordSalt.length);
    }
}
