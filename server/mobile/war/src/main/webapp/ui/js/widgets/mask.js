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
YUI.add(OJST.modules.widgets.Mask, function (Y) {
    "use strict";

    /**
     * @class Mask
     * @namespace OJST.ui.widgets
     * @param {Object} config
     * @constructor
     * @extends Y.Base
     */
    OJST.ui.widgets.Mask = Y.Base.create('widgetsMask', Y.Base, [], {

        /** @override */
        initializer: function (cfg) {
            /**
             * @type {Y.Node}
             * @private
             */
            this._maskNode = undefined;
            /**
             * @type {Y.Node}
             * @private
             */
            this._messageNode = undefined;
            /**
             * @type {boolean}
             * @private
             */
            this._active = false;
        },

        /** @override */
        destructor: function () {
            this.hide();
        },

        /**
         * @public
         */
        show: function () {
            if (this._active) {
                return;
            }

            var parentNode = this.get('node');

            this._maskNode = Y.Node.create('<div></div>').addClass('yui3-widgetsmask');
            this._maskNode.setStyles({
                position: 'absolute',
                width: '100%',
                height: '100%',
                top: '0',
                left: '0',
                display: 'block'
            });

            this._messageNode = Y.Node.create(
                '<div class="yui3-widgetsmask-message"><div class="icon"></div><span class="message">' + this.get('message') + '</span></div>');

            this._messageNode.setStyles({
                position: 'absolute'
            });

            parentNode.append(this._maskNode);
            parentNode.append(this._messageNode);

            this._active = true;

            this.syncSize();
        },

        /**
         * @public
         */
        hide: function () {
            if (this._active) {
                this._maskNode.remove(true);
                this._messageNode.remove(true);
                this._active = false;
            }
            delete this._maskNode;
            delete this._messageNode;
        },

        /**
         * @public
         */
        syncSize: function () {
            if (!this._active) {
                return;
            }

            var parentNode = this.get('node'),
                msgHeight = this._messageNode.get('offsetHeight'),
                msgWidth = this._messageNode.get('offsetWidth');

            if (msgWidth < 300) {
                msgWidth = 200;
                this._messageNode.setStyle('width', msgWidth + 'px');
            }

            this._messageNode.setStyles({
                top: parseInt((parentNode.get('offsetHeight') / 2) - (msgHeight / 2), 10) + 'px',
                left: parseInt((parentNode.get('offsetWidth') / 2) - (msgWidth / 2), 10) + 'px'
            });
        }

    }, {
        ATTRS: {
            node: {
                writeOnce: 'initOnly',
                validator: function (v) {
                    return v instanceof Y.Node;
                }
            },
            message: {
                validator: Y.Lang.isString
            }
        }
    });

}, OJST.VERSION, {
    requires: [
        'base'
    ]});