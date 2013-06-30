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
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.jetbrains.annotations.Nullable;
import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.openjst.client.android.commons.managers.SessionManager;
import org.openjst.client.android.commons.utils.DatabaseUtils;
import org.openjst.client.android.dao.SessionDAO;
import org.openjst.client.android.dto.TrafficSummary;
import org.openjst.commons.database.model.Database;
import org.openjst.commons.database.model.QueryOrderBy;
import org.openjst.commons.database.model.Table;
import org.openjst.commons.database.model.sqlite.SQLiteModelFactory;
import org.openjst.commons.dto.ApplicationVersion;
import org.openjst.protocols.basic.pdu.packets.RPCPacket;

import static org.openjst.commons.database.model.QueryWhere.and;
import static org.openjst.commons.database.model.QueryWhere.eq;

/**
 * @author Sergey Grachev
 */
@JSTInject(SessionDAO.class)
public final class SQLiteSessionDAO extends SQLiteOpenHelper implements SessionDAO {

    private static final int VERSION = 7;
    private static final String NAME = "session";
    private static final String TABLE_PROTOCOL_RPC_IN = "protocol_rpc_in";
    private static final String TABLE_PROTOCOL_RPC_OUT = "protocol_rpc_out";
    private static final String TABLE_PROTOCOL_PDU = "protocol_pdu";
    private static final String TABLE_VERSIONS = "versions";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_DATA = "data";
    private static final String COLUMN_SIZE = "size";
    private static final String COLUMN_UUID = "uuid";
    private static final String COLUMN_FORMAT = "format";
    private static final String COLUMN_CLIENT_ID = "client_id";
    private static final String COLUMN_ACCOUNT_ID = "account_id";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_MAJOR = "major";
    private static final String COLUMN_MINOR = "minor";
    private static final String COLUMN_BUILD = "build";
    private static final String COLUMN_DESCRIPTION = "description";

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

    @JSTInject
    private SessionManager session;

    public SQLiteSessionDAO(final Application session) {
        super(session, NAME, null, VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        for (final Table table : SQLiteSessionDAO.DB.tables()) {
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

        for (final Table table : SQLiteSessionDAO.DB.tables()) {
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

    public void outPersist(final RPCPacket packet) {
        final ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_ID, session.getAccountId());
        values.put(COLUMN_CLIENT_ID, session.getClientId());
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(COLUMN_UUID, packet.getUUID());
        values.put(COLUMN_FORMAT, packet.getFormat().toString());
        values.put(COLUMN_DATA, packet.getData());
        values.put(COLUMN_SIZE, packet.getData().length);
        values.put(COLUMN_STATUS, PacketStatus.IDLE.toString());
        getWritableDatabase().insert(TABLE_PROTOCOL_RPC_OUT, null, values);
    }

    public void outStatus(final long uuid, final PacketStatus sent) {
        final ContentValues values = new ContentValues(1);
        values.put(COLUMN_STATUS, PacketStatus.IDLE.toString());
        getWritableDatabase().update(TABLE_PROTOCOL_RPC_OUT, values, COLUMN_UUID + "=?", new String[]{String.valueOf(uuid)});
    }

    public void inPersist(final RPCPacket packet) {
        final ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_ID, session.getAccountId());
        values.put(COLUMN_CLIENT_ID, session.getClientId());
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(COLUMN_UUID, packet.getUUID());
        values.put(COLUMN_FORMAT, packet.getFormat().toString());
        values.put(COLUMN_DATA, packet.getData());
        values.put(COLUMN_SIZE, packet.getData().length);
        values.put(COLUMN_STATUS, PacketStatus.RECEIVED.toString());
        getWritableDatabase().insert(TABLE_PROTOCOL_RPC_IN, null, values);
    }

    public void inResponse(final long uuid) {
        // TODO
    }

    public void persistVersion(final ApplicationVersion version) {
        DatabaseUtils.insert(getWritableDatabase(), DB.queryInsert().table(TABLE_VERSIONS)
                .value(COLUMN_ACCOUNT_ID, session.getAccountId())
                .value(COLUMN_CLIENT_ID, session.getClientId())
                .value(COLUMN_TIMESTAMP, version.getTimestamp())
                .value(COLUMN_MAJOR, version.getMajor())
                .value(COLUMN_MINOR, version.getMinor())
                .value(COLUMN_BUILD, version.getBuild())
                .value(COLUMN_DESCRIPTION, version.getDescription()));
    }

    @Nullable
    public Long findVersionId(final int major, final int minor, final int build) {
        return DatabaseUtils.selectId(getReadableDatabase(), DB.querySelect()
                .column(COLUMN_ID).from(TABLE_VERSIONS)
                .where(and(
                        eq(COLUMN_ACCOUNT_ID, session.getAccountId()),
                        eq(COLUMN_CLIENT_ID, session.getClientId()),
                        eq(COLUMN_MAJOR, major),
                        eq(COLUMN_MINOR, minor),
                        eq(COLUMN_BUILD, build)
                )));
    }

    @Nullable
    public ApplicationVersion getLatestVersion() {
        final Cursor cursor = DatabaseUtils.select(getReadableDatabase(), DB.querySelect()
                .column(COLUMN_TIMESTAMP).column(COLUMN_MAJOR).column(COLUMN_MINOR).column(COLUMN_BUILD).column(COLUMN_DESCRIPTION)
                .from(TABLE_VERSIONS)
                .where(and(
                        eq(COLUMN_ACCOUNT_ID, session.getAccountId()),
                        eq(COLUMN_CLIENT_ID, session.getClientId())))
                .orderBy(
                        QueryOrderBy.desc(COLUMN_MAJOR), QueryOrderBy.desc(COLUMN_MINOR), QueryOrderBy.desc(COLUMN_BUILD), QueryOrderBy.desc(COLUMN_TIMESTAMP)));

        try {
            if (cursor.moveToFirst()) {
                return new ApplicationVersion(cursor.getLong(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4));
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    public TrafficSummary getTrafficSummary() {
        final TrafficSummary result = new TrafficSummary();

        result.plusRPCIn(DatabaseUtils.selectLong(getReadableDatabase(), DB.querySelect()
                .sum(COLUMN_SIZE).from(TABLE_PROTOCOL_RPC_IN)
                .where(and(eq(COLUMN_ACCOUNT_ID, session.getAccountId()), eq(COLUMN_CLIENT_ID, session.getClientId())))));

        result.plusRPCOut(DatabaseUtils.selectLong(getReadableDatabase(), DB.querySelect()
                .sum(COLUMN_SIZE).from(TABLE_PROTOCOL_RPC_OUT)
                .where(and(eq(COLUMN_ACCOUNT_ID, session.getAccountId()), eq(COLUMN_CLIENT_ID, session.getClientId())))));

        return result;
    }

    public void inStatus(final long uuid, final PacketStatus status) {
        // TODO
    }
}
