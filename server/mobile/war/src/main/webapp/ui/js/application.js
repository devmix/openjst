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

// DEFINE GLOBAL DATA

(function () {
    "use strict";

    // define packages

    if (!OJST.app) {
        OJST.app = {
            locale: undefined,
            session: {
                user: {
                    name: undefined,
                    role: undefined
                }
            }
        };
    }

    OJST.core = {

    };

    OJST.singleton = {
        app: undefined
    };

    OJST.STATIC = {
        /**
         * @return {undefined}
         */
        EMPTY_FN: function () {
            return undefined;
        },
        /**
         * @type {string}
         */
        BBX: 'boundingBox',
        /**
         * @type {string}
         */
        CBX: 'contentBox',
        /**
         * @type {string}
         */
        CONTAINER: 'container'
    };

}());

// YUI CONFIG

(function () {
    "use strict";

    OJST.yui.config.maxURLLength = 0xFFFF;
    OJST.yui.config.groups.openjst.maxURLLength = 0xFFFF;
    OJST.yui.config.skin = 'sam';

}());

// I18N

(function () {
    "use strict";

    var GROUP_WEB_UI_LABEL = 'web.ui.label',
        GROUP_WEB_UI_MSG = 'web.ui.msg';

    /**
     * @class LanguageResourcesBundle
     * @namespace OJST.core
     * @constructor
     * @param {Object} languageData
     */
    function LanguageResourcesBundle(languageData) {
        this.data = languageData;
    }

    /**
     * @param {string} key
     * @param {string} subKey
     * @return {string}
     * @public
     */
    LanguageResourcesBundle.localize = function (key, subKey) {
        return this.data[subKey ? key + '.' + subKey : key];
    };

    /**
     * @param {string} key
     * @return {string}
     * @public
     */
    LanguageResourcesBundle.prototype.label = function (key) {
        return this.data[GROUP_WEB_UI_LABEL + '.' + key];
    };

    /**
     * @param {string} key
     * @return {string}
     * @public
     */
    LanguageResourcesBundle.prototype.msg = function (key) {
        return this.data[GROUP_WEB_UI_MSG + '.' + key];
    };

    OJST.core.LanguageResourcesBundle = LanguageResourcesBundle;

    /**
     * @type {OJST.core.LanguageResourcesBundle}
     */
    OJST.i18n = new LanguageResourcesBundle(OJST.app.i18n);

}());