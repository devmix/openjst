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

var org = {test: {modules: {
    Class: 'org.test.modules.Class',
    Class2: 'org.test.modules.Class2',
    module: {
        Class: 'org.test.modules.Class',
        Class2: 'org.test.modules.Class2'
    }
}}};

/*global YUI, OJST*/
YUI.add(org.test.modules.Class, function (Y) {
    "use strict";

    var Dummy = {
        fnDummy: function (arg1, arg2) {
            var a = 2 + 2;
            if (a == 5) {
                throw 'unexpected';
            }
            return a;
        }
    };

}, OJST.VERSION, {requires: [
    'yui-module-1', org.test.modules.Class2,
    'yui-module-2', org.test.modules.module.Class, 'yui-module,3'
]});