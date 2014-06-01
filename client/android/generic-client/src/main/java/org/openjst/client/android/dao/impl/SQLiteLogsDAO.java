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
import org.openjst.client.android.dao.LogsDAO;
import org.openjst.client.android.db.LogsDB;

/**
 * @author Sergey Grachev
 */
@Singleton(lazy = false)
public final class SQLiteLogsDAO implements LogsDAO {

    @Inject
    private LogsDB db;

    @Override
    public void errorSendRPC(final String host, final Integer port, final String accountId, final String clientId, final String message) {
        // TODO
    }

}
