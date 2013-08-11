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

import static org.openjst.server.mobile.model.Queries.RPCMessage.*;

/**
 * @author Sergey Grachev
 */
@NamedQueries({
        @NamedQuery(name = GET_NOT_DELIVERED_TO_SERVERS,
                query = "select new org.openjst.server.mobile.model.dto.RPCMessageObj(" +
                        "   e.id, e.format, e.data, a.authId, c.authId " +
                        ")from RPCMessageForwardToServer e join e.account a join e.client c" +
                        " where a.id = :recipientId and (e.state is null or e.state not in (:successStates))" +
                        " order by e.created asc"),

        @NamedQuery(name = FILTER_SERVERS_WITH_NOT_DELIVERED_MESSAGES,
                query = "select distinct new org.openjst.server.mobile.model.dto.SimpleActorObj(" +
                        "   a.id, a.authId " +
                        ")from RPCMessageForwardToServer e join e.account a" +
                        " where e.account.id in (:recipients) and (e.state is null or e.state not in (:successStates))"),

        @NamedQuery(name = GET_COUNT_FROM_CLIENTS,
                query = "select count(e) from RPCMessageForwardToServer e where e.account.id = :accountId"),

        @NamedQuery(name = GET_COUNT_DELIVERED_TO_SERVER,
                query = "select count(e) from RPCMessageForwardToServer e" +
                        " where e.account.id = :accountId and e.state in (:state)")
})
@Entity
@DiscriminatorValue(value = "0")
public class RPCMessageForwardToServer extends RPCMessage {

}
