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
import javax.validation.constraints.Size;
import java.util.Arrays;

/**
 * @author Sergey Grachev
 */
@NamedQueries({
        @NamedQuery(name = Queries.Client.FIND_CACHED_SECRET_KEY_OF,
                query = "select e.password, e.passwordSalt from Client e" +
                        " where e.allowServerAuthentication = true and e.authId = :clientId and e.account.authId = :accountId")
})
@Entity
@Table(name = Client.TABLE,
        uniqueConstraints = {@UniqueConstraint(columnNames = {Client.COLUMN_ACCOUNT_ID, Client.COLUMN_AUTH_ID})}
)
@SuppressWarnings("UnusedDeclaration")
public class Client extends AbstractIdEntity {

    public static final String TABLE = "client";
    public static final String COLUMN_ACCOUNT_ID = "account_id";
    public static final String COLUMN_AUTH_ID = "authId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PASSWORD_SALT = "password_salt";
    public static final String COLUMN_ONLINE = "online";
    public static final String COLUMN_BLOCKED = "blocked";
    public static final String COLUMN_ALLOW_SERVER_AUTHENTICATION = "allow_server_authentication";
    public static final String COLUMN_LAST_CLIENT_VERSION = "last_client_version";
    public static final String COLUMN_LAST_CLIENT_TYPE = "last_client_type";
    public static final String COLUMN_LAST_PROTOCOL_TYPE = "last_protocol_type";
    public static final String COLUMN_LAST_SYNC_TIME = "last_sync_time";

    public static final int DEFAULT_SALT_SIZE = 255;

    @JoinColumn(name = COLUMN_ACCOUNT_ID, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Account account;

    @NotEmpty
    @Size(min = 1, max = 255)
    @Column(name = COLUMN_AUTH_ID, length = 255, nullable = false)
    private String authId;

    @Size(min = 1, max = 255)
    @Column(name = COLUMN_PASSWORD, length = 255, nullable = false)
    private String password;

    @Size(min = 8, max = DEFAULT_SALT_SIZE)
    @Column(name = COLUMN_PASSWORD_SALT, length = DEFAULT_SALT_SIZE, nullable = false)
    private byte[] passwordSalt;

    @NotEmpty
    @Size(min = 1, max = 255)
    @Column(name = COLUMN_NAME, length = 255, nullable = false)
    private String name;

    @Column(name = COLUMN_ONLINE, nullable = false)
    private boolean online = false;

    @Column(name = COLUMN_BLOCKED, nullable = false)
    private boolean blocked = false;

    @Column(name = COLUMN_ALLOW_SERVER_AUTHENTICATION, nullable = false)
    private boolean allowServerAuthentication = false;

    @Column(name = COLUMN_LAST_CLIENT_VERSION)
    private String lastClientVersion;

    @Column(name = COLUMN_LAST_CLIENT_TYPE)
    private String lastClientType;

    @Column(name = COLUMN_LAST_PROTOCOL_TYPE)
    private String lastProtocolType;

    @Column(name = COLUMN_LAST_SYNC_TIME)
    private String lastSyncTime;

    public Account getAccount() {
        return account;
    }

    public void setAccount(final Account account) {
        this.account = account;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(final String clientId) {
        this.authId = clientId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public byte[] getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(final byte[] passwordSalt) {
        this.passwordSalt = Arrays.copyOf(passwordSalt, passwordSalt.length);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean setOnline() {
        return online;
    }

    public void setOnline(final boolean presence) {
        this.online = presence;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(final boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isAllowServerAuthentication() {
        return allowServerAuthentication;
    }

    public void setAllowServerAuthentication(final boolean allowServerAuthentication) {
        this.allowServerAuthentication = allowServerAuthentication;
    }

    public String getLastClientVersion() {
        return lastClientVersion;
    }

    public void setLastClientVersion(final String lastClientVersion) {
        this.lastClientVersion = lastClientVersion;
    }

    public String getLastClientType() {
        return lastClientType;
    }

    public void setLastClientType(final String lastClientType) {
        this.lastClientType = lastClientType;
    }

    public String getLastProtocolType() {
        return lastProtocolType;
    }

    public void setLastProtocolType(final String lastProtocolType) {
        this.lastProtocolType = lastProtocolType;
    }

    public String getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(final String lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }
}
