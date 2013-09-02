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

/*global Y, YUI, OJST, $*/
/*jslint nomen:true, node:true, white:true, browser:true, plusplus:true*/
YUI.add(OJST.ns.widgets.form.FormFields, function (Y) {
    "use strict";

    /**
     * @class FormFields
     * @namespace OJST.ui.widgets.form
     * @constructor
     * @extends OJST.ui.widgets.AbstractWidget
     */
    OJST.ui.widgets.form.FormFields = Y.Base.create('widgets-form-fields', OJST.ui.widgets.AbstractWidget, [Y.WidgetParent, Y.WidgetChild], {

        BOUNDING_TEMPLATE: '<div class="form-horizontal"></div>',
        CONTENT_TEMPLATE: '<div><span class="title"><span class="content"></span><hr></span></div> ',

        /** @override */
        initializer: function () {
            /**
             * @type {Y.Node}
             * @private
             */
            this._titleNode = undefined;
        },

        /** @override */
        renderUI: function () {
            var bbx = this.get(OJST.STATIC.BBX), cbx = this.get(OJST.STATIC.CBX);
            this._titleNode = cbx.one('.title');
            this._onChangeTitle();
        },

        /** @override */
        bindUI: function () {
            this.subscribe(this.after('titleChange', this._onChangeTitle));
        },

        /** @override */
        syncSize: function () {
            OJST.ui.widgets.form.FormFields.superclass.syncSize.apply(this, arguments);
            this.each(function (item, index) {
                if (item.syncSize) {
                    item.syncSize();
                }
            });
        },

        /** @override */
        disable: function () {
            this.each(function (child) {
                if (child.disable) {
                    child.disable();
                }
            });
        },

        /** @override */
        enable: function () {
            this.each(function (child) {
                if (child.enable) {
                    child.enable();
                }
            });
        },

        _onChangeTitle: function () {
            var title = this.get('title');
            if (title) {
                this._titleNode.setStyle('display', 'block');
                this._titleNode.one('.content').set('text', title);
            } else {
                this._titleNode.setStyle('display', 'none');
            }
        }

    }, {
        ATTRS: {
            name: {
                writeOnce: 'initOnly',
                validator: Y.Lang.isString
            },
            title: {
            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.widgets.AbstractWidget,
    'widget-child',
    'widget-parent'
]});