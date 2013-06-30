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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Sergey Grachev
 */
public abstract class AbstractQueryInsert implements QueryInsert {

    protected String table;
    protected Map<String, Object> set = new TreeMap<String, Object>();

    @Override
    public String getTable() {
        return table;
    }

    @Override
    public QueryInsert table(final String table) {
        this.table = table;
        return this;
    }

    @Override
    public QueryInsert value(final String key, final Object value) {
        set.put(key, value);
        return this;
    }

    @Override
    public Map<String, Object> values() {
        return set;
    }

    @Override
    public String toSql() {
        return toSql(null);
    }

    @Override
    public String toSql(@Nullable final List<Object> parameters) {
        final StringBuilder sb = new StringBuilder("INSERT INTO ").append(table);
        sqlColumns(sb);
        sqlValues(sb, parameters);
        return sb.toString();
    }

    private void sqlColumns(final StringBuilder sb) {
        sb.append(" (");
        boolean first = true;
        for (final String key : set.keySet()) {
            if (!first) {
                sb.append(',');
            } else {
                first = false;
            }
            sb.append(key);
        }
        sb.append(") ");
    }

    private void sqlValues(final StringBuilder sb, @Nullable final List<Object> parameters) {
        sb.append(" VALUES (");
        if (parameters != null) {
            boolean first = true;
            for (final Object value : set.values()) {
                if (!first) {
                    sb.append(',');
                } else {
                    first = false;
                }
                sb.append("?");
                parameters.add(value);
            }
        } else {
            for (int i = 0, count = set.size(); i < count; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append('?');
            }
        }
        sb.append(") ");
    }
}
