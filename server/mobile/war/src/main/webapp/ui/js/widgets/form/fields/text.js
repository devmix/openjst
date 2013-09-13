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
/*jslint nomen:true, node:true, white:true, browser:true, plusplus:true, stupid: true*/
YUI.add(OJST.ns.widgets.form.fields.Text, function (Y) {
    "use strict";

    var TPL = {
        INPUT: '<input class="form-control" placeholder="{placeholder}" type="{type}" id="{id}" name="{name}">',
        TRIGGER_BUTTON: '<span class="input-group-btn btn-group"><button class="btn btn-default" type="button">{label}</button></span>',
        TRIGGER_ICON: '<i class="glyphicon glyphicon-{name}"></i> '
    };

    /**
     * @class Text
     * @namespace OJST.ui.widgets.form.fields
     * @constructor
     * @extends OJST.ui.widgets.form.Field
     */
    OJST.ui.widgets.form.fields.Text = Y.Base.create('widgets-form-fields-text', OJST.ui.widgets.form.Field, [], {

        /**
         * @override
         */
        initializer: function () {
            /**
             * @type {Y.Node}
             * @private
             */
            this._control = undefined;
            /**
             * @type {Y.Node}
             * @private
             */
            this._triggerBtn = undefined;
        },

        /**
         * @override
         */
        destructor: function () {
            this._triggerBtn.destroy(true);
            this._control.destroy(true);

            delete this._triggerBtn;
            delete this._control;
        },

        /**
         * @override
         */
        renderControl: function (container, controlId) {
            var trigger = this.get('trigger');

            this._control = Y.Node.create(Y.Lang.sub(TPL.INPUT, {
                id: controlId,
                name: this.get('name') || controlId,
                placeholder: this.get('placeholder') || '',
                type: this.get('isPassword') ? 'password' : 'text'
            }));
            container.append(this._control);

            if (trigger) {
                this._triggerBtn = Y.Node.create(Y.Lang.sub(TPL.TRIGGER_BUTTON, {
                    label: (trigger.icon ? Y.Lang.sub(TPL.TRIGGER_ICON, {name: trigger.icon}) : '')
                        + (trigger.label ? (trigger.icon ? ' ' : '') + trigger.label : '')
                }));
                container.append(this._triggerBtn);
            }

            return this._control;
        },

        /**
         * @override
         */
        bindUI: function () {
            OJST.ui.widgets.form.fields.Text.superclass.bindUI.apply(this, arguments);

            var trigger = this.get('trigger');
            if (this._triggerBtn && trigger && trigger.handler) {
                this.subscribe(this._triggerBtn.on('click', function () {
                    trigger.handler.call(trigger.scope || this, this);
                }, this));
            }

            this.subscribe(this._control.on('change', function () {
                if (this._control) {
                    this.set('value', this._control.get('value'));
                }
            }, this));
        },

        /**
         * @override
         */
        disable: function () {
            OJST.ui.widgets.form.fields.Text.superclass.disable.apply(this, arguments);
            if (this._triggerBtn) {
                this._triggerBtn.one('> button').setAttribute('disabled', 'disabled');
            }
        },

        /**
         * @override
         */
        enable: function () {
            OJST.ui.widgets.form.fields.Text.superclass.enable.apply(this, arguments);
            if (this._triggerBtn) {
                this._triggerBtn.one('> button').removeAttribute('disabled');
            }
        },

        /**
         * @param {*} value
         * @returns {*}
         * @private
         */
        _setterValue: function (value) {
            if (this._control) {
                this._control.set('value', value);
            }
            return value;
        }

    }, {
        ATTRS: {
            value: {
                setter: '_setterValue'
            },
            placeholder: {
                writeOnce: 'initOnly',
                validator: Y.Lang.isString
            },
            isPassword: {
                value: false,
                writeOnce: 'initOnly',
                validator: Y.Lang.isBoolean
            },
            trigger: {
                validator: Y.Lang.isObject,
                writeOnce: true
            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.utils.Html,
    OJST.ns.utils.Framework,
    OJST.ns.widgets.form.Field,
    'dom-base'
]});