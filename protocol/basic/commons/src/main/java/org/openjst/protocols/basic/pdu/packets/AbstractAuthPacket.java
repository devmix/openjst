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

package org.openjst.protocols.basic.pdu.packets;

import org.openjst.protocols.basic.pdu.beans.Parameter;

import java.util.List;

/**
 * @author Sergey Grachev
 */
public abstract class AbstractAuthPacket extends AbstractPacket {

    protected String clientId;
    protected long requestId;
    protected String accountId;
    protected List<Parameter> parameters;

    public String getAccountId() {
        return accountId;
    }

    public long getRequestId() {
        return requestId;
    }

    public String getClientId() {
        return clientId;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "AbstractAuthPacket{" +
                "clientId='" + clientId + '\'' +
                ", requestId=" + requestId +
                ", accountId='" + accountId + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
