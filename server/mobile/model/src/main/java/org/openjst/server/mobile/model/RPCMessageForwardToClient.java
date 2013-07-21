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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * @author Sergey Grachev
 */
@NamedQueries({
        @NamedQuery(name = Queries.RPCMessage.GET_NOT_DELIVERED_TO_CLIENTS,
                query = "select new org.openjst.server.mobile.model.dto.RPCMessageObj(" +
                        "   e.id, e.format, e.data, a.authId, c.authId " +
                        ")from RPCMessageForwardToClient e join e.account a join e.client c" +
                        " where c.id = :recipientId and (e.state is null or e.state not in (:successStates))" +
                        " order by e.created asc"),

        @NamedQuery(name = Queries.RPCMessage.FILTER_CLIENTS_WITH_NOT_DELIVERED_MESSAGES,
                query = "select distinct new org.openjst.server.mobile.model.dto.SimpleActorObj(" +
                        "   c.id, c.authId " +
                        ")from RPCMessageForwardToClient e join e.client c" +
                        " where e.account.id in (:recipients) and (e.state is null or e.state not in (:successStates))")
})
@Entity
@DiscriminatorValue(value = "1")
public class RPCMessageForwardToClient extends RPCMessage {

}
