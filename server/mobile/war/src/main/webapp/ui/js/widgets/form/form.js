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
YUI.add(OJST.modules.widgets.form.Form, function (Y) {
    "use strict";

    var EVT = {
        LOAD: 'load',
        UPDATE: 'update',
        CREATE: 'create'
    };

    /**
     * @class FormFields
     * @namespace OJST.ui.widgets.form
     * @constructor
     * @extends Y.Widget
     * @uses Y.WidgetChild
     * @uses Y.WidgetParent
     */
    OJST.ui.widgets.form.FormFields = Y.Base.create('widgetsFormFields', Y.Widget, [Y.WidgetParent, Y.WidgetChild], {

        BOUNDING_TEMPLATE: '<form></form>',
        CONTENT_TEMPLATE: null,

        /** @override */
        renderUI: function () {
            var bbx = this.get(OJST.STATIC.BBX);
            if (this.get('horizontal')) {
                bbx.addClass('form-horizontal');
            }
        }

    }, {
        ATTRS: {
            horizontal: {
                writeOnce: 'initOnly',
                validator: Y.Lang.isBoolean
            }
        }
    });

    /**
     * @class Form
     * @namespace OJST.ui.widgets.form
     * @constructor
     * @extends OJST.ui.widgets.AbstractWidget
     * @uses Y.WidgetChild
     */
    OJST.ui.widgets.form.Form = Y.Base.create('widgetsForm', OJST.ui.widgets.AbstractWidget, [Y.WidgetChild], {

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
             * @type {Y.Button}
             * @private
             */
            this._saveBtn = undefined;
            /**
             * @type {Y.Button}
             * @private
             */
            this._closeBtn = undefined;
            /**
             * @type {boolean}
             * @private
             */
            this._isNew = true;
            /**
             * @type {Y.Node}
             * @private
             */
            this._alertMessageNode = undefined;

            this.set('layout', 'border');
        },

        /** @override */
        destructor: function () {
            delete this._fields;
            delete this._saveBtn;
            delete this._closeBtn;
            delete this._alertMessageNode;
        },

        /** @override */
        renderUI: function () {
            var bbx = this.get(OJST.STATIC.BBX),
                children = this.get('children'),
                buttons = this.get('buttons'),
                layout = this.get('layout'),
                useDefaultButtons = this.get('useDefaultButtons'),
                buttonsPanel;

            if (children && children.length > 0) {
                this._fields = new OJST.ui.widgets.form.FormFields({
                    children: children,
                    horizontal: this.get('horizontal')
                });
                layout.add({region: 'center', element: this._fields});
            }

            if ((buttons && buttons.length > 0) || useDefaultButtons) {
                buttonsPanel = Y.Node.create('<div style="text-align:right"><div class="alert" style="display:none;float:left;"></div></div>');
                this._alertMessageNode = buttonsPanel.one('div.alert');
                if (useDefaultButtons) {
                    this._closeBtn = new Y.Button({
                        render: buttonsPanel,
                        label: ('Close'),
                        on: {
                            click: Y.bind(function () {
                                this.fire('close');
                            }, this)
                        }
                    });
                    this._saveBtn = new Y.Button({
                        render: buttonsPanel,
                        label: OJST.i18n.label('save'),
                        on: {
                            click: Y.bind(this._onSave, this)
                        }
                    });
                }
                layout.add({region: 'bottom', element: buttonsPanel});
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
                this._hideAlertMessage();
                if (this.updateModel(model)) {
                    this.mask();
                    model.save(Y.bind(function () {
                        this.unmask();
                        if (!model.isLastOperationFailed()) {
                            this.fire(this._isNew ? EVT.CREATE : EVT.UPDATE, model);
                            this._checkNew(model);
                            this._showAlertMessage('Data has been successfully saved', 'success');
                        } else {
                            this._showAlertMessage('Error saving data!', 'error');
                        }
                    }, this));
                } else {
                    this._showAlertMessage('Some fields filled incorrect!', 'error');
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
        },

        /**
         * @param {string} message
         * @private
         */
        _showAlertMessage: function (message, type) {
            if (this._alertMessageNode) {
                this._alertMessageNode.setAttribute('class', '');
                this._alertMessageNode.addClass('alert alert-' + type);
                this._alertMessageNode.setStyle('display', 'block');
                this._alertMessageNode.set('text', message);
            } else {
                OJST.ui.widgets.Alerts.alert(OJST.i18n.label('error', message));
            }
        },

        /**
         * @private
         */
        _hideAlertMessage: function () {
            if (this._alertMessageNode) {
                this._alertMessageNode.setStyle('display', 'none');
                this._alertMessageNode.set('text', '');
            }
        }

    }, {
        ATTRS: {
            horizontal: {
                writeOnce: 'initOnly',
                validator: Y.Lang.isBoolean
            },
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

}, OJST.VERSION, {
    requires: [
        OJST.modules.widgets.AbstractWidget,
        OJST.modules.widgets.Alerts,
        'widget', 'widget-child', 'widget-parent'
    ]});