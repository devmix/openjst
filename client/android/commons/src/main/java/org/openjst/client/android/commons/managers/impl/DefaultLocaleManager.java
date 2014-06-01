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

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import org.openjst.client.android.commons.inject.annotations.Singleton;
import org.openjst.client.android.commons.managers.LocaleManager;

import javax.annotation.Nullable;
import java.util.Locale;

import static org.openjst.client.android.commons.GlobalContext.context;

/**
 * @author Sergey Grachev
 */
@Singleton
public class DefaultLocaleManager implements LocaleManager {

    public void changeLocale(@Nullable final String code) {
        final Resources resources = context().getResources();
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
