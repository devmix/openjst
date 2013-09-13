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
import org.openjst.server.commons.model.types.ProtocolType;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Date;

import static org.openjst.server.mobile.model.Queries.Client.*;

/**
 * @author Sergey Grachev
 */
@NamedQueries({
        @NamedQuery(name = GET_CLIENT_ID_OF_ACCOUNT_BY_AUTH_ID,
                query = "select e.id from Client e where e.account.id = :accountId and e.authId = :clientId"),

        @NamedQuery(name = FIND_CACHED_SECRET_KEY_OF,
                query = "select new org.openjst.server.mobile.model.dto.ClientAuthenticationObj(" +
                        "   e.account.id, e.id, e.password, e.passwordSalt" +
                        ") from Client e" +
                        " where e.allowServerAuthentication = true " +
                        "   and e.authId = :clientId and e.account.authId = :accountId"),

        @NamedQuery(name = FIND_BY_ACCOUNT_AND_CLIENT_NAME,
                query = "select e from Client e where e.id = :clientId and e.account.authId = :accountId"),

        @NamedQuery(name = SET_OFFLINE_STATUS_FOR_ALL,
                query = "update Client e set e.online = false"),

        @NamedQuery(name = SET_OFFLINE_STATUS,
                query = "update Client e set e.online = false where e.id = :clientId"),

        @NamedQuery(name = SET_ONLINE_STATUS,
                query = "update Client e" +
                        " set e.online = true," +
                        "   e.lastOnlineTime = current_timestamp," +
                        "   e.lastProtocolType = :protocolType," +
                        "   e.lastRemoteHost = :host" +
                        " where e.id = :clientId"),

        @NamedQuery(name = GET_ONLINE_LIST_OF,
                query = "select new org.openjst.server.mobile.model.dto.ClientConnectionObj(" +
                        "   e.id, a.id, e.authId, e.name, e.lastOnlineTime, e.lastProtocolType, e.lastRemoteHost" +
                        ")from Client e join e.account a" +
                        " where e.online = true"),

        @NamedQuery(name = GET_ONLINE_COUNT_OF,
                query = "select count(e.id) from Client e where e.online = true")
})
@Entity
@Table(name = Client.TABLE,
        uniqueConstraints = {@UniqueConstraint(columnNames = {Client.COLUMN_ACCOUNT_ID, Client.COLUMN_AUTH_ID})}
)
@SuppressWarnings("UnusedDeclaration")
public class Client extends AbstractAccountEntity {

    public static final String TABLE = "client";
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
    public static final String COLUMN_LAST_ONLINE_TIME = "last_online_time";
    public static final String COLUMN_LAST_REMOTE_HOST = "last_remote_host";

    public static final int DEFAULT_SALT_SIZE = 255;

    @NotEmpty
    @Size(min = 1, max = 255)
    @Column(name = COLUMN_AUTH_ID, length = 255, nullable = false)
    private String authId;

    @Size(min = 1, max = 255)
    @Column(name = COLUMN_PASSWORD, length = 255)
    private String password;

    @Size(min = 8, max = DEFAULT_SALT_SIZE)
    @Column(name = COLUMN_PASSWORD_SALT, length = DEFAULT_SALT_SIZE)
    private byte[] passwordSalt;

    @Size(min = 1, max = 255)
    @Column(name = COLUMN_NAME, length = 255)
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
    private ProtocolType lastProtocolType;

    @Column(name = COLUMN_LAST_SYNC_TIME)
    private Date lastSyncTime = new Date();

    @Column(name = COLUMN_LAST_ONLINE_TIME)
    private Date lastOnlineTime;

    @Column(name = COLUMN_LAST_REMOTE_HOST)
    private String lastRemoteHost;

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

    public ProtocolType getLastProtocolType() {
        return lastProtocolType;
    }

    public void setLastProtocolType(final ProtocolType lastProtocolType) {
        this.lastProtocolType = lastProtocolType;
    }

    public Date getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(final Date lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public Date getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(final Date lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    public String getLastRemoteHost() {
        return lastRemoteHost;
    }

    public void setLastRemoteHost(final String lastRemoteHost) {
        this.lastRemoteHost = lastRemoteHost;
    }
}
