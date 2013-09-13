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
            LABEL: '<label class="col-xs-12 col-sm-3 col-md-3 col-lg-3 control-label {requiredClass}" for="{controlId}">' +
                '{label}' +
                '<span style="float:right; display: none;" class="icon-error glyphicon glyphicon-exclamation-sign"></span>' +
                '</label>'
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
            /**
             * @type {Y.Node}
             * @private
             */
            this._labelIconErrorNode = undefined;
            /**
             * @type {Y.Node}
             * @private
             */
            this._labelNode = undefined;
            /**
             * @type {Y.Node}
             * @private
             */
            this._controlNode = undefined;
        },

        /** @override */
        destructor: function () {
            OJST.ui.utils.Framework.detach(this._subscribers);
            this._labelNode.destroy(true);

            delete this._controlNode;
            delete this._labelNode;
            delete this._labelIconErrorNode;
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
                label = this.get('label');

            this.set('controlId', controlId);

            if (label) {
                this._labelNode = Y.Node.create(Y.Lang.sub(TPL.LABEL, {
                    controlId: controlId,
                    label: label,
                    requiredClass: this.get('required') ? CLS.REQUIRED : ''
                }));
                this._labelIconErrorNode = this._labelNode.one('span.icon-error');
                bbx.prepend(this._labelNode);
            }

            this._controlNode = this.renderControl(bbx.one('.input-group'), controlId);
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

        /** @override */
        syncUI: function () {
            if (Framework.isValue(this.get('value'))) {
                this.validate();
            }
        },

        /**
         * @param {Y.Node} container
         * @param {string} controlId
         * @return {Y.Node}
         * @protected
         */
        renderControl: function (container, controlId) {
            return undefined;
        },

        /**
         * @return {Y.Node}
         * @protected
         */
        getControlNode: function () {
            return this._controlNode || this.get(OJST.STATIC.BBX);
        },

        /**
         * @returns {boolean|string}
         * @public
         */
        isValid: function () {
            return !this.get('required') || Framework.isValue(this.get('value'));
        },

        /**
         * @return {boolean}
         * @public
         */
        validate: function () {
            var result = this.isValid();
            if (result === false) {
                this.markInvalid(OJST.i18n.msg('errorRequiredField'));
            } else if (Y.Lang.isString(result)) {
                this.markInvalid(result);
            } else {
                this.clearInvalid();
            }
            return result === true;
        },

        /**
         * @public
         */
        markInvalid: function (msg) {
            this.get(OJST.STATIC.BBX).addClass(CLS.INVALID);
            var labelIcon = this._labelIconErrorNode,
                controlNode = this.getControlNode();
            if (labelIcon) {
                Framework.setToolTip(labelIcon.setStyle('display', 'inline'), msg, 'bottom');
            }
            if (controlNode) {
                controlNode.setAttribute('title', msg);
            }
        },

        /**
         * @public
         */
        clearInvalid: function () {
            this.get(OJST.STATIC.BBX).removeClass(CLS.INVALID);
            var labelIcon = this._labelIconErrorNode,
                controlNode = this.getControlNode();
            if (labelIcon) {
                Framework.removeToolTip(labelIcon.setStyle('display', 'none'));
            }
            if (controlNode) {
                controlNode.removeAttribute('title');
            }
        },

        /**
         * @return {boolean}
         * @public
         */
        isBlank: function () {
            return OJST.ui.utils.Framework.isValue(this.get('value'));
        },

        /**
         * @return {boolean}
         * @public
         */
        isEnabled: function () {
            var node = this.getControlNode();
            return node ? !node.hasAttribute('disabled') : true;
        },

        /**
         * @public
         */
        disable: function () {
            var node = this.getControlNode();
            if (node) {
                node.setAttribute('disabled', 'disabled');
            }
        },

        /**
         * @public
         */
        enable: function () {
            var node = this.getControlNode();
            if (node) {
                node.removeAttribute('disabled');
            }
        },

        /**
         * @public
         */
        focus: function () {
            var node = this.getControlNode();
            if (node) {
                node.focus();
            }
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
    OJST.ns.utils.Framework,
    'widget-child'
]});