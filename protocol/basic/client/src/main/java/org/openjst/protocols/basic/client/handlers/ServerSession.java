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

package org.openjst.protocols.basic.client.handlers;

import org.openjst.commons.conversion.encodings.Base64;
import org.openjst.protocols.basic.pdu.packets.AuthServerRequestPacket;
import org.openjst.protocols.basic.session.Session;

/**
 * @author Sergey Grachev
 */
final class ServerSession implements Session {

    private final String accountId;
    private final String apiKey;

    public ServerSession(final String accountId, final String apiKey) {
        this.accountId = accountId;
        this.apiKey = apiKey;
    }

    public ServerSession(final AuthServerRequestPacket request) {
        this(null, Base64.encodeToString(request.getSecretKey().get(), false));
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    @Override
    public String getClientId() {
        return apiKey;
    }

    @Override
    public String toString() {
        return "ServerSession{" +
                "accountId='" + accountId + '\'' +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
