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

package org.openjst.server.mobile.mq.model;

import org.openjst.commons.dto.ApplicationVersion;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.mq.IMapping;
import org.openjst.server.mobile.model.Account;

import java.util.Date;

/**
 * @author Sergey Grachev
 */
public final class AccountSummaryModel {

    public static final IMapping<Account, AccountSummaryModel> ACCOUNT_TO_MODEL = new IMapping<Account, AccountSummaryModel>() {
        @Override
        public AccountSummaryModel map(final Account value) {
            final AccountSummaryModel model = new AccountSummaryModel();
            model.setName(value.getName());
            model.setApiKey(value.getApiKey());
            model.setAuthId(value.getAuthId());
            model.setOnline(value.isOnline());
            model.setSystem(value.isSystem());
            model.setLastOnlineTime(value.getLastOnlineTime());
            model.setLastProtocolType(value.getLastProtocolType());
            model.setLastRemoteHost(value.getLastRemoteHost());
            return model;
        }
    };

    private String apiKey;
    private String authId;
    private String name;
    private boolean system;

    private int usersCount;
    private int clientsCount;

    private boolean online;

    private ProtocolType lastProtocolType;
    private Date lastOnlineTime;
    private String lastRemoteHost;

    private int messagesToClient;
    private int messagesToServer;
    private int messagesFromClient;
    private int messagesFromServer;

    private int updatesCount;

    private ApplicationVersion androidVersion;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
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

    public boolean isSystem() {
        return system;
    }

    public void setSystem(final boolean system) {
        this.system = system;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(final int usersCount) {
        this.usersCount = usersCount;
    }

    public int getClientsCount() {
        return clientsCount;
    }

    public void setClientsCount(final int clientsCount) {
        this.clientsCount = clientsCount;
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

    public void setLastOnlineTime(final Date lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    public String getLastRemoteHost() {
        return lastRemoteHost;
    }

    public void setLastRemoteHost(final String lastRemoteHost) {
        this.lastRemoteHost = lastRemoteHost;
    }

    public int getMessagesToClient() {
        return messagesToClient;
    }

    public void setMessagesToClient(final int messagesToClient) {
        this.messagesToClient = messagesToClient;
    }

    public int getMessagesToServer() {
        return messagesToServer;
    }

    public void setMessagesToServer(final int messagesToServer) {
        this.messagesToServer = messagesToServer;
    }

    public int getMessagesFromClient() {
        return messagesFromClient;
    }

    public void setMessagesFromClient(final int messagesFromClient) {
        this.messagesFromClient = messagesFromClient;
    }

    public int getMessagesFromServer() {
        return messagesFromServer;
    }

    public void setMessagesFromServer(final int messagesFromServer) {
        this.messagesFromServer = messagesFromServer;
    }

    public int getUpdatesCount() {
        return updatesCount;
    }

    public void setUpdatesCount(final int updatesCount) {
        this.updatesCount = updatesCount;
    }

    public ApplicationVersion getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(final ApplicationVersion androidVersion) {
        this.androidVersion = androidVersion;
    }
}
