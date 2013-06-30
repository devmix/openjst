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

package org.openjst.client.android.commons.managers;

import android.app.Activity;
import android.app.Notification;

import java.io.Serializable;

/**
 * @author Sergey Grachev
 */
public interface NotificationsManager {

    String ACTIVITY_PARAMETER = "notification-parameter";

    void show(int id, Notification notification);

    void show(String tag, int id, Notification notification);

    void cancelAll();

    void cancel(String tag, int id);

    void cancel(int id);

    void newActivityInvoke(String tag, int id, String statusBarText, String text,
                           Class<? extends Activity> activityClass, Serializable parameter);
}
