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

package org.openjst.server.mobile.model;

/**
 * @author Sergey Grachev
 */
public final class Queries {

    private Queries() {
    }

    //<editor-fold desc="Account">

    public static final class Account {
        public static final String PREFIX = "Account.";
        public static final String FIND_BY_AUTH_ID = PREFIX + "findByAuthId";
        public static final String FIND_BY_ID = PREFIX + "find";
        public static final String FIND_SYSTEM = PREFIX + "findSystem";
        public static final String FIND_ACCOUNT_ID_BY_API_KEY = PREFIX + "findAccountIdByApiKey";
        public static final String GET_LIST_OF = PREFIX + "getListOf";
        public static final String GET_COUNT_OF = PREFIX + "getCountOf";

        private Account() {
        }
    }

    //</editor-fold>
    //<editor-fold desc="User">

    public static final class User {
        public static final String PREFIX = "User.";
        public static final String FIND_BY_ACCOUNT_AND_NAME = PREFIX + "findByAccountAndName";
        public static final String FIND_BY_AUTH_ID = PREFIX + "findByAuthId";
        public static final String FIND_BY_ID = PREFIX + "findById";
        public static final String FIND_SYSTEM = PREFIX + "findSystem";
        public static final String FIND_SECRET_KEY_OF = PREFIX + "findSecretKeyOf";
        public static final String FIND_USER_ROLE = PREFIX + "findUserRole";
        public static final String GET_LIST_OF = PREFIX + "getListOf";
        public static final String GET_COUNT_OF = PREFIX + "getCountOf";

        private User() {
        }
    }

    //</editor-fold>
    //<editor-fold desc="Client">

    public static final class Client {
        public static final String PREFIX = "Client.";
        public static final String FIND_CACHED_SECRET_KEY_OF = PREFIX + "findCachedSecretKeyOf";

        private Client() {
        }
    }
    //</editor-fold>
}
