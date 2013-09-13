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

package org.openjst.server.mobile.dao;

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.server.commons.mq.ModelQuery;
import org.openjst.server.commons.mq.queries.VoidQuery;
import org.openjst.server.mobile.model.Update;
import org.openjst.server.mobile.mq.model.UpdateModel;
import org.openjst.server.mobile.mq.queries.UpdateQuery;

import java.util.List;

/**
 * @author Sergey Grachev
 */
public interface UpdatesDAO {

    Pair<List<Update>, Long> getListOf(ModelQuery<UpdateQuery.Filter, UpdateQuery.Order, VoidQuery.Search> query);

    Update create(UpdateModel model);

    @Nullable
    Update findById(Long id);

    Update getById(Long id);

    Update update(Update entity, UpdateModel model);

    @Nullable
    Update update(Long id, UpdateModel model);

    void delete(Long id);

    Long getCountForAccount(Long id);
}
