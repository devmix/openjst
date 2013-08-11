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

/*global YUI, OJST*/
/*jslint nomen:true, node:true, white:true, browser:true, plusplus:true*/
YUI.add(OJST.ns.api.Security, function (Y) {
    "use strict";

    var API = {
        GENERATE_ACCOUNT_API_KEY: 'accounts/generateAccountAPIKey'
    };

    /**
     * @typedef {function(response: string, isSuccess: boolean)} RequestCallback
     */

    /**
     * @class Security
     * @namespace OJST.ui.api
     * @static
     */
    OJST.ui.api.Security = {
        /**
         * @param {(number|string)} accountId
         * @param {RequestCallback} [completeCallback]
         * @param {object} [scope]
         * @static
         * @public
         */
        generateAccountAPIKey: function (accountId, completeCallback, scope) {
            OJST.ui.api.Base.post(API.GENERATE_ACCOUNT_API_KEY, {
                accountId: isNaN(parseInt(accountId, 10)) ? -1 : accountId
            }, completeCallback, scope);
        }
    };

}, OJST.VERSION, {requires: [
    OJST.ns.api.Base
]});