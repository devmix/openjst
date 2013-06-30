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

/**
 * @author Sergey Grachev
 */
public final class QueryOrderBy {

    private final boolean asc;
    private final String column;

    private QueryOrderBy(final boolean asc, final String column) {
        this.asc = asc;
        this.column = column;
    }

    public static QueryOrderBy desc(final String column) {
        return new QueryOrderBy(false, column);
    }

    public static QueryOrderBy asc(final String column) {
        return new QueryOrderBy(true, column);
    }

    public boolean isAsc() {
        return asc;
    }

    public String getColumn() {
        return column;
    }
}
