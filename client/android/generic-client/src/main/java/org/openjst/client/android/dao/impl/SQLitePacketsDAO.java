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

import android.content.ContentValues;
import org.openjst.client.android.commons.inject.annotations.Inject;
import org.openjst.client.android.commons.inject.annotations.Singleton;
import org.openjst.client.android.commons.managers.SessionManager;
import org.openjst.client.android.dao.PacketsDAO;
import org.openjst.client.android.db.ClientDB;
import org.openjst.commons.rpc.RPCMessageFormat;

import static org.openjst.client.android.db.impl.SQLiteClientDB.*;

/**
 * @author Sergey Grachev
 */
@Singleton(lazy = false)
public final class SQLitePacketsDAO implements PacketsDAO {

    @Inject
    private ClientDB db;

    @Inject
    private SessionManager session;

    public void outPersist(final String uuid, final RPCMessageFormat format, final byte[] data) {
        final ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_ID, session.getAccountId());
        values.put(COLUMN_CLIENT_ID, session.getClientId());
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(COLUMN_UUID, uuid);
        values.put(COLUMN_FORMAT, format.name());
        values.put(COLUMN_DATA, data);
        values.put(COLUMN_SIZE, data.length);
        values.put(COLUMN_STATUS, PacketStatus.IDLE.toString());
        db.insert(TABLE_PROTOCOL_RPC_OUT, null, values);
    }

    public void outStatus(final String uuid, final PacketStatus sent) {
        final ContentValues values = new ContentValues(1);
        values.put(COLUMN_STATUS, PacketStatus.IDLE.toString());
        db.update(TABLE_PROTOCOL_RPC_OUT, values, COLUMN_UUID + "=?", new String[]{String.valueOf(uuid)});
    }

    public void inPersist(final String uuid, final RPCMessageFormat format, final byte[] data) {
        final ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_ID, session.getAccountId());
        values.put(COLUMN_CLIENT_ID, session.getClientId());
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(COLUMN_UUID, uuid);
        values.put(COLUMN_FORMAT, format.name());
        values.put(COLUMN_DATA, data);
        values.put(COLUMN_SIZE, data.length);
        values.put(COLUMN_STATUS, PacketStatus.RECEIVED.toString());
        db.insert(TABLE_PROTOCOL_RPC_IN, null, values);
    }

    public void inResponse(final String uuid) {
        // TODO
    }

    public void inStatus(final String uuid, final PacketStatus status) {
        // TODO
    }
}
