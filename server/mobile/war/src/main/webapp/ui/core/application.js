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
     * @class CoreApplication
     * @namespace OJST.core
     * @constructor
     */
    function CoreApplication() {
        this._yuiApp = undefined;
    }

    /**
     * @param {OJST.ui.apps.Abstract} yuiApp
     */
    CoreApplication.prototype.assignYUIApplication = function (yuiApp) {
        this._yuiApp = yuiApp;
    };

    /**
     * @param {string} route
     */
    CoreApplication.prototype.saveRoute = function (route) {
        if (this._yuiApp) {
            this._yuiApp.save(route);
        }
    };

    OJST.core.CoreApplication = CoreApplication;

}());