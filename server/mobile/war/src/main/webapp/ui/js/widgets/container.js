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
YUI.add(OJST.ns.widgets.Container, function (Y) {
    "use strict";

    /**
     * @class Container
     * @name OJST.ui.widgets
     * @extends Y.View
     * @constructor
     * @uses Y.Base
     */
    OJST.ui.widgets.Container = Y.Base.create('widgetsContainer', Y.View, [], {

        containerTemplate: '<div></div>',

        /** @override */
        initializer: function (cfg) {
            /**
             * @type {Y.EventHandle[]}
             * @private
             */
            this._subscribers = [];
        },

        /** @override */
        destructor: function () {
            OJST.ui.utils.Framework.detach(this._subscribers);
            var layout = this.get('layout');
            if (layout) {
                layout.clear();
            }
        },

        /**
         * @protected
         */
        syncSize: function () {
            var layout = this.get('layout');
            if (layout) {
                layout.syncSize();
            }
        },

        /**
         * @param {String|Object} value
         * @return {*}
         * @private
         */
        _layoutSetter: function (value) {
            switch (value) {
                case 'border':
                    return new OJST.ui.layouts.Border();
                default:
                    return Y.Attribute.INVALID_VALUE;
            }
        },

        /**
         * @return {OJST.ui.widgets.Container}
         * @protected
         */
        render: function () {
            var container = this.get('container'),
                layout = this.get('layout'),
                items = this.get('items');

            if (items && items.length > 0) {
                Y.each(items, function (item) {
                    layout.add(item);
                }, this);
            }

            layout.render(container);

            return this;
        }

    }, {
        ATTRS: {
            layout: {
                writeOnce: 'initOnly',
                setter: '_layoutSetter'
            },
            items: {
                validator: Y.Lang.isArray
            }
        }
    });


}, OJST.VERSION, {requires: [
    'view', 'widget', 'node'
]});