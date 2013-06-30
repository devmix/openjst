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

package org.openjst.client.android.dao.impl;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.openjst.client.android.dao.LogsDAO;
import org.openjst.commons.database.model.Database;
import org.openjst.commons.database.model.Table;
import org.openjst.commons.database.model.sqlite.SQLiteModelFactory;

/**
 * @author Sergey Grachev
 */
@JSTInject(LogsDAO.class)
public final class SQLiteArchiveDAO extends SQLiteOpenHelper implements LogsDAO {

    private static final int VERSION = 1;
    private static final String NAME = "archive";
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_PDU = "pdu";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_DATA = "data";
    private static final String COLUMN_SIZE = "size";

    private static final Database DB = SQLiteModelFactory.newInstance(NAME);

    static {
        DB
                .addTable(TABLE_MESSAGES).table(TABLE_MESSAGES)
                .addColumn(COLUMN_TIMESTAMP, "INTEGER", "NOT NULL")
                .addColumn(COLUMN_MESSAGE, "TEXT", "NOT NULL")
                .database()

                .addTable(TABLE_PDU).table(TABLE_PDU)
                .addColumn(COLUMN_TYPE, "INTEGER", "NOT NULL")
                .addColumn(COLUMN_SIZE, "INTEGER", "")
                .addColumn(COLUMN_DATA, "BLOB", "")
        ;
    }

    public SQLiteArchiveDAO(final Application application) {
        super(application, NAME, null, VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        for (final Table table : DB.tables()) {
            final String query = table.ddlCreate();
            Log.d(this.getClass().getSimpleName(), query);
            db.execSQL(query);
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.w(SQLiteArchiveDAO.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        for (final Table table : DB.tables()) {
            final String query = table.ddlDrop();
            Log.d(this.getClass().getSimpleName(), query);
            db.execSQL(query);
        }

        onCreate(db);
    }

    public void open() {
        getReadableDatabase();
    }

    public boolean isOpened() {
        return getReadableDatabase().isOpen();
    }

    public void errorSendRPC(final String host, final Integer port, final String accountId, final String clientId, final String message) {
        // TODO
    }
}
