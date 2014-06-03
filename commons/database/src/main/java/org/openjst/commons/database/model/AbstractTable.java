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

import org.openjst.commons.database.model.empty.EmptyColumn;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Sergey Grachev
 */
public abstract class AbstractTable implements Table {

    protected final Database database;
    protected final String name;
    protected final Map<String, Column> columns;

    public AbstractTable(final Database database, final String name) {
        this.database = database;
        this.name = name;
        this.columns = new TreeMap<String, Column>();
    }

    @Override
    public Column column(final String name) {
        synchronized (columns) {
            return columns.containsKey(name) ? columns.get(name) : EmptyColumn.getInstance();
        }
    }

    @Override
    public Collection<Column> columns() {
        return columns != null ? columns.values() : Collections.<Column>emptyList();
    }

    @Override
    public String ddlCreate() {
        final StringBuilder sb = new StringBuilder("CREATE TABLE " + name() + "(");
        boolean first = true;
        for (final Column column : columns()) {
            if (first) {
                first = false;

            } else {
                sb.append(",");
            }
            sb.append(column.ddlCreate());
        }
        return sb.append(");").toString();
    }

    @Override
    public String ddlDrop() {
        return "DROP TABLE IF EXISTS " + name() + ";";
    }

    @Override

    public Database database() {
        return database;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return ddlCreate();
    }
}
