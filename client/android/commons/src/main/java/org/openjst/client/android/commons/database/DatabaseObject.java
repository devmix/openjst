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

package org.openjst.client.android.commons.database;

import android.content.ContentValues;
import android.database.Cursor;
import org.openjst.commons.database.model.QueryInsert;
import org.openjst.commons.database.model.QuerySelect;
import org.openjst.commons.database.model.QueryUpdate;

/**
 * @author Sergey Grachev
 */
public interface DatabaseObject {

    void open();

    void close();

    boolean isOpened();

    QuerySelect querySelect();

    QueryInsert queryInsert();

    QueryUpdate queryUpdate();

    Cursor rawQuery(String sql, String[] selectionArgs);

    long insert(String table, String nullColumnHack, ContentValues values);

    int update(String table, ContentValues values, String whereClause, String[] whereArgs);

    void updateSQL(String sql, Object[] bindArgs);
}
