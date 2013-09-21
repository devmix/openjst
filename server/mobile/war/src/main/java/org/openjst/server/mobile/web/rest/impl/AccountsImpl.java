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

import org.openjst.commons.checksum.MD5;
import org.openjst.commons.encodings.Base64;
import org.openjst.server.commons.mq.IOrder;
import org.openjst.server.commons.mq.ModelQuery;
import org.openjst.server.commons.mq.QueryListParams;
import org.openjst.server.commons.mq.access.ModelAccessRestriction;
import org.openjst.server.commons.mq.mapping.ExceptionMapping;
import org.openjst.server.commons.mq.queries.VoidQuery;
import org.openjst.server.commons.mq.results.QueryListResult;
import org.openjst.server.commons.mq.results.QuerySingleResult;
import org.openjst.server.commons.mq.results.Result;
import org.openjst.server.commons.web.interceptors.UIService;
import org.openjst.server.mobile.dao.AccountDAO;
import org.openjst.server.mobile.dao.RPCMessagesDAO;
import org.openjst.server.mobile.dao.UpdatesDAO;
import org.openjst.server.mobile.model.Account;
import org.openjst.server.mobile.mq.model.AccountModel;
import org.openjst.server.mobile.mq.model.AccountSummaryModel;
import org.openjst.server.mobile.mq.queries.AccountQuery;
import org.openjst.server.mobile.session.MobileSession;
import org.openjst.server.mobile.web.rest.Accounts;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sergey Grachev
 */
@UIService
@ModelAccessRestriction(requireAdministrator = true)
public class AccountsImpl implements Accounts {

    private static final AtomicLong API_KEY_COUNTER = new AtomicLong(0);

    @Inject
    private AccountDAO accountDAO;

    @Inject
    private RPCMessagesDAO rpcMessagesDAO;

    @Inject
    private UpdatesDAO updatesDAO;

    @Inject
    private MobileSession session;

    @Override
    public QueryListResult<AccountModel> list(final QueryListParams parameters) {
        final ModelQuery<VoidQuery.Filter, AccountQuery.Order, VoidQuery.Search> query = ModelQuery.Builder.newInstance(parameters)
                .orderBy(AccountQuery.Order.NAME, IOrder.Type.ASC)
                .build();

        return QueryListResult.Builder.<AccountModel>newInstanceFor(query)
                .total(accountDAO.getCountOf(query))
                .convert(accountDAO.getListOf(query), AccountModel.ACCOUNT_TO_MODEL)
                .build();
    }

    @ExceptionMapping(unique = true, uniqueFields = {"authId", "apiKey"})
    @Override
    public QuerySingleResult<AccountModel> create(final AccountModel model) {
        return read(accountDAO.create(model).getId());
    }

    @Override
    public QuerySingleResult<AccountModel> read(final Long id) {
        final Account e = accountDAO.findById(id);
        if (e == null) {
            return Result.notExists();
        }
        return QuerySingleResult.Builder.<AccountModel>newInstance().convert(e, AccountModel.ACCOUNT_TO_MODEL).build();
    }

    @ExceptionMapping(unique = true, uniqueFields = {"authId", "apiKey"})
    @Override
    public QuerySingleResult<AccountModel> update(final Long id, final AccountModel model) {
        final Account entity = accountDAO.findById(id);
        if (entity == null) {
            return Result.notExists();
        }
        return read(accountDAO.update(entity, model).getId());
    }

    @Override
    public QuerySingleResult<AccountModel> delete(final Long id) {
        accountDAO.delete(id);
        return Result.ok();
    }

    @Override
    public QuerySingleResult<String> generateAccountAPIKey(final Long accountId) {
        final String account = String.valueOf(accountId) + '-' + System.nanoTime() + '-' + API_KEY_COUNTER.getAndIncrement();
        final String key = Base64.encodeToString(MD5.checksum(account.getBytes()), false).replaceAll("=", "");
        return QuerySingleResult.Builder.<String>newInstance().value(key).build();
    }

    @Override
    public QuerySingleResult<AccountSummaryModel> getSummary(final Long accountId) {
        final Account e = accountDAO.findById(session.isAdministrator() ? accountId : session.getAccountId());
        if (e == null) {
            return Result.notExists();
        }

        final AccountSummaryModel model = AccountSummaryModel.ACCOUNT_TO_MODEL.map(e);

        model.setClientsCount(accountDAO.getCountOfClients(e.getId()));
        model.setUsersCount(accountDAO.getCountOfUsers(e.getId()));

        model.setMessagesFromClient(rpcMessagesDAO.getCountFromClients(e.getId()));
        model.setMessagesFromServer(rpcMessagesDAO.getCountFromServer(e.getId()));
        model.setMessagesToClient(rpcMessagesDAO.getCountDeliveredToClients(e.getId()));
        model.setMessagesToServer(rpcMessagesDAO.getCountDeliveredToServer(e.getId()));

        model.setUpdatesCount(updatesDAO.getCountForAccount(e.getId()).intValue());

        return QuerySingleResult.Builder.<AccountSummaryModel>newInstance().value(model).build();
    }
}
