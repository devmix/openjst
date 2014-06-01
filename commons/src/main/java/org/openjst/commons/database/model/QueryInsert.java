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

package org.openjst.commons.database.model;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public interface QueryInsert {

    String getTable();

    QueryInsert table(String table);

    QueryInsert value(String key, Object value);

    Map<String, Object> values();

    String toSql();

    String toSql(@Nullable List<Object> parameters);
}
