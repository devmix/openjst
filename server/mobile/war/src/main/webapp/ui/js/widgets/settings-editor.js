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
YUI.add(OJST.ns.widgets.SettingsEditor, function (Y) {
    "use strict";

    /**
     * @class SettingsEditorSections
     * @namespace OJST.ui.widgets
     * @constructor
     * @extends OJST.ui.widgets.AbstractWidget
     */
    OJST.ui.widgets.SettingsEditorSections = Y.Base.create('widgets-settings-editor-sections', OJST.ui.widgets.AbstractWidget, [Y.WidgetParent], {

        BOUNDING_TEMPLATE: '<div></div>',
        CONTENT_TEMPLATE: null,

        /** @override */
        initializer: function () {
            /**
             * @type {OJST.ui.utils.DependencyResolver}
             * @private
             */
            this._dependencies = new OJST.ui.utils.DependencyResolver();
            /**
             * @type {{module: OJST.ui.widgets.form.Field}}
             * @private
             */
            this._fieldOfSetting = {};
        },

        /** @override */
        syncSize: function () {
            OJST.ui.widgets.SettingsEditorSections.superclass.syncSize.apply(this, arguments);
            this.each(function (item, index) {
                if (item.syncSize) {
                    item.syncSize();
                }
            });
        },

        /**
         * @param {OJST.ui.models.SettingGroup} model
         * @public
         */
        renderModel: function (model) {
            this._dependencies.reset();
            this._fieldOfSetting = {};
            this.removeAll();
            this._renderGroup(model);
            this._updateFieldsStates();
        },

        /**
         * @returns {{name: string}}
         * @public
         */
        getValues: function () {
            var values = {};
            this._collectValues(this, values);
            return values;
        },

        /**
         * @param {OJST.ui.models.SettingGroup} group
         * @private
         */
        _renderGroup: function (group) {
            var cbx = this.get(OJST.STATIC.CBX),
                sections = group.get('sections'),
                result = [];

            if (sections && sections.length > 0) {
                Y.each(sections, function (section) {
                    result.push(this._renderSection(section, cbx));
                }, this);
            }

            this.add(result);

            this._fillFieldsMap(this);

            return result;
        },

        /**
         * @param {OJST.ui.models.SettingSection} section
         * @param {Y.Node} container
         * @private
         */
        _renderSection: function (section, container) {
            var settings = section.get('settings'),
                fields, fieldNames = [];

            if (settings && settings.length > 0) {
                fields = [];
                Y.each(settings, function (setting) {
                    var defaultValue = setting.get('defaultValue'),
                        config = {
                            name: setting.get('name'),
                            label: OJST.i18n.setting(setting.get('name')) + (defaultValue ? ' (' + defaultValue + ')' : ''),
                            value: setting.get('value'),
                            on: {
                                change: Y.bind(function (e) {
                                    this._onChangeSetting(e.target);
                                }, this)
                            }
                        };
                    switch (setting.get('type')) {
                        case 'INTEGER':
                            config.type = OJST.ui.widgets.form.TextField;
                            break;
                        case 'LONG':
                            config.type = OJST.ui.widgets.form.TextField;
                            break;
                        case 'FLOAT':
                            config.type = OJST.ui.widgets.form.TextField;
                            break;
                        case 'DOUBLE':
                            config.type = OJST.ui.widgets.form.TextField;
                            break;
                        case 'STRING':
                            config.type = OJST.ui.widgets.form.TextField;
                            break;
                        case 'DATE':
                            config.type = OJST.ui.widgets.form.TextField;
                            break;
                        case 'BOOLEAN':
                            config.type = OJST.ui.widgets.form.CheckBox;
                            break;
                    }

                    fields.push(config);
                    fieldNames.push(setting.get('name'));

                    this._dependencies.add(setting.get('name'), setting.get('requires'));
                }, this);
            }

            this._dependencies.add(fieldNames, section.get('requires'));

            return new OJST.ui.widgets.form.FormFields({
                name: section.get('name'),
                title: OJST.i18n.label(section.get('name')),
                children: fields
            });
        },

        /**
         * @param {Y.Widget} parent
         * @private
         */
        _fillFieldsMap: function (parent) {
            parent.each(function (child) {
                var name = child.get('name');
                if (child instanceof OJST.ui.widgets.form.FormFields) {
                    this._fillFieldsMap(child);
                } else {
                    this._fieldOfSetting[name] = child;
                }
            }, this);
        },

        /**
         * @param {Y.Widget} parent
         * @param {{name: string}} values
         * @private
         */
        _collectValues: function (parent, values) {
            parent.each(function (child) {
                var name = child.get('name');
                if (child instanceof OJST.ui.widgets.form.FormFields) {
                    this._collectValues(child, values);
                } else if (child.isEnabled()) {
                    values[name] = child.get('value');
                }
            }, this);
        },

        /**
         * @param {string} fieldName
         * @returns {boolean}
         * @private
         */
        _checkFieldState: function (fieldName) {
            var field = this._fieldOfSetting[fieldName];
            return field ? !field.isBlank() : false;
        },

        /**
         * @param {OJST.ui.widgets.form.Field} field
         * @private
         */
        _onChangeSetting: function (field) {
            var widget, module,
                modules = this._dependencies.resolveDependenciesStates(field.get('name'), this._checkFieldState, this);

            for (module in modules) {
                if (modules.hasOwnProperty(module)) {
                    widget = this._fieldOfSetting[module];
                    if (widget) {
                        if (modules[module].available) {
                            if (!widget.isEnabled()) {
                                widget.enable();
                            }
                        } else {
                            if (widget.isEnabled()) {
                                widget.disable();
                            }
                        }
                    }
                }
            }
        },

        /**
         * @private
         */
        _updateFieldsStates: function () {
            var widget, module,
                modules = this._dependencies.resolveModulesStates(this._checkFieldState, this);

            for (module in modules) {
                if (modules.hasOwnProperty(module)) {
                    widget = this._fieldOfSetting[module];
                    if (widget) {
                        widget[modules[module].available ? 'enable' : 'disable']();
                    }
                }
            }
        }

    }, {
        ATTRS: {
        }
    });

    /**
     * @class SettingsEditor
     * @namespace OJST.ui.widgets
     * @constructor
     * @extends OJST.ui.widgets.AbstractWidget
     */
    OJST.ui.widgets.SettingsEditor = Y.Base.create('widgets-settings-editor', OJST.ui.widgets.AbstractWidget, [Y.WidgetParent], {

        BOUNDING_TEMPLATE: '<div ></div>',
        CONTENT_TEMPLATE: null,

        /** @override */
        initializer: function () {
            /**
             * @type {OJST.ui.widgets.SettingsEditorSections}
             * @private
             */
            this._sections = undefined;

            this.set('layout', 'border');
        },

        /** @override */
        renderUI: function () {
            var bbx = this.get(OJST.STATIC.BBX),
                layout = this.get('layout');

            this._sections = new OJST.ui.widgets.SettingsEditorSections({ padding: 8 });

            layout.add({region: 'center', element: this._sections});
            layout.add({region: 'bottom', element: new OJST.ui.widgets.toolbar.Controls({
                controls: [
                    {
                        label: OJST.i18n.label('reload'),
                        align: 'right',
                        handler: this.syncUI,
                        scope: this
                    },
                    {
                        label: OJST.i18n.label('save'),
                        align: 'right',
                        primary: true,
                        handler: this._onSave,
                        scope: this
                    }
                ]
            })});

            layout.render(bbx);
        },

        /** @override */
        bindUI: function () {
            var model = this.get('model');
            if (model) {
                this.subscribe(model.after('load', function () {
                    this._sections.renderModel(model);
                    this.unmask();
                }, this));
            }
        },

        /** @override */
        syncUI: function () {
            var model = this.get('model');
            if (model) {
                if (this.get('autoLoad')) {
                    this.mask();
                    model.load();
                }
            }
        },

        /**
         * @private
         */
        _onSave: function () {
            var model = this.get('model'),
                values = this._sections.getValues();

            if (Y.Lang.isValue(values)) {
                Y.io(model.getURL(), {
                    method: 'PUT',
                    data: values,
                    on: {
                        start: this.mask,
                        failure: function () {
                            this.showNotification(OJST.i18n.msg('errorSaving'), 'danger');
                        },
                        success: function () {
                            this.showNotification(OJST.i18n.msg('successSaving'), 'success');
                        },
                        end: this.unmask
                    },
                    context: this
                });
            }
        }

    }, {
        ATTRS: {
            autoLoad: {
                validator: Y.Lang.isBoolean,
                value: true,
                writeOnce: 'initOnly'
            },
            model: {
                validator: function (v) {
                    return v instanceof OJST.ui.models.SettingGroup;
                },
                writeOnce: 'initOnly'
            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.widgets.AbstractWidget,
    OJST.ns.widgets.form.FormFields,
    OJST.ns.widgets.form.CheckBox,
    OJST.ns.widgets.form.TextField,
    OJST.ns.widgets.toolbar.Controls,
    OJST.ns.utils.DependencyResolver,
    'widget-parent'
]});