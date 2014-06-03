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

import org.openjst.commons.database.model.QueryUpdate;
import org.openjst.commons.database.model.QueryWhere;
import org.openjst.commons.dto.constants.Constants;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class EmptyQueryUpdate implements QueryUpdate {

    private static final QueryUpdate INSTANCE = new EmptyQueryUpdate();

    public static QueryUpdate getInstance() {
        return INSTANCE;
    }

    @Override
    public QueryUpdate table(final String table) {
        return this;
    }

    @Override
    public QueryUpdate set(final String key, final Object value) {
        return this;
    }

    @Override
    public QueryUpdate where(final QueryWhere where) {
        return this;
    }

    @Override
    public boolean hasWhere() {
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
