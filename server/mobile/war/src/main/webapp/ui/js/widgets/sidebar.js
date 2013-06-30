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
YUI.add(OJST.modules.widgets.Sidebar, function (Y) {
    "use strict";

    var ALIGN = {
            LEFT: 'left', RIGHT: 'right'
        },
        CLASS = {
            ACTIVE: 'active'
        },
        TPL = {
            SIDEBAR_BOUNDING: '<div><div class="header"></div></div>',
            SIDEBAR_CONTENT: '<ul></ul>'
        };

    /**
     * @class SidebarItem
     * @namespace OJST.ui.widgets
     * @constructor
     * @extends Y.Widget
     * @uses Y.WidgetChild
     */
    OJST.ui.widgets.SidebarItem = Y.Base.create("sidebarItem", Y.Widget, [Y.WidgetChild], {

        CONTENT_TEMPLATE: '<a href="javascript:void(0)"></a>',
        BOUNDING_TEMPLATE: '<li></li>',

        /** @override */
        initializer: function () {
            /**
             * @type {Y.EventHandle[]}
             * @private
             */
            this._subscribers = [];
        },

        /** @override */
        destructor: function () {
            OJST.ui.utils.Framework.detach(this._subscribers);
            delete this._subscribers;
        },

        /** @override */
        renderUI: function () {
            var cbx = this.get(OJST.STATIC.CBX),
                level = this.get('level');

            if (level > 0) {
                cbx.setStyle('paddingLeft', (8 + 5 * level) + 'px');
            }
        }

    }, {
        ATTRS: {
            label: {
                validator: Y.Lang.isString,
                lazyAdd: false,
                /**
                 * @param {string} value
                 * @return {string}
                 */
                setter: function (value) {
                    this.get(OJST.STATIC.CBX).setContent(value);
                    return value;
                }
            },

            active: {
                validator: Y.Lang.isBoolean,
                /**
                 * @return {boolean}
                 */
                getter: function () {
                    return this.get(OJST.STATIC.CBX).hasClass(CLASS.ACTIVE);
                },
                /**
                 * @param {boolean} value
                 * @retrun {boolean}
                 */
                setter: function (value) {
                    this.get(OJST.STATIC.CBX)[value ? 'addClass' : 'removeClass'](CLASS.ACTIVE);
                    return value;
                }
            },

            handler: {
                writeOnce: 'initOnly',
                validator: Y.Lang.isFunction
            },

            scope: {
                writeOnce: 'initOnly',
                validator: Y.Lang.isObject
            },

            level: {
                validator: Y.Lang.isNumber
            },

            visible: {
                validator: Y.Lang.isBoolean,
                /**
                 * @return {boolean}
                 */
                getter: function () {
                    return this.get(OJST.STATIC.CBX).getStyle('display') !== 'none';
                },
                /**
                 * @param {boolean} value
                 * @return {boolean|*}
                 */
                setter: function (value) {
                    this.get(OJST.STATIC.CBX).setStyle('display', value ? 'block' : 'none');
                    return value;
                }
            }
        }
    });

    /**
     * @class Sidebar
     * @namespace OJST.ui.widgets
     * @constructor
     * @extends Y.Widget
     * @uses Y.WidgetParent
     */
    OJST.ui.widgets.Sidebar = Y.Base.create('sidebar', Y.Widget, [Y.WidgetParent], {

        BOUNDING_TEMPLATE: TPL.SIDEBAR_BOUNDING,
        CONTENT_TEMPLATE: TPL.SIDEBAR_CONTENT,

        /** @override */
        initializer: function () {
            /**
             * @type {OJST.ui.widgets.SidebarItem}
             * @private
             */
            this._activeTab = undefined;
        },

        /** @override */
        renderUI: function () {
            var bbx = this.get(OJST.STATIC.BBX);

            this._headerNode = bbx.one('div.header');

            // render title
            this.get('title');

            this.each(function (child) {
                if (child.get('active')) {
                    this._activeTab = child;
                }
            }, this);
        },

        /** @override */
        bindUI: function () {
            this.on("sidebarItem:click", this._onItemClick);
        },

        /**
         * @param {Y.EventFacade} e
         * @private
         */
        _onItemClick: function (e) {
            e.preventDefault();

            if (e.target === this._activeTab) {
                return;
            }

            if (this._activeTab) {
                this._activeTab.set('active', false);
            }
            e.target.set('active', true);
            this._activeTab = e.target;

            var handler = this._activeTab.get('handler'),
                scope = this._activeTab.get('scope');

            if (handler) {
                handler.call(scope || this);
            }
        },

        /**
         * @param {String} value
         * @private
         */
        _setterTitle: function (value) {
            if (value) {
                this._headerNode.set('text', value);
            } else {
                this._headerNode.setStyle('display', 'none');
            }
            return value;
        }

    }, {
        ATTRS: {
            defaultChildType: {
                value: OJST.ui.widgets.SidebarItem
            },
            title: {
                writeOnce: 'initOnly',
                validator: Y.Lang.isString,
                setter: '_setterTitle'
            },
            align: {
                writeOnce: 'initOnly',
                validator: function (v) {
                    return v === ALIGN.LEFT || v === ALIGN.RIGHT;
                }
            }
        }
    });

}, OJST.VERSION, {
    requires: [
        'widget',
        'widget-child',
        'widget-parent'
    ]});