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

import org.openjst.commons.database.model.empty.EmptyTable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public abstract class AbstractDatabase implements Database {

    protected final String name;
    protected final Map<String, Table> tables;

    public AbstractDatabase(final String name) {
        this.name = name;
        this.tables = new HashMap<String, Table>();
    }

    @Override
    public Table table(final String name) {
        synchronized (tables) {
            return tables.containsKey(name) ? tables.get(name) : EmptyTable.getInstance();
        }
    }

    @Override
    public Collection<Table> tables() {
        return tables != null ? tables.values() : Collections.<Table>emptyList();
    }

    @Override
    public String ddlCreate() {
        final StringBuilder sb = new StringBuilder();
        for (final Table table : tables()) {
            sb.append(table.ddlCreate());
        }
        return sb.toString();
    }

    @Override
    public String ddlDrop() {
        final StringBuilder sb = new StringBuilder();
        for (final Table table : tables()) {
            sb.append(table.ddlDrop());
        }
        return sb.toString();
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
