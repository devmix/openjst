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

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.database.model.QueryOrderBy;
import org.openjst.commons.database.model.QuerySelect;
import org.openjst.commons.database.model.QueryWhere;
import org.openjst.commons.utils.Constants;

import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class EmptyQuerySelect implements QuerySelect {

    private static final QuerySelect INSTANCE = new EmptyQuerySelect();

    public static QuerySelect getInstance() {
        return INSTANCE;
    }

    @Override
    public QuerySelect count(final String column) {
        return this;
    }

    @Override
    public QuerySelect sum(final String column) {
        return this;
    }

    @Override
    public QuerySelect column(final String column) {
        return this;
    }

    @Override
    public QuerySelect from(final String table) {
        return this;
    }

    @Override
    public QuerySelect where(final QueryWhere where) {
        return this;
    }

    @Override
    public QuerySelect orderBy(final QueryOrderBy... columns) {
        return this;
    }

    @Override
    public boolean hasWhere() {
        return false;
    }

    @Override
    public boolean hasOrderBy() {
        return false;
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
