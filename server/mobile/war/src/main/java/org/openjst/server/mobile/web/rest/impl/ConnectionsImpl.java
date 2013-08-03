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

package org.openjst.server.mobile.web.rest.impl;

import org.jboss.resteasy.annotations.Form;
import org.openjst.server.commons.cdi.interceptors.UIService;
import org.openjst.server.commons.mq.ModelQuery;
import org.openjst.server.commons.mq.QueryListParams;
import org.openjst.server.commons.mq.QueryResult;
import org.openjst.server.commons.mq.access.ModelAccessRestriction;
import org.openjst.server.mobile.dao.AccountDAO;
import org.openjst.server.mobile.dao.ClientDAO;
import org.openjst.server.mobile.model.dto.AccountConnectionObj;
import org.openjst.server.mobile.model.dto.ClientConnectionObj;
import org.openjst.server.mobile.web.rest.Connections;

import javax.ejb.EJB;

import static org.openjst.server.commons.mq.queries.VoidQuery.*;

/**
 * @author Sergey Grachev
 */
@UIService
@ModelAccessRestriction
public class ConnectionsImpl implements Connections {

    @EJB
    private ClientDAO clientDAO;

    @EJB
    private AccountDAO accountDAO;

    @Override
    public QueryResult<ClientConnectionObj> clientList(@Form final QueryListParams parameters) {
        final ModelQuery<Filter, Order, Search> query = ModelQuery.Builder.newInstance(parameters).build();
        return QueryResult.Builder.<ClientConnectionObj>newInstanceFor(query)
                .total(clientDAO.getOnlineCountOf(query))
                .add(clientDAO.getOnlineListOf(query))
                .build();
    }

    @Override
    public QueryResult<AccountConnectionObj> accountList(@Form final QueryListParams parameters) {
        final ModelQuery<Filter, Order, Search> query = ModelQuery.Builder.newInstance(parameters).build();
        return QueryResult.Builder.<AccountConnectionObj>newInstanceFor(query)
                .total(accountDAO.getOnlineCountOf(query))
                .add(accountDAO.getOnlineListOf(query))
                .build();
    }
}
