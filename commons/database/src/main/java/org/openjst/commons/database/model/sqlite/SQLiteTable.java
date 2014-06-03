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

import org.openjst.commons.database.model.AbstractTable;
import org.openjst.commons.database.model.Column;
import org.openjst.commons.database.model.Database;
import org.openjst.commons.database.model.Table;

/**
 * @author Sergey Grachev
 */
final class SQLiteTable extends AbstractTable {

    public SQLiteTable(final Database database, final String name) {
        super(database, name);
    }

    @Override
    public Table addColumn(final String name, final String type, final String modifiers) {
        final Column column = new SQLiteColumn(this, name, type, modifiers);
        synchronized (columns) {
            columns.put(column.name(), column);
        }
        return this;
    }
}
