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

package org.openjst.server.mobile.web.rest;

import org.openjst.server.commons.mq.IRestCrud;
import org.openjst.server.commons.mq.QueryListParams;
import org.openjst.server.mobile.mq.model.UserModel;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * @author Sergey Grachev
 */
@Path("/users")
public interface Users extends IRestCrud<UserModel, Users.ListParamsParameters> {

    final class ListParamsParameters extends QueryListParams {
        @QueryParam("accountId")
        public Long accountId;
    }
}
