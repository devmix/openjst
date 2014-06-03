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

package org.openjst.commons.database.model.empty;

import org.openjst.commons.database.model.QueryInsert;
import org.openjst.commons.dto.constants.Constants;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class EmptyQueryInsert implements QueryInsert {

    private static final QueryInsert INSTANCE = new EmptyQueryInsert();

    public static QueryInsert getInstance() {
        return INSTANCE;
    }

    @Override
    public String getTable() {
        return Constants.stringsEmpty();
    }

    @Override
    public QueryInsert table(final String table) {
        return this;
    }

    @Override
    public QueryInsert value(final String key, final Object value) {
        return this;
    }

    @Override
    public Map<String, Object> values() {
        return Collections.emptyMap();
    }

    @Override
    public String toSql() {
        return Constants.stringsEmpty();
    }

    @Override
    public String toSql(@Nullable final List<Object> parameters) {
        return Constants.stringsEmpty();
    }
}
