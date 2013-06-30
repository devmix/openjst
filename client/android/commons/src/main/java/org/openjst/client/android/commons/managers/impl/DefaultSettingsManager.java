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

package org.openjst.client.android.commons.managers.impl;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.jetbrains.annotations.Nullable;
import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.openjst.client.android.commons.managers.SettingsManager;

/**
 * @author Sergey Grachev
 */
@JSTInject(SettingsManager.class)
public class DefaultSettingsManager implements SettingsManager {

    protected final SharedPreferences sharedPreferences;

    public DefaultSettingsManager(final Application application) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Nullable
    public String getString(final String key, final String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    @Nullable
    public String getString(final String key) {
        return getString(key, null);
    }

    public void setString(final String key, final String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public boolean getBoolean(final String key, final boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public boolean getBoolean(final String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public int getInteger(final String key) {
        final Object value = sharedPreferences.getString(key, null);
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        return sharedPreferences.getInt(key, 0);
    }

    public int getInteger(final String key, final int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public SharedPreferences.Editor edit() {
        return sharedPreferences.edit();
    }
}
