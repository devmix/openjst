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

    /**
     * @class CoreEnums
     * @namespace OJST.core
     * @constructor
     * @param {Object} data
     */
    function CoreEnums(data) {
        this._data = data;
    }

    /**
     * @return {{string: string[]}}
     * @public
     */
    CoreEnums.prototype.all = function () {
        return this._data;
    };

    /**
     * @param {string} name
     * @return {string[]}
     * @public
     */
    CoreEnums.prototype.get = function (name) {
        return this._data[name] || [];
    };

    /**
     * @param {string} className
     * @param {{enumName:boolean}} [filter]
     * @return {string[][]}
     * @public
     */
    CoreEnums.prototype.asFieldData = function (className, filter) {
        var data = this._data[className], result = [], i, length, enumName;
        if (data && data.length > 0) {
            for (i = 0, length = data.length; i < length; i++) {
                enumName = data[i];
                if (!filter || filter[enumName] !== false) {
                    result.push([enumName, OJST.i18n.enumeration(className, enumName)]);
                }
            }
        }
        return result;
    };

    OJST.core.CoreEnums = CoreEnums;

}());