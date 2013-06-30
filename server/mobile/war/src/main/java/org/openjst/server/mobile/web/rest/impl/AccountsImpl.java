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

import org.openjst.server.commons.cdi.interceptors.UIService;
import org.openjst.server.commons.mq.IOrder;
import org.openjst.server.commons.mq.ModelQuery;
import org.openjst.server.commons.mq.QueryListParams;
import org.openjst.server.commons.mq.QueryResult;
import org.openjst.server.commons.mq.access.ModelAccessRestriction;
import org.openjst.server.commons.mq.mapping.ExceptionMapping;
import org.openjst.server.commons.mq.queries.VoidQuery;
import org.openjst.server.mobile.dao.AccountDAO;
import org.openjst.server.mobile.model.Account;
import org.openjst.server.mobile.mq.model.AccountModel;
import org.openjst.server.mobile.mq.queries.AccountQuery;
import org.openjst.server.mobile.web.rest.Accounts;

import javax.inject.Inject;

/**
 * @author Sergey Grachev
 */
@UIService
@ModelAccessRestriction(requireAdministrator = true)
public class AccountsImpl implements Accounts {

    @Inject
    private AccountDAO accountDAO;

    @Override
    public QueryResult<AccountModel> list(final QueryListParams parameters) {
        final ModelQuery<VoidQuery.Filter, AccountQuery.Order, VoidQuery.Search> query = ModelQuery.Builder.newInstance(parameters)
                .orderBy(AccountQuery.Order.NAME, IOrder.Type.ASC)
                .build();

        return QueryResult.Builder.<AccountModel>newInstanceFor(query)
                .total(accountDAO.getCountOf(query))
                .convert(accountDAO.getListOf(query), AccountModel.ACCOUNT_TO_MODEL)
                .build();
    }

    @ExceptionMapping(unique = true, uniqueFields = "authId")
    @Override
    public QueryResult<AccountModel> create(final AccountModel model) {
        return read(accountDAO.create(model).getId());
    }

    @Override
    public QueryResult<AccountModel> read(final Long id) {
        final Account e = accountDAO.findById(id);
        if (e == null) {
            return QueryResult.notExists();
        }
        return QueryResult.Builder.<AccountModel>newInstance().convert(e, AccountModel.ACCOUNT_TO_MODEL).build();
    }

    @ExceptionMapping(unique = true, uniqueFields = "authId")
    @Override
    public QueryResult<AccountModel> update(final Long id, final AccountModel model) {
        final Account entity = accountDAO.findById(id);
        if (entity == null) {
            return QueryResult.notExists();
        }
        return read(accountDAO.update(entity, model).getId());
    }

    @Override
    public QueryResult<AccountModel> delete(final Long id) {
        accountDAO.delete(id);
        return QueryResult.ok();
    }
}
