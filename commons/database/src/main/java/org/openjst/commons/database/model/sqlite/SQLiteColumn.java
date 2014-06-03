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

import org.openjst.commons.database.model.AbstractColumn;

/**
 * @author Sergey Grachev
 */
final class SQLiteColumn extends AbstractColumn {

    public SQLiteColumn(final SQLiteTable table, final String name, final String type, final String modifiers) {
        super(table, name, type, modifiers);
    }
}
