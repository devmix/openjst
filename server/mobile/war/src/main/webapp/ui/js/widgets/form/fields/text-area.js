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
YUI.add(OJST.ns.widgets.form.fields.TextArea, function (Y) {
    "use strict";

    var TPL = {
        INPUT: '<textarea id="{id}" name="{name}" class="form-control" rows="{rows}"></textarea>'
    };

    /**
     * @class TextArea
     * @namespace OJST.ui.widgets.form.fields
     * @constructor
     * @extends OJST.ui.widgets.form.Field
     */
    OJST.ui.widgets.form.fields.TextArea = Y.Base.create('widgets-form-fields-text-area', OJST.ui.widgets.form.Field, [], {

        /**
         * @override
         */
        initializer: function () {
            /**
             * @type {Y.Node}
             * @private
             */
            this._control = undefined;
        },

        /**
         * @override
         */
        destructor: function () {
            this._control.destroy(true);
            delete this._control;
        },

        /**
         * @override
         */
        renderControl: function (container, controlId) {
            this._control = Y.Node.create(Y.Lang.sub(TPL.INPUT, {
                id: controlId,
                name: this.get('name') || controlId,
                rows: this.get('rows')
            }));

            container.append(this._control);

            return this._control;
        },

        /**
         * @override
         */
        bindUI: function () {
            OJST.ui.widgets.form.fields.TextArea.superclass.bindUI.apply(this, arguments);

            this.subscribe(this._control.on('change', function () {
                if (this._control) {
                    this.set('value', this._control.get('value'));
                }
            }, this));
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
            rows: {
                validator: Y.Lang.isNumber,
                value: 3
            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.widgets.form.Field,
    OJST.ns.utils.Framework
]});