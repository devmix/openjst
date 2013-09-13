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
YUI.add(OJST.ns.widgets.form.fields.Version, function (Y) {
    "use strict";

    var Framework = OJST.ui.utils.Framework,
        TPL = {
            INPUT: '<div id="{id}" class="form-inline">{elements}</div>',
            ELEMENT: '<div class="form-group col-xs-12 col-sm-2 col-md-2 col-lg-2"><input type="text" class="form-control" placeholder="{label}"></div>'
        };

    /**
     * @class Version
     * @namespace OJST.ui.widgets.form.fields
     * @constructor
     * @extends OJST.ui.widgets.form.Field
     */
    OJST.ui.widgets.form.fields.Version = Y.Base.create('widgets-form-fields-version', OJST.ui.widgets.form.Field, [], {

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
             * @type {Y.NodeList}
             * @private
             */
            this._elements = undefined;
        },

        /**
         * @override
         */
        renderControl: function (container, controlId) {
            var elements = [];
            Y.each(this.get('elements'), function (label) {
                elements.push(Y.Lang.sub(TPL.ELEMENT, {label: label}));
            });

            this._control = Y.Node.create(Y.Lang.sub(TPL.INPUT, {
                id: controlId,
                elements: elements.join('')
            }));

            this._elements = this._control.all('input.form-control');

            this._setterValue(this.get('value'));

            container.append(this._control);

            return this._control;
        },

        /**
         * @override
         */
        bindUI: function () {
            OJST.ui.widgets.form.fields.Version.superclass.bindUI.apply(this, arguments);
            this.subscribe(this._elements.on('change', this._onChangeElements, this));
        },

        /**
         * @override
         */
        isValid: function () {
            var valid = OJST.ui.widgets.form.fields.Version.superclass.isValid.apply(this, arguments),
                value = this.get('value'),
                i, length, parts;

            if (valid && this.get('requireFullVersion')) {
                parts = Framework.isValue(value) ? value.split(this.get('delimiter')) : [];
                valid = this._elements.size() === parts.length;
                if (valid) {
                    for (i = 0, length = parts.length; i < length; i++) {
                        if (!Framework.isValue(parts[i])) {
                            valid = false;
                            break;
                        }
                    }
                }
                return valid || OJST.i18n.msg('requiresFullVersion');
            }

            return valid;
        },

        /**
         * @private
         */
        _onChangeElements: function () {
            var parts = [];
            this._elements.each(function (e) {
                var value = String(e.get('value')).trim();
                if (Framework.isValue(value)) {
                    parts.push(value);
                } else {
                    parts.push('');
                }
            });
            this.set('value', parts.join(this.get('delimiter')));
        },

        /**
         * @param {*} value
         * @returns {string}
         * @private
         */
        _setterValue: function (value) {
            if (!this._elements || this._elements.isEmpty()) {
                return Y.Attribute.INVALID_VALUE;
            }

            var v, parts;

            if (Framework.isValue(value)) {
                v = String(value).trim();
                parts = v.split(this.get('delimiter'));
            } else {
                v = value;
                parts = [];
            }

            this._elements.each(function (e, i) {
                if (i < parts.length) {
                    e.set('value', String(parts[i]).trim());
                } else {
                    e.set('value', '');
                }
            });

            return v;
        }

    }, {
        ATTRS: {
            value: {
                setter: '_setterValue'
            },
            elements: {
                validator: Y.Lang.isArray,
                value: ['Major', 'Minor', 'Build']
            },
            delimiter: {
                validator: Y.Lang.isString,
                value: '.'
            },
            requireFullVersion: {
                validator: Y.Lang.isBoolean,
                value: true
            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.widgets.form.Field,
    OJST.ns.utils.Framework
]});