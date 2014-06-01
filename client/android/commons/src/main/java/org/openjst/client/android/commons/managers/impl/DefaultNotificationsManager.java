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

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import org.openjst.client.android.commons.R;
import org.openjst.client.android.commons.inject.annotations.Inject;
import org.openjst.client.android.commons.inject.annotations.Singleton;
import org.openjst.client.android.commons.inject.annotations.android.ASystemService;
import org.openjst.client.android.commons.managers.ApplicationManager;
import org.openjst.client.android.commons.managers.NotificationsManager;
import org.openjst.client.android.commons.utils.NotificationBuilder;

import java.io.Serializable;

import static org.openjst.client.android.commons.GlobalContext.context;

/**
 * @author Sergey Grachev
 */
@Singleton
public class DefaultNotificationsManager implements NotificationsManager {

    @ASystemService(Context.NOTIFICATION_SERVICE)
    private NotificationManager notificationManager;

    @Inject
    private ApplicationManager applicationManager;

    public void show(final int id, final Notification notification) {
        notificationManager.notify(id, notification);
    }

    public void show(final String tag, final int id, final Notification notification) {
        notificationManager.notify(tag, id, notification);
    }

    public void cancelAll() {

    }

    public void cancel(final String tag, final int id) {
        notificationManager.cancel(tag, id);
    }

    public void cancel(final int id) {
        notificationManager.cancel(id);
    }

    public void newActivityInvoke(final String tag, final int id, final String statusBarText, final String text,
                                  final Class<? extends Activity> activityClass, final Serializable parameter) {

        final Context ctx = context();

        final PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0,
                new Intent(ctx, activityClass).putExtra(ACTIVITY_PARAMETER, parameter), 0);

        notificationManager.notify(tag, id, new NotificationBuilder(ctx).icon(R.drawable.ic_notification_app)
                .title(applicationManager.getName()).timestamp(System.currentTimeMillis())
                .statusBarText(statusBarText).text(text)
                .viewIntent(pendingIntent).build());
    }
}
