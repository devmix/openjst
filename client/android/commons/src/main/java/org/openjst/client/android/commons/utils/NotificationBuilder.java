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

package org.openjst.client.android.commons.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;

/**
 * @author Sergey Grachev
 */
public final class NotificationBuilder {

    private final Context context;
    private String statusBarText = null;
    private String title = null;
    private String text = null;
    private long timestamp = 0;
    private PendingIntent viewIntent = null;
    private int icon = 0;

    public NotificationBuilder(final Context context) {
        this.context = context;
    }

    public NotificationBuilder icon(final int icon) {
        this.icon = icon;
        return this;
    }

    public NotificationBuilder statusBarText(final String statusBarText) {
        this.statusBarText = statusBarText;
        return this;
    }

    public NotificationBuilder title(final String title) {
        this.title = title;
        return this;
    }

    public NotificationBuilder text(final String text) {
        this.text = text;
        return this;
    }

    public NotificationBuilder timestamp(final long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public NotificationBuilder viewIntent(final PendingIntent viewIntent) {
        this.viewIntent = viewIntent;
        return this;
    }

    public Notification build() {
        final Notification notification = new Notification();
        if (icon != 0) {
            notification.icon = icon;
        }
        if (statusBarText != null) {
            notification.tickerText = statusBarText;
        }
        if (timestamp != 0) {
            notification.when = timestamp;
        }
        if (viewIntent != null) {
            notification.contentIntent = viewIntent;
        }

        // TODO custom content view
        notification.setLatestEventInfo(context, title, text, viewIntent);

        return notification;
    }
}
