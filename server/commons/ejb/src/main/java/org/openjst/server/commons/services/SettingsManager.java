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

import org.openjst.server.commons.settings.Setting;

import java.util.Date;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public interface SettingsManager {

    Object get(Setting setting);

    String getString(Setting setting);

    int getInteger(Setting setting);

    long getLong(Setting setting);

    float getFloat(Setting setting);

    double getDouble(Setting setting);

    boolean getBoolean(Setting setting);

    Date getDate(Setting setting);

    Map<Setting, Object> getList(Setting... settings);

    void set(Setting setting, Object value);

    void set(Map<Setting, Object> values);

    void setString(Setting setting, String value);

    void setInteger(Setting setting, int value);

    void setLong(Setting setting, long value);

    void setFloat(Setting setting, float value);

    void setDouble(Setting setting, double value);

    void setBoolean(Setting setting, boolean value);

    void setDate(Setting setting, Date value);

    Object convertSetting(Setting setting, String value);
}
