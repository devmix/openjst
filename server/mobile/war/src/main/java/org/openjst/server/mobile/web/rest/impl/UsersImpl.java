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
import org.openjst.server.commons.mq.QueryResult;
import org.openjst.server.commons.mq.mapping.ExceptionMapping;
import org.openjst.server.commons.mq.queries.VoidQuery;
import org.openjst.server.mobile.dao.UserDAO;
import org.openjst.server.mobile.model.User;
import org.openjst.server.mobile.mq.model.UserModel;
import org.openjst.server.mobile.mq.queries.UserQuery;
import org.openjst.server.mobile.web.rest.Users;

import javax.ejb.EJB;

/**
 * @author Sergey Grachev
 */
@UIService
public class UsersImpl implements Users {

    @EJB
    private UserDAO userDAO;

    @Override
    public QueryResult<UserModel> list(final ListParamsParameters parameters) {
        final ModelQuery<UserQuery.Filter, UserQuery.Order, VoidQuery.Search> query = ModelQuery.Builder.newInstance(parameters)
                .filterBy(UserQuery.Filter.ACCOUNT_ID, parameters.accountId)
                .orderBy(UserQuery.Order.NAME, IOrder.Type.ASC)
                .build();

        return QueryResult.Builder.<UserModel>newInstanceFor(query)
                .total(userDAO.getCountOf(query))
                .convert(userDAO.getListOf(query), UserModel.USER_TO_MODEL)
                .build();
    }

    @ExceptionMapping(unique = true, uniqueFields = "authId")
    @Override
    public QueryResult<UserModel> create(final UserModel model) {
        return read(userDAO.create(model).getId());
    }

    @Override
    public QueryResult<UserModel> read(final Long id) {
        final User e = userDAO.findById(id);
        if (e == null) {
            return QueryResult.notExists();
        }
        return QueryResult.Builder.<UserModel>newInstance().convert(e, UserModel.USER_TO_MODEL).build();
    }

    @Override
    public QueryResult<UserModel> update(final Long id, final UserModel model) {
        final User entity = userDAO.findById(id);
        if (entity == null) {
            return QueryResult.notExists();
        }
        userDAO.update(entity, model);
        return read(id);
    }

    @Override
    public QueryResult<UserModel> delete(final Long id) {
        userDAO.delete(id);
        return QueryResult.ok();
    }
}
