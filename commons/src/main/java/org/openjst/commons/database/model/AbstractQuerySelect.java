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

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public abstract class AbstractQuerySelect implements QuerySelect {

    private final List<String> columns = new ArrayList<String>(0);
    private final List<String> from = new ArrayList<String>(0);
    private QueryWhere where;
    private QueryOrderBy[] orderBy;

    public AbstractQuerySelect() {
    }

    @Override
    public QuerySelect count(final String column) {
        columns.add("COUNT(" + column + ")");
        return this;
    }

    @Override
    public QuerySelect sum(final String column) {
        columns.add("SUM(" + column + ")");
        return this;
    }

    @Override

    public QuerySelect column(final String column) {
        columns.add(column);
        return this;
    }

    @Override
    public QuerySelect from(final String table) {
        from.add(table);
        return this;
    }

    @Override
    public QuerySelect where(final QueryWhere where) {
        this.where = where;
        return this;
    }

    @Override
    public QuerySelect orderBy(final QueryOrderBy... columns) {
        this.orderBy = columns;
        return this;
    }

    @Override
    public boolean hasWhere() {
        return where != null;
    }

    @Override
    public boolean hasOrderBy() {
        return orderBy != null && orderBy.length >= 0;
    }

    @Override
    public String toSql() {
        return toSql(null);
    }

    @Override
    public String toSql(@Nullable final List<Object> parameters) {
        final StringBuilder sb = new StringBuilder("SELECT ");
        sqlColumns(sb);
        sb.append(" FROM ");
        sqlFrom(sb);
        if (hasWhere()) {
            sb.append(" WHERE ");
            where.toSql(sb, parameters);
        }
        if (hasOrderBy()) {
            sb.append(" ORDER BY ");
            sqlOrderBy(sb);
        }
        return sb.toString();
    }

    protected void sqlOrderBy(final StringBuilder sb) {
        boolean first = true;
        for (final QueryOrderBy item : orderBy) {
            if (!first) {
                sb.append(',');
            } else {
                first = false;
            }
            sb.append(item.getColumn()).append(" ").append(item.isAsc() ? "ASC" : "DESC");
        }
    }

    protected void sqlColumns(final StringBuilder sb) {
        boolean first = true;
        for (final String column : columns) {
            if (!first) {
                sb.append(',');
            } else {
                first = false;
            }
            sb.append(column);
        }
    }

    protected void sqlFrom(final StringBuilder sb) {
        boolean first = true;
        for (final String table : from) {
            if (!first) {
                sb.append(',');
            } else {
                first = false;
            }
            sb.append(table);
        }
    }

    @Override
    public String toString() {
        return "AbstractQuerySelect{" +
                "columns=" + columns +
                ", from=" + from +
                ", where=" + where +
                ", orderBy=" + (orderBy == null ? null : Arrays.asList(orderBy)) +
                '}';
    }
}
