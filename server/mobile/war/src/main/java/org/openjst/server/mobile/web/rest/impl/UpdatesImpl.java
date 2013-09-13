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
import org.openjst.server.commons.mq.IOrder;
import org.openjst.server.commons.mq.ModelQuery;
import org.openjst.server.commons.mq.queries.VoidQuery;
import org.openjst.server.commons.mq.results.QueryListResult;
import org.openjst.server.commons.mq.results.QuerySingleResult;
import org.openjst.server.commons.mq.results.Result;
import org.openjst.server.mobile.dao.UpdatesDAO;
import org.openjst.server.mobile.model.Update;
import org.openjst.server.mobile.mq.model.UpdateModel;
import org.openjst.server.mobile.mq.queries.UpdateQuery;
import org.openjst.server.mobile.respository.UpdatesRepository;
import org.openjst.server.mobile.web.rest.Updates;

import javax.ejb.EJB;

import static org.openjst.server.mobile.mq.queries.UpdateQuery.Order;

/**
 * @author Sergey Grachev
 */
@UIService
public class UpdatesImpl implements Updates {

    @EJB
    private UpdatesDAO updatesDAO;

    @EJB
    private UpdatesRepository updatesRepository;

    @Override
    public QueryListResult<UpdateModel> list(@Form final Updates.ListParameters parameters) {
        final ModelQuery<UpdateQuery.Filter, UpdateQuery.Order, VoidQuery.Search> query = ModelQuery.Builder.newInstance(parameters)
                .filterBy(UpdateQuery.Filter.ACCOUNT_ID, parameters.accountId)
                .orderBy(Order.VERSION, IOrder.Type.ASC)
                .build();

        return QueryListResult.Builder.<UpdateModel>newInstanceFor(query)
                .convert(updatesDAO.getListOf(query), UpdateModel.ENTITY_TO_MODEL)
                .build();
    }

    @Override
    public QuerySingleResult<UpdateModel> create(final UpdateModel model) {
        return read(updatesDAO.create(model).getId());
    }

    @Override
    public QuerySingleResult<UpdateModel> read(final Long id) {
        final Update e = updatesDAO.findById(id);
        if (e == null) {
            return Result.notExists();
        }
        return QuerySingleResult.Builder.<UpdateModel>newInstance().convert(e, UpdateModel.ENTITY_TO_MODEL).build();
    }

    @Override
    public QuerySingleResult<UpdateModel> update(final Long id, final UpdateModel model) {
        final Update e = updatesDAO.update(id, model);
        if (e == null) {
            return Result.notExists();
        }
        return read(e.getId());
    }

    @Override
    public QuerySingleResult<UpdateModel> delete(final Long id) {
        updatesDAO.delete(id);
        return Result.ok();
    }
}
