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
YUI.add(OJST.modules.widgets.form.TextField, function (Y) {
    "use strict";

    /**
     * @class TextField
     * @namespace OJST.ui.widgets.form
     * @constructor
     * @extends OJST.ui.widgets.form.Field
     */
    OJST.ui.widgets.form.TextField = Y.Base.create('widgetsFormTextField', OJST.ui.widgets.form.Field, [], {

        renderUI: function () {
            OJST.ui.widgets.form.TextField.superclass.renderUI.apply(this, arguments);

            var id = this.get('id');

            //noinspection JSValidateTypes
            this._control = Y.Node.create(Y.Lang.sub(
                '<input class="input-block-level" placeholder="{placeholder}" type="{type}" id="{id}" name="{name}">',
                {
                    id: id,
                    name: this.get('name') || id,
                    placeholder: this.get('placeholder') || '',
                    type: this.get('isPassword') ? 'password' : 'text'
                }
            ));

            this.get('value'); // set default value

            this.get('controlsContainer').append(this._control);
        },

        _valueSetter: function (value) {
            this._control.set('value', value);
            return value;
        },

        _valueGetter: function () {
            return this._control.get('value');
        },

        /** @override */
        focus: function () {
            this._control.focus();
        }

    }, {
        ATTRS: {
            value: {
                getter: '_valueGetter',
                setter: '_valueSetter'
            },
            placeholder: {
                writeOnce: 'initOnly',
                validator: Y.Lang.isString
            },
            isPassword: {
                value: false,
                writeOnce: 'initOnly',
                validator: Y.Lang.isBoolean
            }
        }
    });

}, OJST.VERSION, {
    requires: [
        OJST.modules.widgets.form.Field
    ]});