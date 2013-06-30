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

(function (scope) {
    "use strict";

    if (scope._PROJECT_MANIFEST_OBJECT_ === undefined) {
        scope._PROJECT_MANIFEST_OBJECT_ = {};
    }

    var o = scope._PROJECT_MANIFEST_OBJECT_, x;

    //noinspection JSUnresolvedVariable,JSLint
    x = _PROJECT_MANIFEST_META_;
    o.NAMESPACE = x.NAMESPACE;
    o.VERSION = x.VERSION;
    o.PROJECT_NAME = x.PROJECT_NAME;
    o.ui = x.ui;
    o.modules = x.modules;

    //noinspection JSUnresolvedVariable,JSLint
    o.yui = _PROJECT_MANIFEST_YUI_;
}(window));