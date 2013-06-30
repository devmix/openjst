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

package org.openjst.client.android;

import android.app.Application;
import android.content.Intent;
import org.openjst.client.android.commons.ApplicationContext;
import org.openjst.client.android.commons.inject.GenericInjector;
import org.openjst.client.android.commons.inject.annotations.JSTApplicationContext;
import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.openjst.client.android.commons.managers.LocaleManager;
import org.openjst.client.android.commons.managers.SettingsManager;
import org.openjst.client.android.commons.managers.impl.*;
import org.openjst.client.android.dao.impl.SQLiteArchiveDAO;
import org.openjst.client.android.dao.impl.SQLiteSessionDAO;
import org.openjst.client.android.managers.impl.DefaultRPCManager;
import org.openjst.client.android.service.ServerConnectionService;

/**
 * @author Sergey Grachev
 */
@JSTApplicationContext(
        injector = GenericInjector.class,
        managers = {
                // managers
                DefaultSettingsManager.class,
                DefaultLocaleManager.class,
                DefaultNotificationsManager.class,
                DefaultApplicationManager.class,
                DefaultRPCManager.class,
                DefaultSessionManager.class,
                // database
                SQLiteArchiveDAO.class,
                SQLiteSessionDAO.class
        }
)
public final class OpenJSTApplication extends Application {

    @JSTInject
    private LocaleManager localeManager;

    @JSTInject
    private SettingsManager settingsManager;

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContext.init(this);

        localeManager.changeLocale(settingsManager.getString(Constants.Settings.LOCALE_CODE));

        startService(new Intent(this, ServerConnectionService.class));
    }
}
