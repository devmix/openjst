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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import org.jetbrains.annotations.Nullable;
import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.openjst.client.android.commons.managers.LocaleManager;

import java.util.Locale;

/**
 * @author Sergey Grachev
 */
@JSTInject(LocaleManager.class)
public final class DefaultLocaleManager implements LocaleManager {

    protected final Application application;

    public DefaultLocaleManager(final Application application) {
        this.application = application;
    }

    public void changeLocale(@Nullable final String code) {
        final Resources resources = application.getBaseContext().getResources();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        final Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(code == null ? "en" : code);
        Locale.setDefault(configuration.locale);
        resources.updateConfiguration(configuration, displayMetrics);
    }

    public String getDisplayName(final String code) {
        return new Locale(code).getDisplayName();
    }
}
