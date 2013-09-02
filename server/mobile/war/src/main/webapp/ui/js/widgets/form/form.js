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
YUI.add(OJST.ns.widgets.form.Form, function (Y) {
    "use strict";

    var EVT = {
        LOAD: 'load',
        UPDATE: 'update',
        CREATE: 'create'
    };

    /**
     * @class Form
     * @namespace OJST.ui.widgets.form
     * @constructor
     * @extends OJST.ui.widgets.AbstractWidget
     * @uses Y.WidgetChild
     */
    OJST.ui.widgets.form.Form = Y.Base.create('widgets-form', OJST.ui.widgets.AbstractWidget, [Y.WidgetChild], {

        BOUNDING_TEMPLATE: '<div></div>',
        CONTENT_TEMPLATE: null,

        /** @override */
        initializer: function () {
            /**
             * @type {OJST.ui.widgets.form.FormFields}
             * @private
             */
            this._fields = undefined;
            /**
             * @type {boolean}
             * @private
             */
            this._isNew = true;

            this.set('layout', 'border');
        },

        /** @override */
        renderUI: function () {
            var bbx = this.get(OJST.STATIC.BBX),
                children = this.get('children'),
                buttons = this.get('buttons'),
                layout = this.get('layout'),
                useDefaultButtons = this.get('useDefaultButtons');

            if (children && children.length > 0) {
                this._fields = new OJST.ui.widgets.form.FormFields({
                    children: children
                });
                layout.add({region: 'center', element: this._fields});
            }

            if ((buttons && buttons.length > 0) || useDefaultButtons) {
                if (useDefaultButtons) {
                    layout.add({region: 'bottom', element: new OJST.ui.widgets.toolbar.Controls({
                        controls: [
                            {
                                label: OJST.i18n.label('close'),
                                align: 'right',
                                handler: function () {
                                    this.fire('close');
                                },
                                scope: this
                            },
                            {
                                label: OJST.i18n.label('save'),
                                align: 'right',
                                primary: true,
                                handler: function () {
                                    this._onSave();
                                },
                                scope: this
                            }
                        ]
                    })});
                }
            }

            layout.render(bbx);
        },

        /** @override */
        bindUI: function () {
            var model = this.get('model');

            this._checkNew(model);

            this.subscribe(this.get(OJST.STATIC.BBX).on('submit', function (e) {
                e.preventDefault();
            }));

            if (!this._isNew && this.get('auto') && model) {
                this.mask();
                model.load(Y.bind(this._onLoad, this));
            } else {
                this._focusOnFirst();
            }

            this.subscribe(model.on('error', function (e) {
                var msg;
                switch (e.status) {
                    case OJST.ui.models.STATUS.NOT_EXISTS:
                        msg = OJST.i18n.msg('errorNotExists');
                        break;
                    case OJST.ui.models.STATUS.INTERNAL_SERVER_ERROR:
                        msg = OJST.i18n.msg('errorInternalServerError');
                        break;
                }

                if (msg) {
                    OJST.ui.widgets.Alerts.alert(OJST.i18n.label('error'), msg);
                }

                this.fire('error', {
                    status: e.status,
                    errorData: e.errorData
                });
            }, this));
        },

        /**
         * @param {Y.Model} model
         * @return {boolean}
         * @public
         */
        updateModel: function (model) {
            var valid = true;
            this._fields.each(function (item) {
                if (item.validate()) {
                    var value = item.get('value');
                    model.set(item.get('name'), Y.Lang.isValue(value) ? value : null);
                } else {
                    valid = false;
                }
            });
            return valid;
        },

        /**
         * @param {Y.Model} model
         * @public
         */
        loadModel: function (model) {
            this._fields.each(function (item) {
                var name = item.get('name');
                item.set('value', model.get(name));
                if (item.validate) {
                    item.validate();
                }
            });
            return model;
        },

        /**
         * @public
         */
        validate: function () {
            var isValid = true;
            this._fields.each(function (item) {
                if (item.validate) {
                    isValid = isValid && item.validate();
                }
            });
            return isValid;
        },

        /**
         * @param {Model} m
         * @private
         */
        _checkNew: function (m) {
            this._isNew = !m || !Y.Lang.isValue(m.get('id'));
        },

        /**
         * @private
         */
        _onSave: function () {
            var model = this.get('model');

            if (this.get('auto') && model) {
                this.hideNotification(true);
                if (this.updateModel(model)) {
                    this.mask();
                    model.save(Y.bind(function () {
                        this.unmask();
                        if (!model.isLastOperationFailed()) {
                            this.fire(this._isNew ? EVT.CREATE : EVT.UPDATE, model);
                            this._checkNew(model);
                            this.showNotification(OJST.i18n.msg('successSaving'), 'success', 0);
                        } else {
                            this.showNotification(OJST.i18n.msg('errorSaving'), 'danger', 2000);
                        }
                    }, this));
                } else {
                    this.showNotification(OJST.i18n.msg('errorSomeFieldsFilledIncorrect'), 'danger', 1000);
                }
            } else {
                this.fire(EVT.UPDATE);
            }
        },

        /**
         * @private
         */
        _onLoad: function () {
            this.unmask();
            var model = this.get('model');

            this.loadModel(model);
            this.fire(EVT.LOAD, model);

            this._focusOnFirst();
        },

        /**
         * @private
         */
        _focusOnFirst: function () {
            var firstItem = this._fields.item(0);
            if (firstItem) {
                firstItem.focus();
            }
        }

    }, {
        ATTRS: {
            children: {
                writeOnce: 'initOnly'
            },
            buttons: {
                writeOnce: 'initOnly',
                validator: Y.Lang.isArray
            },
            useDefaultButtons: {
                writeOnce: 'initOnly',
                validator: Y.Lang.isBoolean
            },
            auto: {
                writeOnce: 'initOnly',
                validator: Y.Lang.isBoolean
            },
            model: {
                writeOnce: 'initOnly',
                validator: function (v) {
                    return v instanceof OJST.ui.models.Base;
                }
            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.widgets.AbstractWidget,
    OJST.ns.widgets.form.FormFields,
    OJST.ns.widgets.toolbar.Controls,
    OJST.ns.widgets.Alerts,
    'widget-child'
]});