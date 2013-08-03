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

/**
 * @author Sergey Grachev
 */

/*global Y, YUI, OJST, $*/
/*jslint nomen:true, node:true, white:true, browser:true, plusplus:true*/
YUI.add(OJST.ns.models.connection.Account, function (Y) {
    "use strict";

    /**
     * @class Account
     * @namespace OJST.ui.models.connection
     * @constructor
     * @extends OJST.ui.models.Base
     */
    OJST.ui.models.connection.Account = Y.Base.create('modelsConnectionAccount', OJST.ui.models.Base, [], {
        root: 'ui-api/dashboard/connections/accounts'
    }, {
        ATTRS: {
            id: {validator: Y.Lang.isNumber},
            authId: {validator: Y.Lang.isString},
            lastOnlineTime: {validator: Y.Lang.isDate, type: 'date'},
            lastProtocolType: {validator: Y.Lang.isString},
            lastRemoteHost: {validator: Y.Lang.isString}
        }
    });

    /**
     * @class AccountList
     * @namespace OJST.ui.models.connection
     * @constructor
     * @extends OJST.ui.models.BaseList
     */
    OJST.ui.models.connection.AccountList = Y.Base.create('modelsConnectionAccountList', OJST.ui.models.BaseList, [], {
        model: OJST.ui.models.connection.Account
    });

}, OJST.VERSION, {requires: [
    OJST.ns.models.Base
]});