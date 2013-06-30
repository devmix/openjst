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

/**
 * @author Sergey Grachev
 */
public final class Constants {

    private Constants() {
    }

    public static final class Notifications {

        public static final String TAG_APPLICATION = "application";
        public static final String TAG_UPDATE = "update";
        public static final int ID_APPLICATION = 1;

        private Notifications() {
        }
    }

    public static final class Settings {

        public static final String LOGIN_REMEMBER = "settings_login_remember";
        public static final String LOGIN_CLIENT_ACCOUNT = "settings_server_account";
        public static final String LOGIN_CLIENT_ID = "settings_login_client_id";
        public static final String LOGIN_CLIENT_SECRET_KEY = "settings_login_client_secret_key";
        public static final String SERVER_HOST = "settings_server_host";
        public static final String SERVER_PORT = "settings_server_port";
        public static final String LOCALE_CODE = "settings_locale_code";
        public static final String UPDATE_CHECK = "settings_update_check";

        private Settings() {
        }
    }

    public static final class RPC {

        public static final String OBJECT_MOBILE = "mobile";
        public static final String MOBILE_CHECK_UPDATE = "checkUpdate";

        private RPC() {
        }
    }
}
