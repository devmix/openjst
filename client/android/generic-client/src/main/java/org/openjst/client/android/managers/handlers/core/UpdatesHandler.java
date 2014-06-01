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

package org.openjst.client.android.managers.handlers.core;

import android.widget.Toast;
import org.openjst.client.android.Constants;
import org.openjst.client.android.R;
import org.openjst.client.android.activity.ApplicationUpdateActivity;
import org.openjst.client.android.commons.GlobalContext;
import org.openjst.client.android.commons.inject.annotations.Inject;
import org.openjst.client.android.commons.inject.annotations.Singleton;
import org.openjst.client.android.commons.managers.NotificationsManager;
import org.openjst.client.android.dao.VersionDAO;
import org.openjst.commons.dto.ApplicationVersion;

import static org.openjst.client.android.commons.GlobalContext.context;

/**
 * @author Sergey Grachev
 */
@Singleton
public final class UpdatesHandler {

    @Inject
    private VersionDAO dao;

    @Inject
    private NotificationsManager notifications;

    public void add(final String os, final ApplicationVersion version) {
        if ("ANDROID".equals(os)) {
            dao.add(version);
            notify(version);
        }
    }

    public void update(final String os, final ApplicationVersion version) {
        if ("ANDROID".equals(os)) {
            dao.update(version);
            notify(version);
        }
    }

    public void remove(final String os, final ApplicationVersion version) {
        if ("ANDROID".equals(os)) {
            dao.remove(version);
            GlobalContext.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context(), "remove " + version, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void notify(final ApplicationVersion version) {
        notifications.newActivityInvoke(Constants.Notifications.TAG_UPDATE, Constants.Notifications.ID_APPLICATION,
                String.format(context().getString(R.string.notification_new_version), version),
                String.format(context().getString(R.string.notification_click_to_start_upgrade_to_new_version), version),
                ApplicationUpdateActivity.class, version);
    }
}
