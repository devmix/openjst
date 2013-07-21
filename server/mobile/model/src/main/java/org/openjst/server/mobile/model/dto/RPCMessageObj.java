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

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.server.commons.network.Message;

/**
 * @author Sergey Grachev
 */
public final class RPCMessageObj implements Message<Long> {

    private final Long id;
    private final RPCMessageFormat format;
    private final byte[] data;
    private final String accountId;
    private final String clientId;

    public RPCMessageObj(final Long id, final RPCMessageFormat format, final byte[] data,
                         final String accountId, final String clientId) {
        this.id = id;
        this.format = format;
        this.data = data;
        this.accountId = accountId;
        this.clientId = clientId;
    }

    @Override
    public Long getId() {
        return id;
    }

    public RPCMessageFormat getFormat() {
        return format;
    }

    public byte[] getData() {
        return data;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "RPCMessageObj{" +
                "id=" + id +
                ", format=" + format +
                ", data=" + data +
                ", accountId='" + accountId + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}
