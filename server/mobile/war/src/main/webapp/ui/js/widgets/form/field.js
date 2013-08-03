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
YUI.add(OJST.ns.widgets.form.Field, function (Y) {
    "use strict";

    var CLS = {
            INVALID: 'invalid',
            REQUIRED: 'required'
        },
        TPL = {
            LABEL: '<label class="control-label {requiredClass}" for="{controlId}">{label}</label>'
        },
        Framework = OJST.ui.utils.Framework;

    /**
     * @class Field
     * @namespace OJST.ui.widgets.form
     * @constructor
     * @extends Y.Widget
     * @uses Y.WidgetChild
     */
    OJST.ui.widgets.form.Field = Y.Base.create('form-field', Y.Widget, [Y.WidgetChild], {

        CONTENT_TEMPLATE: null,
        BOUNDING_TEMPLATE: '<div class="control-group"></div>',

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

        /**
         * @param {Y.EventHandle} handler
         * @public
         */
        subscribe: function (handler) {
            this._subscribers.push(handler);
        },

        /** @override */
        renderUI: function () {
            var bbx = this.get(OJST.STATIC.BBX),
                controlId = Y.guid(),
                label = this.get('label'),
                controlsContainer = Y.Node.create('<div class="controls"></div>'),
                labelContainer = Y.Node.create(Y.Lang.sub(TPL.LABEL, {
                    controlId: controlId, label: label, requiredClass: this.get('required') ? CLS.REQUIRED : '' }));

            if (label) {
                bbx.prepend(labelContainer);
            }

            bbx.append(controlsContainer);

            this.set('controlsContainer', controlsContainer);
            this.set('labelContainer', labelContainer);
            this.set('controlId', controlId);
        },

        bindUI: function () {
            this.subscribe(this.on('focus', function () {
                this.validate();
            }));
            this.subscribe(this.on('blur', function () {
                this.validate();
            }));
        },

        /**
         * @return {Y.Node}
         * @protected
         */
        getValueNode: function () {
            return this.get(OJST.STATIC.BBX);
        },

        /**
         * @return {boolean}
         * @public
         */
        validate: function () {
            var isValid = !this.get('required') || Framework.isValue(this.get('value'));
            if (!isValid) {
                this.markInvalid(OJST.i18n.msg('errorRequiredField'));
            } else {
                this.clearInvalid();
            }
            return isValid;
        },

        /**
         * @public
         */
        markInvalid: function (msg) {
            this.get(OJST.STATIC.BBX).addClass(CLS.INVALID);
            Framework.setToolTip(this.getValueNode(), msg, 'left');
        },

        /**
         * @public
         */
        clearInvalid: function () {
            this.get(OJST.STATIC.BBX).removeClass(CLS.INVALID);
            Framework.removeToolTip(this.getValueNode());
        }

    }, {
        ATTRS: {
            label: {
                validator: Y.Lang.isString,
                writeOnce: 'initOnly'
            },
            name: {
                validator: Y.Lang.isString,
                writeOnce: 'initOnly'
            },
            value: {

            },
            controlId: {
                validator: Y.Lang.isString,
                writeOnce: true
            },
            tabIndex: {
                value: -1
            },
            required: {
                validator: Y.Lang.isBoolean,
                value: false
            },
            valid: {
                validator: Y.Lang.isBoolean,
                value: true
            }
        }
    });

}, OJST.VERSION, {requires: [
    'widget', 'widget-child'
]});