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
YUI.add(OJST.ns.models.AccountSummary, function (Y) {
    "use strict";

    /**
     * @class AccountSummary
     * @namespace OJST.ui.models
     * @constructor
     * @extends OJST.ui.models.Base
     */
    OJST.ui.models.AccountSummary = Y.Base.create('modelsAccountSummary', OJST.ui.models.Base, [], {
        url: 'ui-api/accounts/{id}/summary'
    }, {
        ATTRS: {
            id: {validator: Y.Lang.isNumber},

            authId: {validator: Y.Lang.isString},
            apiKey: {validator: Y.Lang.isString},
            name: {validator: Y.Lang.isString},
            system: {validator: Y.Lang.isBoolean},

            usersCount: {validator: Y.Lang.isNumber},
            clientsCount: {validator: Y.Lang.isNumber},

            online: {validator: Y.Lang.isBoolean},
            lastProtocolType: {validator: Y.Lang.isString},
            lastOnlineTime: {validator: Y.Lang.isDate, type: 'date'},
            lastRemoteHost: {validator: Y.Lang.isString},

            messagesToClient: {validator: Y.Lang.isNumber},
            messagesToServer: {validator: Y.Lang.isNumber},
            messagesFromClient: {validator: Y.Lang.isNumber},
            messagesFromServer: {validator: Y.Lang.isNumber}
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.models.Base
]});