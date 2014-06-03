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

import org.openjst.commons.database.model.*;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Sergey Grachev
 */
public final class EmptyDatabase implements Database {

    private static final Database INSTANCE = new EmptyDatabase();

    public static Database getInstance() {
        return INSTANCE;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public Table table(final String name) {
        return EmptyTable.getInstance();
    }

    @Override
    public Database addTable(final String tableName) {
        return EmptyDatabase.getInstance();
    }

    @Override
    public Collection<Table> tables() {
        return Collections.emptyList();
    }

    @Override
    public String ddlCreate() {
        return null;
    }

    @Override
    public String ddlDrop() {
        return null;
    }

    @Override
    public QuerySelect querySelect() {
        return EmptyQuerySelect.getInstance();
    }

    @Override
    public QueryUpdate queryUpdate() {
        return EmptyQueryUpdate.getInstance();
    }

    @Override
    public QueryInsert queryInsert() {
        return EmptyQueryInsert.getInstance();
    }
}
