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

package org.openjst.commons.database.model.sqlite;

import org.openjst.commons.database.model.*;

/**
 * @author Sergey Grachev
 */
final class SQLiteDatabase extends AbstractDatabase {

    public SQLiteDatabase(final String name) {
        super(name);
    }

    @Override
    public Database addTable(final String tableName) {
        final Table table = new SQLiteTable(this, tableName);
        synchronized (tables) {
            tables.put(table.name(), table);
        }
        return this;
    }

    @Override
    public QuerySelect querySelect() {
        return new SQLiteQuerySelect();
    }

    @Override
    public QueryUpdate queryUpdate() {
        return new SQLiteQueryUpdate();
    }

    @Override
    public QueryInsert queryInsert() {
        return new SQLiteQueryInsert();
    }
}
