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

/*global Y, YUI, OJST, $, WidgetStdMod*/
/*jslint nomen:true, node:true, white:true, browser:true, plusplus:true*/
YUI.add(OJST.modules.widgets.LoadingPanel, function (Y) {
    "use strict";

    /**
     * @class LoadingPanel
     * @namespace OJST.ui.widgets
     * @constructor
     * @extends Y.Panel
     */
    OJST.ui.widgets.LoadingPanel = Y.Base.create('widgetsLoadingPanel', Y.Panel, [], {

        initializer: function (cfg) {
            this.set('bodyContent', '<div class="yui3-loading-message"><div class="yui3-loading-icon"></div>' + cfg.message + '</div>');
            this.set('zIndex', 1);
            this.set('centered', true);
            this.set('focused', false);
            this.set('buttons', []);
        },

        _messageSetter: function (message) {
            this.setStdModContent(WidgetStdMod.BODY, message);
            return message;
        }

    }, {
        ATTRS: {
            message: {
                validator: Y.Lang.isString,
                setter: '_messageSetter'
            }
        }
    });

}, OJST.VERSION, {
    requires: [
        'panel'
    ]});