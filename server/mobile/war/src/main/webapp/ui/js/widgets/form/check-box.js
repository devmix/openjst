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
YUI.add(OJST.ns.widgets.form.CheckBox, function (Y) {
    "use strict";

    var TPL = {
        INPUT: '<input id="{id}" type="checkbox">'
    };

    /**
     * @class TextField
     * @namespace OJST.ui.widgets.form
     * @constructor
     * @extends OJST.ui.widgets.form.Field
     */
    OJST.ui.widgets.form.CheckBox = Y.Base.create('widgetsFormCheckBox', OJST.ui.widgets.form.Field, [], {

        /** @override */
        initializer: function () {
            /**
             * @type {Y.Node}
             * @private
             */
            this._control = undefined;
        },

        /** @override */
        renderUI: function () {
            OJST.ui.widgets.form.CheckBox.superclass.renderUI.apply(this, arguments);

            this._control = Y.Node.create(Y.Lang.sub(TPL.INPUT, { id: this.get('controlId') }));

            this.get('value'); // set default value
            this.get('controlsContainer').append(this._control);
        },

        /** @override */
        focus: function () {
            this._control.focus();
        },

        /**
         * @returns {Y.Node}
         * @public
         */
        getValueNode: function () {
            return this._control;
        },

        /** @override */
        isBlank: function () {
            return this.get('value') === false;
        },

        /** @override */
        isEnabled: function () {
            return !this._control.hasAttribute('disabled');
        },

        /** @override */
        disable: function () {
            this._control.setAttribute('disabled', 'disabled');
        },

        /** @override */
        enable: function () {
            this._control.removeAttribute('disabled');
        }

    }, {
        ATTRS: {
            value: {
                getter: function () {
                    return this._control.get('checked');
                },
                setter: function (v) {
                    this._control.set('checked', v);
                    return v;
                }
            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.utils.Html,
    OJST.ns.widgets.form.Field
]});