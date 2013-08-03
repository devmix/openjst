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
YUI.add(OJST.ns.widgets.Breadcrumbs, function (Y) {
    "use strict";

    var Lang = Y.Lang,
        TPL = {
            TITLE: '<div class="title">{title}</div>'
        };

    /**
     * @class BreadcrumbsItem
     * @namespace OJST.ui.widgets
     * @constructor
     * @extends Y.Widget
     * @uses Y.WidgetChild
     */
    OJST.ui.widgets.BreadcrumbsItem = Y.Base.create("breadcrumbsItem", Y.Widget, [Y.WidgetChild], {

        CONTENT_TEMPLATE: '<a href="javascript:void(0)"></a>',
        BOUNDING_TEMPLATE: '<div class="tab"></div>',

        /** @override */
        initializer: function (config) {
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
            this.get('label');
        },

        /** @override */
        bindUI: function () {
            this._subscribers.push(this.on('click', this._onClick, this));
        },

        /**
         * @private
         */
        _onClick: function () {
            var handler = this.get('handler');
            if (handler) {
                handler.call(this.get('scope') || this);
            }
        }

    }, {
        ATTRS: {
            label: {
                validator: Y.Lang.isString,
                setter: function (value) {
                    this.get(OJST.STATIC.CBX).setContent(value);
                    return value;
                }
            },
            handler: {
                validator: Y.Lang.isFunction
            },
            scope: {
                validator: Y.Lang.isObject
            }
        }
    });

    /**
     * @class Breadcrumbs
     * @namespace OJST.ui.widgets
     * @extends Y.Widget
     * @constructor
     * @uses Y.WidgetChild
     * @uses Y.WidgetParent
     */
    OJST.ui.widgets.Breadcrumbs = Y.Base.create('widgetsBreadcrumbs', Y.Widget, [Y.WidgetChild, Y.WidgetParent], {

        BOUNDING_TEMPLATE: '<div></div>',
        CONTENT_TEMPLATE: null,

        /** @override */
        initializer: function (config) {
            /**
             * @type {Y.Node}
             * @private
             */
            this._titleNode = undefined;
        },

        /** @override */
        destroy: function () {
            delete this._titleNode;
        },

        /** @override */
        renderUI: function () {
            // create title
            this.get('title');
        },

        /**
         * @param {String} title
         * @private
         */
        _setterTitle: function (title) {
            if (title && !this._titleNode) {
                this._titleNode = Y.Node.create(Y.Lang.sub(TPL.TITLE, {title: title}));
                this.get(OJST.STATIC.BBX).prepend(this._titleNode);
            }
            if (this._titleNode) {
                this._titleNode.set('text', title);
            }
        }

    }, {
        ATTRS: {
            defaultChildType: {
                value: OJST.ui.widgets.BreadcrumbsItem
            },
            title: {
                validator: Lang.isString,
                setter: '_setterTitle'
            }
        }
    });

}, OJST.VERSION, {requires: [
    'base', 'widget', 'widget-child', 'widget-parent'
]});