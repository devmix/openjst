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
            INVALID: 'has-error',
            REQUIRED: 'required'
        },
        TPL = {
            CONTROLS: '<div class="form-group">' +
                '   <div class="input-group col-xs-12 col-sm-9 col-md-9 col-lg-9"></div>' +
                '</div>',
            LABEL: '<label class="col-xs-12 col-sm-3 col-md-3 col-lg-3 control-label {requiredClass}" for="{controlId}">{label}</label>'
        },
        Framework = OJST.ui.utils.Framework;

    /**
     * @class Field
     * @namespace OJST.ui.widgets.form
     * @constructor
     * @extends Y.Widget
     * @uses Y.WidgetChild
     */
    OJST.ui.widgets.form.Field = Y.Base.create('form-field', OJST.ui.widgets.AbstractWidget, [Y.WidgetChild], {

        CONTENT_TEMPLATE: null,
        BOUNDING_TEMPLATE: TPL.CONTROLS,

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
                labelContainer = Y.Node.create(Y.Lang.sub(TPL.LABEL, {
                    controlId: controlId, label: label, requiredClass: this.get('required') ? CLS.REQUIRED : '' }));

            if (label) {
                bbx.prepend(labelContainer);
            }

            this.set('controlsContainer', bbx.one('.input-group'));
            this.set('labelContainer', labelContainer);
            this.set('controlId', controlId);
        },

        /** @override */
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
            Framework.setToolTip(this.getValueNode(), msg, 'bottom');
        },

        /**
         * @public
         */
        clearInvalid: function () {
            this.get(OJST.STATIC.BBX).removeClass(CLS.INVALID);
            Framework.removeToolTip(this.getValueNode());
        },

        /**
         * @return {boolean}
         * @public
         */
        isBlank: function () {
            return true;
        },

        /**
         * @return {boolean}
         * @public
         */
        isEnabled: function () {
            return true;
        },

        /**
         * @public
         */
        disable: function () {
            return undefined;
        },

        /**
         * @public
         */
        enable: function () {
            return undefined;
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
    OJST.ns.widgets.AbstractWidget,
    'widget-child'
]});