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
YUI.add(OJST.modules.widgets.form.ListField, function (Y) {
    "use strict";

    /**
     * @class ListField
     * @namespace OJST.ui.widgets.form
     * @constructor
     * @extends OJST.ui.widgets.form.Field
     */
    OJST.ui.widgets.form.ListField = Y.Base.create('widgetsFormListField', OJST.ui.widgets.form.Field, [], {

        /** @override */
        renderUI: function () {
            OJST.ui.widgets.form.ListField.superclass.renderUI.apply(this, arguments);

            this._controlId = this.get('controlId');
            this._control = Y.Node.create(Y.Lang.sub('<input type="hidden" id="{id}" name="{name}"/>',
                {
                    id: this._controlId,
                    name: this.get('name') || this._controlId
                }));

            this.get('controlsContainer').append(this._control);
        },

        /** @override */
        bindUI: function () {
            Y.once('available', this._initializeSelect2, '#' + this.get('controlId'), this);
        },

        /**
         * @private
         */
        _initializeSelect2: function () {
            var controlId = this.get('controlId'), data = [];

            Y.each(this.get('data'), function (item) {
                data.push({id: item[0], text: item[1]});
            });

            $('#' + controlId).select2({
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
            $('#' + this._controlId).select2('val', value);
            return value;
        },

        /**
         * @return {String}
         * @private
         */
        _valueGetter: function () {
            return $('#' + this._controlId).select2('val');
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

}, OJST.VERSION, {
    requires: [
        OJST.modules.widgets.form.Field
    ]});