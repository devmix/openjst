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

package org.openjst.client.android.commons.utils;

import android.content.ContentValues;
import android.database.Cursor;
import org.jetbrains.annotations.Nullable;
import org.openjst.client.android.commons.database.DatabaseObject;
import org.openjst.commons.database.model.QueryInsert;
import org.openjst.commons.database.model.QuerySelect;
import org.openjst.commons.database.model.QueryUpdate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class DatabaseUtils {

    private DatabaseUtils() {
    }

    public static long selectLong(final DatabaseObject db, final QuerySelect select) {
        final List<Object> parameters = new LinkedList<Object>();
        final Cursor cursor = db.rawQuery(select.toSql(parameters), convertParametersToStrings(parameters));
        cursor.moveToFirst();
        try {
            return cursor.getLong(0);
        } finally {
            cursor.close();
        }
    }

    @Nullable
    public static Long selectId(final DatabaseObject db, final QuerySelect select) {
        final List<Object> parameters = new LinkedList<Object>();
        final Cursor cursor = db.rawQuery(select.toSql(parameters), convertParametersToStrings(parameters));
        try {
            return cursor.moveToFirst() ? cursor.getLong(0) : null;
        } finally {
            cursor.close();
        }
    }

    public static void update(final DatabaseObject db, final QueryUpdate update) {
        final List<Object> parameters = new LinkedList<Object>();
        db.updateSQL(update.toSql(parameters), parameters.toArray());
    }

    public static void insert(final DatabaseObject db, final QueryInsert insert) {
        final List<Object> parameters = new LinkedList<Object>();
        db.updateSQL(insert.toSql(parameters), parameters.toArray());
    }

    public static long insertWithResult(final DatabaseObject db, final QueryInsert insert) {
        final ContentValues values = new ContentValues();
        for (final Map.Entry<String, Object> entry : insert.values().entrySet()) {
            final Object value = entry.getValue();
            if (value instanceof Byte) {
                values.put(entry.getKey(), (Byte) value);
            } else if (value instanceof Short) {
                values.put(entry.getKey(), (Short) value);
            } else if (value instanceof Integer) {
                values.put(entry.getKey(), (Integer) value);
            } else if (value instanceof Long) {
                values.put(entry.getKey(), (Long) value);
            } else if (value instanceof Float) {
                values.put(entry.getKey(), (Float) value);
            } else if (value instanceof Double) {
                values.put(entry.getKey(), (Double) value);
            } else if (value instanceof Boolean) {
                values.put(entry.getKey(), (Boolean) value);
            } else if (value instanceof byte[]) {
                values.put(entry.getKey(), (byte[]) value);
            } else if (value instanceof String) {
                values.put(entry.getKey(), (String) value);
            } else {
                values.put(entry.getKey(), String.valueOf(value));
            }
        }
        return db.insert(insert.getTable(), null, values);
    }

    private static String[] convertParametersToStrings(final List<Object> parameters) {
        final String[] arguments = new String[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            arguments[i] = String.valueOf(parameters.get(i));
        }
        return arguments;
    }

    public static Cursor select(final DatabaseObject db, final QuerySelect select) {
        final List<Object> parameters = new LinkedList<Object>();
        return db.rawQuery(select.toSql(parameters), convertParametersToStrings(parameters));
    }
}
