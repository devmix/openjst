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
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.openjst.commons.database.model.*;

/**
 * @author Sergey Grachev
 */
public abstract class AbstractSQLiteDatabaseObject extends SQLiteOpenHelper implements DatabaseObject {

    public AbstractSQLiteDatabaseObject(final Context context, final String name, final SQLiteDatabase.CursorFactory factory, final int version) {
        super(context, name, factory, version);
    }

    protected abstract Database db();

    @Override
    public void onCreate(final SQLiteDatabase db) {
        for (final Table table : db().tables()) {
            final String query = table.ddlCreate();
            Log.d(this.getClass().getSimpleName(), query);
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.w(this.getClass().getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        for (final Table table : db().tables()) {
            final String query = table.ddlDrop();
            Log.d(this.getClass().getSimpleName(), query);
            db.execSQL(query);
        }

        onCreate(db);
    }

    @Override
    public void open() {
        getReadableDatabase();
    }

    @Override
    public boolean isOpened() {
        return getReadableDatabase().isOpen();
    }

    @Override
    public QuerySelect querySelect() {
        return db().querySelect();
    }

    @Override
    public QueryInsert queryInsert() {
        return db().queryInsert();
    }

    @Override
    public QueryUpdate queryUpdate() {
        return db().queryUpdate();
    }

    @Override
    public Cursor rawQuery(final String sql, final String[] selectionArgs) {
        return getReadableDatabase().rawQuery(sql, selectionArgs);
    }

    @Override
    public long insert(final String table, final String nullColumnHack, final ContentValues values) {
        return getWritableDatabase().insert(table, nullColumnHack, values);
    }

    @Override
    public int update(final String table, final ContentValues values, final String whereClause, final String[] whereArgs) {
        return getWritableDatabase().update(table, values, whereClause, whereArgs);
    }

    @Override
    public void updateSQL(final String sql, final Object[] bindArgs) {
        getWritableDatabase().execSQL(sql, bindArgs);
    }
}
