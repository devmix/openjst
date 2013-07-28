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
     * @class CorePreferences
     * @namespace OJST.core
     * @constructor
     */
    function CorePreferences(preferences) {
        this._preferences = preferences || {ui: {scripts: {}}};
    }

    /**
     * @return {boolean}
     */
    CorePreferences.prototype.useDebugScripts = function () {
        return !!this._preferences.groups.ui.groups.scripts.properties.debug;
    };
    /**
     * @return {boolean}
     */
    CorePreferences.prototype.useComboServicesForScripts = function () {
        return !!this._preferences.groups.ui.groups.scripts.properties.combine;
    };

    OJST.core.CorePreferences = CorePreferences;

}());