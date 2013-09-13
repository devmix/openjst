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
YUI.add(OJST.ns.widgets.AbstractWidget, function (Y) {
    "use strict";

    var STR = {
        LOADING: OJST.i18n.msg('loading')
    };

    /**
     * @class AbstractWidget
     * @namespace OJST.ui.widgets
     * @constructor
     * @extends Y.Widget
     */
    OJST.ui.widgets.AbstractWidget = Y.Base.create('widgets-abstract-widget', Y.Widget, [], {

        /** @override */
        initializer: function () {
            /**
             * @type {Y.EventHandle[]}
             * @private
             */
            this._subscribers = [];
            /**
             * @type {OJST.ui.widgets.notify.Mask}
             * @private
             */
            this._mask = undefined;
            /**
             * @type {OJST.ui.widgets.notify.Popup}
             * @private
             */
            this._notification = undefined;
        },

        /** @override */
        destructor: function () {
            var layout = this.get('layout');

            OJST.ui.utils.Framework.detach(this._subscribers);

            if (layout) {
                layout.clear();
            }

            if (this._mask) {
                this._mask.destroy();
            }
            delete this._mask;

            if (this._notification) {
                this._notification.destroy();
            }
            delete this._notification;
        },

        /** @override */
        renderUI: function () {
            var padding = this.get('padding');
            if (padding) {
                this.get(OJST.STATIC.BBX).setStyle('padding', padding + 'px');
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

            if (this._mask) {
                this._mask.syncSize();
            }

            if (this._notification) {
                this._notification.syncSize();
            }
        },

        /**
         * @param {String} type the event type to delegate
         * @param {Function} fn the callback function to execute. This function
         * will be provided the event object for the delegated event.
         * @param {String|Function} spec a selector that must match the target of the
         * event or a function to test target and its parents for a match
         * @param {Object} context  optional argument that specifies what 'this' refers to
         * @param {*} args* 0..n additional arguments to pass on to the callback
         * function. These arguments will be added after the event object.
         * @return {EventHandle} the detach handle
         * @public
         */
        delegate: function (type, fn, spec, context, args) {
            var contentBox = this.get(OJST.STATIC.BBX);
            return contentBox.delegate.apply(contentBox, arguments);
        },

        /**
         * @param {EventHandle} handler
         * @public
         */
        subscribe: function (handler) {
            if (handler) {
                this._subscribers.push(handler);
            }
        },

        /**
         * @public
         */
        mask: function () {
            if (!this._mask) {
                this._mask = new OJST.ui.widgets.notify.Mask({ node: this.get(OJST.STATIC.BBX), message: STR.LOADING });
            }
            this._mask.show();
        },

        /**
         * @public
         */
        unmask: function () {
            if (this._mask) {
                this._mask.hide();
            }
        },

        /**
         * @param {string} message
         * @param {'success'|'info'|'warning'|'danger'} type
         * @param {number} hideDelay
         * @public
         */
        showNotification: function (message, type, hideDelay) {
            if (!this._notification) {
                this._notification = new OJST.ui.widgets.notify.Popup({ parent: this.get(OJST.STATIC.BBX) });
            }
            this._notification.show(message, type, hideDelay);
        },

        /**
         * @param {boolean} clearQueue
         * @public
         */
        hideNotification: function (clearQueue) {
            if (this._notification) {
                this._notification.hide(clearQueue);
            }
        }

    }, {
        ATTRS: {
            layout: {
                setter: function (value) {
                    switch (value) {
                        case 'border':
                            return new OJST.ui.layouts.Border();
                        default:
                            return Y.Attribute.INVALID_VALUE;
                    }
                }
            },
            padding: {
                validator: Y.Lang.isNumber
            }
        }
    });


}, OJST.VERSION, {requires: [
    OJST.ns.widgets.notify.Mask,
    OJST.ns.widgets.notify.Popup,
    OJST.ns.layouts.Border,
    'widget', 'widget-child'
]});