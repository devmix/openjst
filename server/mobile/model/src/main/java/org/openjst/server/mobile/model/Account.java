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

import org.openjst.server.commons.model.AbstractIdEntity;
import org.openjst.server.commons.model.types.ProtocolType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;

import static org.openjst.server.mobile.model.Queries.Account.*;

/**
 * @author Sergey Grachev
 */
@NamedQueries({
        @NamedQuery(name = FIND_BY_ID,
                query = "select e from Account e where e.id = :id"),

        @NamedQuery(name = FIND_BY_AUTH_ID,
                query = "select e from Account e where e.authId = :authId"),

        @NamedQuery(name = FIND_ACCOUNT_BY_API_KEY,
                query = "select new org.openjst.server.mobile.model.dto.AccountAuthenticationObj(" +
                        "   e.id, e.authId" +
                        ")from Account e where e.apiKey = :apiKey"),

        @NamedQuery(name = GET_LIST_OF,
                query = "select e from Account e"),

        @NamedQuery(name = GET_COUNT_OF,
                query = "select count(e.id) from Account e"),

        @NamedQuery(name = FIND_SYSTEM,
                query = "select e from Account e where e.system = true"),

        @NamedQuery(name = SET_ONLINE_STATUS,
                query = "update Account e" +
                        " set e.online = true," +
                        "   e.lastOnlineTime = current_timestamp," +
                        "   e.lastProtocolType = :protocolType," +
                        "   e.lastRemoteHost = :host" +
                        " where e.id = :accountId"),

        @NamedQuery(name = SET_OFFLINE_STATUS,
                query = "update Account e set e.online = false where e.id = :accountId"),

        @NamedQuery(name = GET_ONLINE_LIST_OF,
                query = "select new org.openjst.server.mobile.model.dto.AccountConnectionObj(" +
                        "   e.id, e.authId, e.name, e.lastOnlineTime, e.lastProtocolType, e.lastRemoteHost" +
                        ")from Account e" +
                        " where e.online = true"),

        @NamedQuery(name = GET_ONLINE_COUNT_OF,
                query = "select count(e.id) from Account e where e.online = true"),

        @NamedQuery(name = GET_COUNT_OF_CLIENTS,
                query = "select count(e) from Client e where e.account.id = :accountId"),

        @NamedQuery(name = GET_COUNT_OF_USERS,
                query = "select count(e) from User e where e.account.id = :accountId")
})
@Entity
@Table(name = Account.TABLE)
public class Account extends AbstractIdEntity {

    public static final String TABLE = "account";
    public static final String COLUMN_AUTH_ID = "auth_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SYSTEM = "system";
    public static final String COLUMN_API_KEY = "api_key";
    public static final String COLUMN_ONLINE = "online";
    public static final String COLUMN_LAST_PROTOCOL_TYPE = "last_protocol_type";
    public static final String COLUMN_LAST_ONLINE_TIME = "last_online_time";
    public static final String COLUMN_LAST_REMOTE_HOST = "last_remote_host";

    @NotNull
    @Size(min = 1, max = 255)
    @Pattern(regexp = "[A-Za-z0-9\\-_]*")
    @Column(name = COLUMN_AUTH_ID, unique = true, length = 255, nullable = false)
    private String authId;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = COLUMN_NAME, length = 255, nullable = false)
    private String name;

    @Column(name = COLUMN_SYSTEM, nullable = false)
    private boolean system = false;

    @Column(name = COLUMN_API_KEY, length = 255, unique = true)
    private String apiKey;

    @Column(name = COLUMN_ONLINE, nullable = false)
    private boolean online = false;

    @Column(name = COLUMN_LAST_PROTOCOL_TYPE)
    private ProtocolType lastProtocolType;

    @Column(name = COLUMN_LAST_ONLINE_TIME)
    private Date lastOnlineTime;

    @Column(name = COLUMN_LAST_REMOTE_HOST)
    private String lastRemoteHost;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.REMOVE)
    private Collection<User> users;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.REMOVE)
    private Collection<Client> clients;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.REMOVE)
    private Collection<RPCMessage> rpcMessages;

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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(final boolean online) {
        this.online = online;
    }

    public ProtocolType getLastProtocolType() {
        return lastProtocolType;
    }

    public void setLastProtocolType(final ProtocolType lastProtocolType) {
        this.lastProtocolType = lastProtocolType;
    }

    public Date getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(final Date lastConnectTime) {
        this.lastOnlineTime = lastConnectTime;
    }

    public String getLastRemoteHost() {
        return lastRemoteHost;
    }

    public void setLastRemoteHost(final String lastRemoteHost) {
        this.lastRemoteHost = lastRemoteHost;
    }

    public Collection<Client> getClients() {
        return clients;
    }

    public Collection<RPCMessage> getRpcMessages() {
        return rpcMessages;
    }
}
