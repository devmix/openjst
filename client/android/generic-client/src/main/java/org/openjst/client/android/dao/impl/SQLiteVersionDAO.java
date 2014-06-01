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

import android.database.Cursor;
import org.jetbrains.annotations.Nullable;
import org.openjst.client.android.commons.inject.annotations.Inject;
import org.openjst.client.android.commons.inject.annotations.Singleton;
import org.openjst.client.android.commons.managers.SessionManager;
import org.openjst.client.android.commons.utils.DatabaseUtils;
import org.openjst.client.android.dao.VersionDAO;
import org.openjst.client.android.db.ClientDB;
import org.openjst.commons.dto.ApplicationVersion;

import static org.openjst.client.android.commons.utils.DatabaseUtils.*;
import static org.openjst.client.android.db.impl.SQLiteClientDB.*;
import static org.openjst.commons.database.model.QueryOrderBy.desc;
import static org.openjst.commons.database.model.QueryWhere.and;
import static org.openjst.commons.database.model.QueryWhere.eq;

/**
 * @author Sergey Grachev
 */
@Singleton(lazy = false)
public final class SQLiteVersionDAO implements VersionDAO {

    @Inject
    private ClientDB db;

    @Inject
    private SessionManager session;

    public void add(final ApplicationVersion version) {
        insert(db, db.queryInsert().table(TABLE_VERSIONS)
                .value(COLUMN_ACCOUNT_ID, session.getAccountId())
                .value(COLUMN_CLIENT_ID, session.getClientId())
                .value(COLUMN_TIMESTAMP, version.getTimestamp())
                .value(COLUMN_MAJOR, version.getMajor())
                .value(COLUMN_MINOR, version.getMinor())
                .value(COLUMN_BUILD, version.getBuild())
                .value(COLUMN_DESCRIPTION, version.getDescription()));
    }

    @Override
    public void update(final ApplicationVersion version) {
        final Long id = findVersionId(version.getMajor(), version.getMinor(), version.getBuild());
        if (id != null) {
            DatabaseUtils.update(db, db.queryUpdate().table(TABLE_VERSIONS)
                    .set(COLUMN_DESCRIPTION, version.getDescription())
                    .set(COLUMN_TIMESTAMP, version.getTimestamp())
                    .where(and(
                            eq(COLUMN_ACCOUNT_ID, session.getAccountId()),
                            eq(COLUMN_CLIENT_ID, session.getClientId()),
                            eq(COLUMN_MAJOR, version.getMajor()),
                            eq(COLUMN_MINOR, version.getMinor()),
                            eq(COLUMN_BUILD, version.getBuild())
                    )));
        } else {
            add(version);
        }
    }

    @Override
    public void remove(final ApplicationVersion version) {

    }

    @Nullable
    public Long findVersionId(final int major, final int minor, final int build) {
        return selectId(db, db.querySelect()
                .column(COLUMN_ID).from(TABLE_VERSIONS)
                .where(and(
                        eq(COLUMN_ACCOUNT_ID, session.getAccountId()),
                        eq(COLUMN_CLIENT_ID, session.getClientId()),
                        eq(COLUMN_MAJOR, major),
                        eq(COLUMN_MINOR, minor),
                        eq(COLUMN_BUILD, build)
                ))
                .orderBy(desc(COLUMN_TIMESTAMP)));
    }

    @Nullable
    public ApplicationVersion getLatestVersion() {
        final Cursor cursor = select(db, db.querySelect()
                .column(COLUMN_TIMESTAMP).column(COLUMN_MAJOR).column(COLUMN_MINOR).column(COLUMN_BUILD).column(COLUMN_DESCRIPTION)
                .from(TABLE_VERSIONS)
                .where(and(
                        eq(COLUMN_ACCOUNT_ID, session.getAccountId()),
                        eq(COLUMN_CLIENT_ID, session.getClientId())))
                .orderBy(
                        desc(COLUMN_MAJOR), desc(COLUMN_MINOR), desc(COLUMN_BUILD), desc(COLUMN_TIMESTAMP)));

        try {
            if (cursor.moveToFirst()) {
                return new ApplicationVersion(cursor.getLong(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4));
            }
        } finally {
            cursor.close();
        }

        return null;
    }
}
