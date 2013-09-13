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
YUI.add(OJST.ns.widgets.toolbar.Controls, function (Y) {
    "use strict";

    /**
     * @class Controls
     * @namespace OJST.ui.widgets.toolbar
     * @constructor
     * @extends OJST.ui.widgets.AbstractWidget
     */
    OJST.ui.widgets.toolbar.Controls = Y.Base.create('widgets-toolbar-controls', OJST.ui.widgets.AbstractWidget, [Y.WidgetChild], {

        BOUNDING_TEMPLATE: '<div class="form-inline"></div>',
        CONTENT_TEMPLATE: null,

        /** @override */
        initializer: function () {
            this._callbacks = {};
            this._alerts = {};
        },

        /** @override */
        renderUI: function () {
            this.get(OJST.STATIC.BBX)
                .set('innerHTML', this._renderControls(this.get('controls') || []));
        },

        /** @override */
        bindUI: function () {
            this.subscribe(this.after('controlsChange', this.renderUI, this));
            this.subscribe(this.get(OJST.STATIC.BBX).delegate('click', function (e) {
                var callback = this._callbacks[e.target.get('id')];
                if (callback) {
                    callback.handler.call(callback.scope || this);
                }
            }, 'button', this));
        },

        /**
         * @param {object[]} controls
         * @param {string} [defaultType = 'button']
         * @returns {string}
         * @private
         */
        _renderControls: function (controls, defaultType) {
            var i, length, control, html = [];
            for (i = 0, length = controls.length; i < length; i++) {
                control = controls[i];
                switch (control.type || defaultType || 'button') {
                    case 'btn-group':
                        html.push(this._renderBtnGroup(control));
                        break;

                    case 'button':
                        html.push(this._renderButton(control));
                        break;
                }
            }

            return html.join('');
        },

        /**
         * @param {object} control
         * @returns {string}
         * @private
         */
        _renderBtnGroup: function (control) {
            return '<div class="btn-group'
                + (control.align === 'right' ? ' pull-right' : '')
                + '">' + this._renderControls(control.controls, 'button') + '</div>';
        },

        /**
         * @param {object} control
         * @returns {string}
         * @private
         */
        _renderButton: function (control) {
            var controlId = Y.guid();
            if (control.handler) {
                this._callbacks[controlId] = {
                    handler: control.handler,
                    scope: control.scope
                };
            }

            return '<button id="' + controlId + '" type="button" class="btn btn-default'
                + (control.primary ? ' btn-primary' : '')
                + (control.align === 'right' ? ' pull-right' : '')
                + '"'
                + (control.minWidth ? ' style="min-width:' + parseInt(control.minWidth, 10) + 'px"' : '')
                + '>'
                + (control.icon ? '<i class="glyphicon glyphicon-' + control.icon + '"></i> ' : '')
                + (control.label || '') + '</button>';
        }

    }, {
        ATTRS: {
            controls: {
                validator: Y.Lang.isArray
            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.widgets.AbstractWidget,
    'widget-child'
]});