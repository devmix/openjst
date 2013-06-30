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
import org.openjst.commons.dto.tuples.Triple;
import org.openjst.server.commons.model.types.RoleType;
import org.openjst.server.commons.mq.ModelQuery;
import org.openjst.server.commons.mq.queries.VoidQuery;
import org.openjst.server.mobile.model.User;
import org.openjst.server.mobile.mq.model.UserModel;
import org.openjst.server.mobile.mq.queries.UserQuery;

import java.util.List;

/**
 * @author Sergey Grachev
 */
public interface UserDAO {

    @Nullable
    User findById(Long id);

    @Nullable
    User findByAuthId(String authId);

    @Nullable
    User findByAccountAndName(String accountAuthId, String clientAuthId);

    @Nullable
    Triple<Long, String, byte[]> findSecretKeyOf(String account, String user);

    @Nullable
    RoleType findUserRole(Long userId);

    List<User> getListOf(ModelQuery<UserQuery.Filter, UserQuery.Order, VoidQuery.Search> query);

    long getCountOf(ModelQuery<UserQuery.Filter, UserQuery.Order, VoidQuery.Search> query);

    User create(UserModel model);

    User update(User entity, UserModel model);

    void delete(Long id);
}
