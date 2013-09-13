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
YUI.add(OJST.ns.widgets.form.fields.List, function (Y) {
    "use strict";

    var TPL = {
        INPUT: '<input type="hidden" id="{id}" name="{name}"/>'
    };

    /**
     * @class List
     * @namespace OJST.ui.widgets.form.fields
     * @constructor
     * @extends OJST.ui.widgets.form.Field
     */
    OJST.ui.widgets.form.fields.List = Y.Base.create('widgets-form-fields-list', OJST.ui.widgets.form.Field, [], {

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
                name: this.get('name') || controlId
            }));

            container.append(this._control);

            return this._control;
        },

        /**
         * @override
         */
        bindUI: function () {
            Y.once('available', this._initializeSelect2, '#' + this.get('controlId'), this);
        },

        /**
         * @private
         */
        _initializeSelect2: function () {
            var data = [];

            Y.each(this.get('data'), function (item) {
                data.push({id: item[0], text: item[1]});
            });

            $('#' + this.get('controlId')).select2({
                data: data,
                width: '100%'
            });

            this.get('value'); // set default value
        },

        /**
         * @param {String} value
         * @return {String}
         * @private
         */
        _valueSetter: function (value) {
            $('#' + this.get('controlId')).select2('val', value);
            return value;
        },

        /**
         * @return {String}
         * @private
         */
        _valueGetter: function () {
            return $('#' + this.get('controlId')).select2('val');
        }

    }, {
        ATTRS: {
            value: {
                getter: '_valueGetter',
                setter: '_valueSetter'
            },
            data: {

            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.libs.Select2,
    OJST.ns.widgets.form.Field
]});