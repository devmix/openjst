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
YUI.add(OJST.ns.api.Base, function (Y) {
    "use strict";

    var txStates = {},
        baseUri = 'ui-api/';

    /**
     * @class Base
     * @namespace OJST.ui.api
     * @static
     */
    OJST.ui.api.Base = {
        /**
         * @param {string} uri
         * @param {object} [parameters]
         * @param {function(response: string, isSuccess: boolean, errorData: string)} [completeCallback]
         * @param {object} [scope]
         * @static
         * @public
         */
        post: function (uri, parameters, completeCallback, scope) {
            Y.io(baseUri + uri, {
                method: 'POST',
                data: parameters,
                on: {
                    end: function (txID, response) {
                        try {
                            if (completeCallback) {
                                var state = txStates[txID] || {},
                                    result = OJST.ui.api.Base._parseJson(state.response) || {},
                                    success = !!state.success && result.status === 'OK';
                                completeCallback.call(scope || this, result.value, success, result.errorData);
                            }
                        } finally {
                            delete txStates[txID];
                        }
                    },
                    success: function (txID, response) {
                        txStates[txID] = {success: true, response: response.responseText};
                    },
                    failure: function (txID, response) {
                        txStates[txID] = {success: false, response: response.responseText};
                    }
                }
            });
        },
        /**
         * @param {string} json
         * @returns {(*|string)}
         * @private
         * @static
         */
        _parseJson: function (json) {
            try {
                return Y.JSON.parse(json);
            } catch (e) {
                return json;
            }
        }
    };

}, OJST.VERSION, {requires: [
    'io-base', 'json-parse'
]});