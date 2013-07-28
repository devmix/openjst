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

/*global OJST, Y*/
/*jslint nomen:true, node:true, white:true, browser:true, plusplus:true*/
(function () {
    "use strict";

    /**
     * @class CoreAccount
     * @namespace OJST.core
     * @constructor
     * @param {Object} data
     */
    function CoreAccount(data) {
        this._data = data || {
            name: OJST.i18n.label('guest'),
            authId: 'guest',
            readOnly: true
        };
    }

    /**
     * @return {string}
     */
    CoreAccount.prototype.getAuthId = function () {
        return this._data.authId;
    };
    /**
     * @return {string}
     */
    CoreAccount.prototype.getName = function () {
        return this._data.name;
    };
    /**
     * @return {boolean}
     */
    CoreAccount.prototype.isReadOnly = function () {
        return this._data.readOnly;
    };

    /**
     * @class CoreUser
     * @namespace OJST.core
     * @constructor
     * @param {Object} data
     */
    function CoreUser(data) {
        this._data = data || {
            name: OJST.i18n.label('guest'),
            authId: 'guest',
            role: 'guest',
            language: 'EN',
            readOnly: true
        };
    }

    /**
     * @return {string}
     */
    CoreUser.prototype.getAuthId = function () {
        return this._data.authId;
    };
    /**
     * @return {string}
     */
    CoreUser.prototype.getName = function () {
        return this._data.name;
    };
    /**
     * @return {boolean}
     */
    CoreUser.prototype.isReadOnly = function () {
        return this._data.readOnly;
    };
    /**
     * @return {string}
     */
    CoreUser.prototype.getRole = function () {
        return this._data.role;
    };
    /**
     * @return {string}
     */
    CoreUser.prototype.getLanguage = function () {
        return this._data.language;
    };

    /**
     * @class Session
     * @namespace OJST.core
     * @constructor
     * @param {Object} user
     */
    function Session(user) {
        this._account = new CoreAccount(user ? user.account : undefined);
        this._user = new CoreUser(user);
    }

    /**
     * @return {CoreUser}
     */
    Session.prototype.getUser = function () {
        return this._user;
    };
    /**
     * @return {CoreAccount}
     */
    Session.prototype.getAccount = function () {
        return this._account;
    };

    OJST.core.CoreAccount = CoreAccount;
    OJST.core.CoreUser = CoreUser;
    OJST.core.Session = Session;

}());