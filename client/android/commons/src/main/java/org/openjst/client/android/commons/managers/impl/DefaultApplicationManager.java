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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import org.jetbrains.annotations.Nullable;
import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.openjst.client.android.commons.managers.ApplicationManager;
import org.openjst.commons.dto.ApplicationVersion;

/**
 * @author Sergey Grachev
 */
@JSTInject(ApplicationManager.class)
public class DefaultApplicationManager implements ApplicationManager {

    private static final ApplicationVersion UNKNOWN_VERSION = new ApplicationVersion(0, 0, 0, 0, null);

    protected final Application application;
    protected final PackageManager packageManager;

    public DefaultApplicationManager(final Application application) {
        this.application = application;
        this.packageManager = application.getPackageManager();
    }

    public ApplicationVersion getVersion() {
        final PackageInfo packageInfo;
        try {
            packageInfo = application.getPackageManager().getPackageInfo(application.getPackageName(), 0);
            return ApplicationVersion.parse(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return UNKNOWN_VERSION;
    }

    @Nullable
    public String getName() {
        try {
            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(application.getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (final PackageManager.NameNotFoundException ignore) {
        }
        return null;
    }
}
