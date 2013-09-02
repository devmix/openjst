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

/*global YUI, Y, OJST*/
/*jslint nomen:true, node:true, white:true, browser:true, plusplus:true*/
(function () {
    "use strict";

    /**
     * @class Lang
     * @namespace OJST.core
     * @static
     */
    OJST.core.Lang = {
        /**
         * @param {object} impl
         * @public
         * @static
         */
        createClass: function (impl) {
            var field, clazz = function () {
                if (impl.initializer) {
                    impl.initializer();
                }
            };

            for (field in impl) {
                if (impl.hasOwnProperty(field)) {
                    clazz.prototype[field] = impl[field];
                }
            }

            return clazz;
        }
    };

}());