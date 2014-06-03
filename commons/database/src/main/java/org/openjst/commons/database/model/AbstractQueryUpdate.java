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
import java.util.TreeMap;

/**
 * @author Sergey Grachev
 */
public abstract class AbstractQueryUpdate implements QueryUpdate {

    protected String table;
    protected Map<String, Object> set = new TreeMap<String, Object>();
    protected QueryWhere where;

    @Override
    public QueryUpdate table(final String table) {
        this.table = table;
        return this;
    }

    @Override
    public QueryUpdate set(final String key, final Object value) {
        set.put(key, value);
        return this;
    }

    @Override
    public QueryUpdate where(final QueryWhere where) {
        this.where = where;
        return this;
    }

    @Override
    public boolean hasWhere() {
        return where != null;
    }

    @Override
    public String toSql() {
        return toSql(null);
    }

    @Override
    public String toSql(@Nullable final List<Object> parameters) {
        final StringBuilder sb = new StringBuilder("UPDATE ").append(table).append(" SET ");
        sqlSet(sb, parameters);
        if (hasWhere()) {
            sb.append(" WHERE ");
            where.toSql(sb, parameters);
        }
        return sb.toString();
    }

    private void sqlSet(final StringBuilder sb, @Nullable final List<Object> parameters) {
        boolean first = true;
        for (final Map.Entry<String, Object> entry : set.entrySet()) {
            if (!first) {
                sb.append(',');
            } else {
                first = false;
            }
            sb.append(entry.getKey()).append("=?");
            if (parameters != null) {
                parameters.add(entry.getValue());
            }
        }
    }
}
