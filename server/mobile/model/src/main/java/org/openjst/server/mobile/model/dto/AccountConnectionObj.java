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

import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.mq.model.AbstractEntityModel;

import java.util.Date;

/**
 * @author Sergey Grachev
 */
public final class AccountConnectionObj extends AbstractEntityModel {

    private final String authId;
    private final String name;
    private final Date lastOnlineTime;
    private final ProtocolType lastProtocolType;
    private final String lastRemoteHost;

    public AccountConnectionObj(final long id, final String authId, final String name,
                                final Date lastOnlineTime, final ProtocolType lastProtocolType, final String lastRemoteHost) {

        this.id = id;
        this.authId = authId;
        this.name = name;
        this.lastOnlineTime = lastOnlineTime;
        this.lastProtocolType = lastProtocolType;
        this.lastRemoteHost = lastRemoteHost;
    }

    public String getAuthId() {
        return authId;
    }

    public String getName() {
        return name;
    }

    public Date getLastOnlineTime() {
        return lastOnlineTime;
    }

    public ProtocolType getLastProtocolType() {
        return lastProtocolType;
    }

    public String getLastRemoteHost() {
        return lastRemoteHost;
    }

    @Override
    public String toString() {
        return "AccountConnectionObj{" +
                "authId='" + authId + '\'' +
                ", name='" + name + '\'' +
                ", lastOnlineTime=" + lastOnlineTime +
                ", lastProtocolType=" + lastProtocolType +
                ", lastRemoteHost='" + lastRemoteHost + '\'' +
                '}';
    }
}
