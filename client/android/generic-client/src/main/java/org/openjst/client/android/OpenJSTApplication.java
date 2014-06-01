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
import org.openjst.client.android.commons.GlobalContext;
import org.openjst.client.android.commons.inject.DefaultApplicationInjector;
import org.openjst.client.android.commons.inject.annotations.Inject;
import org.openjst.client.android.commons.inject.annotations.android.ApplicationConfig;
import org.openjst.client.android.commons.managers.LocaleManager;
import org.openjst.client.android.commons.managers.SettingsManager;
import org.openjst.client.android.commons.managers.impl.*;
import org.openjst.client.android.dao.impl.SQLiteLogsDAO;
import org.openjst.client.android.dao.impl.SQLitePacketsDAO;
import org.openjst.client.android.dao.impl.SQLiteTrafficDAO;
import org.openjst.client.android.dao.impl.SQLiteVersionDAO;
import org.openjst.client.android.db.impl.SQLiteClientDB;
import org.openjst.client.android.db.impl.SQLiteLogsDB;
import org.openjst.client.android.managers.impl.DefaultRPCManager;
import org.openjst.client.android.service.ServerConnectionService;

/**
 * @author Sergey Grachev
 */
@ApplicationConfig(
        implementations = {
                // managers
                DefaultSettingsManager.class,
                DefaultLocaleManager.class,
                DefaultNotificationsManager.class,
                DefaultApplicationManager.class,
                DefaultRPCManager.class,
                DefaultSessionManager.class,

                // database
                SQLiteLogsDB.class,
                SQLiteClientDB.class,

                // DAO
                SQLiteLogsDAO.class,
                SQLiteTrafficDAO.class,
                SQLitePacketsDAO.class,
                SQLiteVersionDAO.class
        }
)
public final class OpenJSTApplication extends Application {

    @Inject
    private LocaleManager localeManager;

    @Inject
    private SettingsManager settingsManager;
    
    private DefaultApplicationInjector injector;
    
    @Override
    public void onCreate() {
        super.onCreate();

        GlobalContext.registerApplication(this);

        injector = new DefaultApplicationInjector(this);
        injector.apply(this);
        injector.finish();

        localeManager.changeLocale(settingsManager.getString(Constants.Settings.LOCALE_CODE));

        startService(new Intent(this, ServerConnectionService.class));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        GlobalContext.unregisterApplication();
    }
}
