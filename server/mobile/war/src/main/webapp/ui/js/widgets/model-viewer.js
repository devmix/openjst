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
YUI.add(OJST.ns.widgets.ModelViewer, function (Y) {
    "use strict";

    var SECTIONS = {
            HTML: 'html',
            PANELS: 'panels'
        },
        TPL = {
            PANELS_TABLE: '<table width="100%" class="panels">',
            PANELS_PANEL: '<td width="{size}%">{content}</td>',
            PANELS_PANEL_HEADER: '<div>{header}</div>',
            PANELS_PANEL_ROW: '{label}: <span style="float: right"><%=this.{field}%></span><br>'
        };

    /**
     * @class ModelViewer
     * @namespace OJST.ui.widgets
     * @constructor
     * @extends OJST.ui.widgets.AbstractView
     */
    OJST.ui.widgets.ModelViewer = Y.Base.create('widgetsModelViewer', OJST.ui.widgets.AbstractView, [], {

        /** @override */
        initializer: function (cfg) {
            /**
             * @type {Y.Template}
             * @private
             */
            this._template = new Y.Template();
            /**
             * @type {function}
             * @private
             */
            this._compiled = undefined;
        },

        /** @override */
        render: function () {
            var model = this.get('model'),
                paddingSize = this.get('paddingSize') || 0,
                container = this.get(OJST.STATIC.CONTAINER);

            container.addClass(Y.ClassNameManager.getClassName(this.name.toLowerCase()));

            if (paddingSize > 0) {
                container.setStyle('padding', paddingSize + 'px');
            }

            if (!model) {
                return this;
            }

            if (this.get('autoLoad')) {
                this.mask();
                model.load(Y.bind(function () {
                    try {
                        this.refresh();
                    } finally {
                        this.unmask();
                    }
                }, this));
            } else {
                this.refresh();
            }

            return this;
        },

        /**
         * @public
         */
        refresh: function () {
            var model = this.get('model');
            if (model) {
                this.get(OJST.STATIC.CONTAINER).set('innerHTML', this._getCompiledTemplate()(model.toJSON()));
            }
        },

        /**
         * @return {function}
         * @private
         */
        _getCompiledTemplate: function () {
            if (!this._compiled) {
                this._compiled = this._template.compile(this._makeTemplate(this.get('template') || []));
            }
            return this._compiled;
        },

        /**
         * @private
         */
        _resetTemplate: function () {
            delete this._compiled;
        },

        /**
         * @param {({section: string, content: string}|{section: string, columns: number, content: *[]})[]} data
         * @returns {string}
         * @private
         */
        _makeTemplate: function (data) {
            var result = '', i, length, section;

            for (i = 0, length = data.length; i < length; i++) {
                section = data[i];
                switch (section.section) {
                    case SECTIONS.HTML:
                        result += section.content;
                        break;

                    case SECTIONS.PANELS:
                        result += this._makePanelsTemplate(section.content, section.columns || 1);
                        break;
                }
            }

            return result;
        },

        /**
         * @param {[]} panels
         * @param {number} columns
         * @returns {string}
         * @private
         */
        _makePanelsTemplate: function (panels, columns) {
            var i, length, panel,
                result = TPL.PANELS_TABLE,
                columnSize = Math.ceil(100 / columns);

            for (i = 0, length = panels.length; i < length; i++) {
                panel = panels[i];
                if (i % columns === 0) {
                    if (i !== 0) {
                        result += '</tr>';
                    }
                    result += '<tr>';
                }
                result += Y.Lang.sub(TPL.PANELS_PANEL, {
                    size: columnSize,
                    content: this._makePanelTemplate(panel[0], panel[1])
                });
            }

            return result + '</table>';
        },

        /**
         * @param {string} header
         * @param {*[]} rows
         * @returns {string}
         * @private
         */
        _makePanelTemplate: function (header, rows) {
            var i, length, row,
                result = Y.Lang.sub(TPL.PANELS_PANEL_HEADER, {header: header});

            for (i = 0, length = rows.length; i < length; i++) {
                row = rows[i];
                result += Y.Lang.sub(TPL.PANELS_PANEL_ROW, {label: row[0], field: row[1]});
            }

            return result;
        }

    }, {
        ATTRS: {
            template: {
                validator: Y.Lang.isArray,
                setter: function (v) {
                    this._resetTemplate();
                    return v;
                }
            },
            model: {
                validator: function (v) {
                    return v instanceof Y.Model;
                }
            },
            autoLoad: {
                validator: Y.Lang.isBoolean,
                value: true
            },
            paddingSize: {
                validator: Y.Lang.isNumber
            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.widgets.AbstractView,
    'model', 'classnamemanager', 'template'
]});