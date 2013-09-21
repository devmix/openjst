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
        public static final String FIND_ACCOUNT_BY_API_KEY = PREFIX + "findAccountByApiKey";
        public static final String GET_LIST_OF = PREFIX + "getListOf";
        public static final String GET_COUNT_OF = PREFIX + "getCountOf";
        public static final String SET_ONLINE_STATUS = PREFIX + "setOnlineStatus";
        public static final String SET_OFFLINE_STATUS = PREFIX + "setOfflineStatus";
        public static final String GET_ONLINE_COUNT_OF = PREFIX + "getOnlineCountOf";
        public static final String GET_ONLINE_LIST_OF = PREFIX + "getOnlineListOf";
        public static final String GET_COUNT_OF_CLIENTS = PREFIX + "getCountOfClients";
        public static final String GET_COUNT_OF_USERS = PREFIX + "getCountOfUsers";

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
        public static final String GET_CLIENT_ID_OF_ACCOUNT_BY_AUTH_ID = PREFIX + "getClientIdOfAccountByAuthId";
        public static final String FIND_CACHED_SECRET_KEY_OF = PREFIX + "findCachedSecretKeyOf";
        public static final String FIND_BY_ACCOUNT_AND_CLIENT_NAME = PREFIX + "findByAccountAndClientId";
        public static final String SET_OFFLINE_STATUS_FOR_ALL = PREFIX + "setOfflineStatusForAll";
        public static final String SET_ONLINE_STATUS = PREFIX + "setOnlineStatus";
        public static final String SET_OFFLINE_STATUS = PREFIX + "setOfflineStatus";
        public static final String GET_ONLINE_LIST_OF = PREFIX + "getOnlineListOf";
        public static final String GET_ONLINE_COUNT_OF = PREFIX + "getOnlineCountOf";
        public static final String GET_ALL_CLIENTS_AS_ACTORS = PREFIX + "getAllClientsAsActors";

        private Client() {
        }
    }
    //</editor-fold>
    //<editor-fold desc="RPCMessage">

    public static final class RPCMessage {
        public static final String PREFIX = "RPCMessage.";
        public static final String GET_NOT_DELIVERED_TO_CLIENTS = PREFIX + "getNotDeliveredToClients";
        public static final String GET_NOT_DELIVERED_TO_SERVERS = PREFIX + "getNotDeliveredToServers";
        public static final String GET_COUNT_FROM_CLIENTS = PREFIX + "getCountFromClients";
        public static final String GET_COUNT_DELIVERED_TO_SERVER = PREFIX + "getCountDeliveredToServer";
        public static final String GET_COUNT_FROM_SERVER = PREFIX + "getCountFromServer";
        public static final String GET_COUNT_DELIVERED_TO_CLIENTS = PREFIX + "getCountDeliveredToClients";
        public static final String DELIVERY_SUCCESS = PREFIX + "deliverySuccess";
        public static final String DELIVERY_FAIL = PREFIX + "deliveryFail";
        public static final String FILTER_CLIENTS_WITH_NOT_DELIVERED_MESSAGES = PREFIX + "filterClientsWithNotDeliveredMessages";
        public static final String FILTER_SERVERS_WITH_NOT_DELIVERED_MESSAGES = PREFIX + "filterServersWithNotDeliveredMessages";

        private RPCMessage() {
        }
    }
    //</editor-fold>
    //<editor-fold desc="Update">

    public static final class Update {
        public static final String PREFIX = "Update.";
        public static final String FIND_BY_ID = PREFIX + "findById";
        public static final String GET_LIST_OF = PREFIX + "getListOf";
        public static final String GET_COUNT_OF = PREFIX + "getCountOf";
        public static final String GET_UPDATE_TO_SENT = PREFIX + "getUpdateToSent";

        private Update() {
        }
    }
    //</editor-fold>
}
