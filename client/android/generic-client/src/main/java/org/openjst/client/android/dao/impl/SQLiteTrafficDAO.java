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

import org.openjst.client.android.commons.inject.annotations.Inject;
import org.openjst.client.android.commons.inject.annotations.Singleton;
import org.openjst.client.android.commons.managers.SessionManager;
import org.openjst.client.android.commons.utils.DatabaseUtils;
import org.openjst.client.android.dao.TrafficDAO;
import org.openjst.client.android.db.ClientDB;
import org.openjst.client.android.dto.TrafficSummary;

import static org.openjst.client.android.db.impl.SQLiteClientDB.*;
import static org.openjst.commons.database.model.QueryWhere.and;
import static org.openjst.commons.database.model.QueryWhere.eq;

/**
 * @author Sergey Grachev
 */
@Singleton(lazy = false)
public final class SQLiteTrafficDAO implements TrafficDAO {

    @Inject
    private ClientDB db;

    @Inject
    private SessionManager session;

    @Override
    public TrafficSummary getTrafficSummary() {
        final TrafficSummary result = new TrafficSummary();

        result.plusRPCIn(DatabaseUtils.selectLong(db, db.querySelect()
                .sum(COLUMN_SIZE).from(TABLE_PROTOCOL_RPC_IN)
                .where(and(eq(COLUMN_ACCOUNT_ID, session.getAccountId()), eq(COLUMN_CLIENT_ID, session.getClientId())))));

        result.plusRPCOut(DatabaseUtils.selectLong(db, db.querySelect()
                .sum(COLUMN_SIZE).from(TABLE_PROTOCOL_RPC_OUT)
                .where(and(eq(COLUMN_ACCOUNT_ID, session.getAccountId()), eq(COLUMN_CLIENT_ID, session.getClientId())))));

        return result;
    }
}
