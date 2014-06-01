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

package org.openjst.client.android.db.impl;

import android.content.Context;
import org.openjst.client.android.commons.database.AbstractSQLiteDatabaseObject;
import org.openjst.client.android.commons.inject.annotations.Singleton;
import org.openjst.client.android.db.ClientDB;
import org.openjst.commons.database.model.Database;
import org.openjst.commons.database.model.sqlite.SQLiteModelFactory;

/**
 * @author Sergey Grachev
 */
@Singleton
public final class SQLiteClientDB extends AbstractSQLiteDatabaseObject implements ClientDB {

    public static final int VERSION = 7;
    public static final String NAME = "session";
    public static final String TABLE_PROTOCOL_RPC_IN = "protocol_rpc_in";
    public static final String TABLE_PROTOCOL_RPC_OUT = "protocol_rpc_out";
    public static final String TABLE_PROTOCOL_PDU = "protocol_pdu";
    public static final String TABLE_VERSIONS = "versions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_FORMAT = "format";
    public static final String COLUMN_CLIENT_ID = "client_id";
    public static final String COLUMN_ACCOUNT_ID = "account_id";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_MAJOR = "major";
    public static final String COLUMN_MINOR = "minor";
    public static final String COLUMN_BUILD = "build";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final Database DB = SQLiteModelFactory.newInstance(NAME);

    static {
        DB
                .addTable(TABLE_PROTOCOL_RPC_IN).table(TABLE_PROTOCOL_RPC_IN)
                .addColumn(COLUMN_ID, "INTEGER", "PRIMARY KEY")
                .addColumn(COLUMN_CLIENT_ID, "TEXT", "NOT NULL")
                .addColumn(COLUMN_ACCOUNT_ID, "TEXT", "NOT NULL")
                .addColumn(COLUMN_TIMESTAMP, "INTEGER", "NOT NULL")
                .addColumn(COLUMN_UUID, "TEXT", "NOT NULL")
                .addColumn(COLUMN_FORMAT, "TEXT", "NOT NULL")
                .addColumn(COLUMN_STATUS, "TEXT", "NOT NULL")
                .addColumn(COLUMN_SIZE, "INTEGER", "NOT NULL")
                .addColumn(COLUMN_DATA, "BLOB", "NOT NULL")
                .database()

                .addTable(TABLE_PROTOCOL_RPC_OUT).table(TABLE_PROTOCOL_RPC_OUT)
                .addColumn(COLUMN_ID, "INTEGER", "PRIMARY KEY")
                .addColumn(COLUMN_CLIENT_ID, "TEXT", "NOT NULL")
                .addColumn(COLUMN_ACCOUNT_ID, "TEXT", "NOT NULL")
                .addColumn(COLUMN_TIMESTAMP, "INTEGER", "NOT NULL")
                .addColumn(COLUMN_UUID, "TEXT", "NOT NULL")
                .addColumn(COLUMN_FORMAT, "TEXT", "NOT NULL")
                .addColumn(COLUMN_STATUS, "TEXT", "NOT NULL")
                .addColumn(COLUMN_SIZE, "INTEGER", "NOT NULL")
                .addColumn(COLUMN_DATA, "BLOB", "NOT NULL")
                .database()

                .addTable(TABLE_VERSIONS).table(TABLE_VERSIONS)
                .addColumn(COLUMN_ID, "INTEGER", "PRIMARY KEY")
                .addColumn(COLUMN_CLIENT_ID, "TEXT", "NOT NULL")
                .addColumn(COLUMN_ACCOUNT_ID, "TEXT", "NOT NULL")
                .addColumn(COLUMN_TIMESTAMP, "INTEGER", "NOT NULL")
                .addColumn(COLUMN_MAJOR, "INTEGER", "NOT NULL")
                .addColumn(COLUMN_MINOR, "INTEGER", "NOT NULL")
                .addColumn(COLUMN_BUILD, "INTEGER", "NOT NULL")
                .addColumn(COLUMN_DESCRIPTION, "TEXT", "")
                .database()

                .addTable(TABLE_PROTOCOL_PDU).table(TABLE_PROTOCOL_PDU)
                .addColumn(COLUMN_TYPE, "INTEGER", "NOT NULL")
                .addColumn(COLUMN_SIZE, "INTEGER", "")
                .addColumn(COLUMN_DATA, "BLOB", "")
        ;
    }

    public SQLiteClientDB(final Context ctx) {
        super(ctx, NAME, null, VERSION);
    }

    @Override
    protected Database db() {
        return DB;
    }
}
