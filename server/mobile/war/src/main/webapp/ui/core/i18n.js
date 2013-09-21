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

/*global OJST*/
/*jslint nomen:true, node:true, white:true, browser:true, plusplus:true*/
(function () {
    "use strict";

    var GROUP_WEB_UI_LABEL = 'web.ui.label',
        GROUP_WEB_UI_MSG = 'web.ui.msg',
        GROUP_WEB_UI_ENUM = 'web.ui.enum';

    /**
     * @class CoreI18n
     * @namespace OJST.core
     * @constructor
     * @param {Object} data
     */
    function CoreI18n(data) {
        this._data = data;
    }

    /**
     * @param {string} key
     * @param {string} subKey
     * @return {string}
     * @public
     */
    CoreI18n.prototype.localize = function (key, subKey) {
        return this._data[subKey ? key + '.' + subKey : key];
    };

    /**
     * @param {string} key
     * @return {string}
     * @public
     */
    CoreI18n.prototype.label = function (key) {
        return this._data[GROUP_WEB_UI_LABEL + '.' + key] || key;
    };

    /**
     * @param {string} key
     * @return {string}
     * @public
     */
    CoreI18n.prototype.setting = function (key) {
        return this._data[key] || key;
    };

    /**
     * @param {string} key
     * @return {string}
     * @public
     */
    CoreI18n.prototype.msg = function (key) {
        return this._data[GROUP_WEB_UI_MSG + '.' + key] || key;
    };

    /**
     * @param {string} className
     * @param {string} enumName
     * @return {string}
     * @public
     */
    CoreI18n.prototype.enumeration = function (className, enumName) {
        return this._data[GROUP_WEB_UI_ENUM + '.' + className + '.' + enumName] || enumName;
    };

    OJST.core.CoreI18n = CoreI18n;

}());