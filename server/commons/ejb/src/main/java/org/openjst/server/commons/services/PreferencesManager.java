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

package org.openjst.server.commons.services;

import org.openjst.server.commons.preferences.ServerPreference;

import java.util.Date;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public interface PreferencesManager {

    String getString(ServerPreference property);

    int getInteger(ServerPreference property);

    long getLong(ServerPreference property);

    float getFloat(ServerPreference property);

    double getDouble(ServerPreference property);

    boolean getBoolean(ServerPreference property);

    Date getDate(ServerPreference property);

    Map<ServerPreference, Object> getList(ServerPreference... properties);

    void setString(ServerPreference property, String value);

    void setInteger(ServerPreference property, int value);

    void setLong(ServerPreference property, long value);

    void setFloat(ServerPreference property, float value);

    void setDouble(ServerPreference property, double value);

    void setBoolean(ServerPreference property, boolean value);

    void setDate(ServerPreference property, Date value);

    Object get(ServerPreference property);
}
