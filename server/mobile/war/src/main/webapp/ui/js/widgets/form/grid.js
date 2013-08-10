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
YUI.add(OJST.ns.widgets.form.Grid, function (Y) {
    "use strict";

    var STR = {
            page: OJST.i18n.label('page'),
            size: OJST.i18n.label('size'),
            refresh: OJST.i18n.label('refresh')
        },
        TPL = {
            HEADER: '<table class="header"><thead><tr></tr></thead></table>',
            HEADER_COLUMNS: '{columns}',
            HEADER_COLUMN_CELL: '<th style="width:{width}">{label}</th>',
            HEADER_COLUMN_SCROLL_BAR_SPACE: '<th style="width:{width}; margin:0; padding:0;"></th>',

            BODY: '<div><table class="table-hover body"><tbody/></table></div>',
            BODY_ROW: '<tr modelId="{modelId}">{cells}</tr>',
            BODY_ROW_CELL: '<td>{content}</td>',

            FOOTER: '<div class="footer"></div>',

            PAGING_SIZE_TITLE: '<li><span><b>{title}</b></span></li>',
            PAGING_SIZE_ELEMENT: '<li {clazz}><a tabindex="-1" href="javascript:void(0)" pageSize="{size}">{size}</a></li>',
            PAGING_SIZE: '<span class="pagination pagination-small pagination-right" style="float:right">&nbsp;<ul>{sizes}</ul></span>',

            PAGING_INDEX_TITLE: '<li><span><b>{title}</b></span></li>',
            PAGING_INDEX_ELEMENT: '<li {clazz}><a href="javascript:void(0)" pageIndex="{index}">{index}</a></li>',
            PAGING_INDEX_DIVIDER: '<li class="disabled"><a href="javascript:void(0)" pageIndex="-2">...</a></li>',
            PAGING_INDEX: '<span class="pagination pagination-small pagination-right" style="float:right"><ul>{indexes}</ul></span>',

            BUTTONS: '<span class="btn-toolbar"><span class="btn-group dropup">{buttons}</span></span>',
            BUTTON: '<button class="btn btn-small" type="button" index="{index}">{icon}{label}</button>',
            BUTTON_ICON: '<i class="icon-{name}"></i> '
        },
        EMPTY_DATA = {
            startIndex: 0,
            total: 0,
            sortBy: null,
            sortOrder: 0,
            pageSize: 15
        },
        sub = Y.Lang.sub,
        escape = Y.Escape.html;

    /**
     * @class GridColumn
     * @namespace OJST.ui.widgets.form
     * @constructor
     * @extends Y.Base
     */
    OJST.ui.widgets.form.GridColumn = Y.Base.create('widgetsFormGridColumn', Y.Base, [], {

        /**
         * @return {string}
         * @public
         */
        getTableCellFixedWidth: function () {
            var width = this.get('width');
            return width && width > 1 ? width + 'px' : 'auto';
        },
        /**
         * @return {boolean}
         */
        hasRender: function () {
            return !!this.get('render');
        },
        /**
         * @return {boolean}
         */
        hasFormat: function () {
            return !!this.get('format');
        },
        /**
         * @return {boolean}
         */
        allowHtml: function () {
            return !!this.get('html');
        },
        /**
         * @param {*} v
         * @return {string}
         */
        format: function (v) {
            var format = this.get('format');
            if (format) {
                if (Y.Lang.isDate(v)) {
                    return Y.Date.format(v, {format: format});
                }
            }
            return v;
        },
        /**
         * @param {*} v
         * @param {Y.Model} m
         * @return {*}
         */
        render: function (v, m) {
            var render = this.get('render');
            return render ? render(v, m) : v;
        }

    }, {
        ATTRS: {
            grid: {
                writeOnce: 'initOnly',
                validator: function (v) {
                    return v instanceof OJST.ui.widgets.form.Grid;
                }
            },
            index: {
                validator: Y.Lang.isNumber
            },
            name: {
                validator: Y.Lang.isString
            },
            label: {
                validator: Y.Lang.isString
            },
            fill: {
                validator: Y.Lang.isBoolean
            },
            html: {
                validator: Y.Lang.isBoolean
            },
            width: {
                validator: Y.Lang.isNumber
            },
            render: {
                validator: Y.Lang.isFunction
            },
            format: {
                validator: Y.Lang.isString
            }
        }
    });

    /**
     * @class Grid
     * @namespace OJST.ui.widgets.form
     * @constructor
     * @extends OJST.ui.widgets.AbstractWidget
     * @uses Y.WidgetChild
     */
    OJST.ui.widgets.form.Grid = Y.Base.create('widgetsFormGrid', OJST.ui.widgets.AbstractWidget, [Y.WidgetChild], {

        BOUNDING_TEMPLATE: '<div></div>',
        CONTENT_TEMPLATE: null,

        /** @override */
        initializer: function () {
            /**
             * @type {boolean}
             * @private
             */
            this._sizeSynced = false;
            /**
             * @type {{node: Node, model: Model}[]}
             * @private
             */
            this._rows = [];
            /**
             * @type {OJST.ui.widgets.form.GridColumn[]}
             * @private
             */
            this._columns = [];
            /**
             * @type {Node}
             * @private
             */
            this._rowsNode = undefined;
            /**
             * @type {Node}
             * @private
             */
            this._tableNode = undefined;
            /**
             * @type {Node}
             * @private
             */
            this._bodyNode = undefined;
            /**
             * @type {Node}
             * @private
             */
            this._columnsNode = undefined;
            /**
             * @type {Node}
             * @private
             */
            this._pageSizesNode = undefined;
            /**
             * @type {Node}
             * @private
             */
            this._pageIndexesNode = undefined;
            /**
             * @type {{label: string, icon: string, handler: function. scope: object}[]}
             * @private
             */
            this._buttons = undefined;

            this.set('layout', 'border');
            this._initializeColumns();
        },

        /** @override */
        destructor: function () {
            delete this._columns;
            delete this._rows;
            delete this._tableNode;
            delete this._rowsNode;
            delete this._bodyNode;
        },

        /** @override */
        renderUI: function () {
            var cbx = this.get(OJST.STATIC.CBX),
                layout = this.get('layout');

            this._bodyNode = Y.Node.create(TPL.BODY);
            this._tableNode = this._bodyNode.one('>table');
            this._rowsNode = this._tableNode.one('>tbody');

            layout.add([
                {region: 'top', element: this._createHeader()},
                {region: 'center', element: this._bodyNode},
                {region: 'bottom', element: this._createFooter()}
            ]);

            layout.render(cbx);

            this._renderRows();
        },

        /** @override */
        syncSize: function () {
            OJST.ui.widgets.form.Grid.superclass.syncSize.apply(this, arguments);
            this._syncColumnsSize();
            this._sizeSynced = true;
        },

        /**
         * @param {string} id
         * @return {Model}
         * @public
         */
        getModel: function (id) {
            var model = null;
            this.get('data').some(function (m) {
                if (String(m.get('id')) === id) {
                    model = m;
                    return true;
                }
                return false;
            });
            return model;
        },

        /** @override */
        bindUI: function () {
            var data = this.get('data'),
                delegates = this.get('delegates');

            if (delegates) {
                Y.each(delegates, function (fn, name) {
                    var splitIdx = name.indexOf(':'),
                        eventName = splitIdx !== -1 ? name.substr(0, splitIdx) : name,
                        eventSelector = splitIdx !== -1 ? name.substr(splitIdx + 1) : null;
                    this.subscribe(this._rowsNode.delegate(eventName, fn, eventSelector, this));
                }, this);
            }

            if (!data) {
                return;
            }

            this.subscribe(data.after('*:change', function (e) {
                var model = e.target, row = data.indexOf(model);
                if (row && row.node && row.model) {
                    this._updateRow(row.node, row.model);
                }
            }, this));

            this.subscribe(data.after('reset', function () {
                this._renderRows();
                this._updateFooter();
            }, this));

            this.subscribe(data.after('remove', function (e) {
                this._removeRow(e.index);
            }, this));

            this.subscribe(data.after('add', function (e) {
                this._addRow(e.model, e.index);
                if (this._sizeSynced) {
                    this._syncColumnsSize();
                }
            }, this));

            this.subscribe(data.after('load', function () {
                this._renderRows();
                this._updateFooter();
                this.unmask();
            }, this));

            this.subscribe(data.after('destroy', function () {
                this._removeRows();
            }, this));

            this.subscribe(this._pageSizesNode.delegate('click', function (e) {
                var newSize = parseInt(e.target.getAttribute('pageSize'), 10),
                    oldSize = data.get('pageSize');
                if (newSize > 0 && newSize !== oldSize) {
                    data.set('pageSize', newSize);
                    data.load();
                }
                e.preventDefault();
            }, 'a', this));

            this.subscribe(this._pageIndexesNode.delegate('click', function (e) {
                var newIndex = (parseInt(e.target.getAttribute('pageIndex'), 10) - 1) * data.get('pageSize'),
                    oldIndex = data.get('startIndex');
                if (newIndex >= 0 && newIndex !== oldIndex) {
                    data.set('startIndex', newIndex);
                    data.load();
                }
                e.preventDefault();
            }, 'a', this));

            this.subscribe(this._rowsNode.delegate('click', this._onCellClick, 'td', this));

            if (this._buttonsNode) {
                this.subscribe(this._buttonsNode.delegate('click', function (e) {
                    //noinspection JSPotentiallyInvalidUsageOfThis
                    var index = parseInt(e.target.getAttribute('index'), 10),
                        button;

                    if (this._buttons && index !== -1 && index < this._buttons.length) {
                        button = this._buttons[index];
                        if (button.handler) {
                            e.preventDefault();
                            button.handler.call(button.scope || this);
                        }
                    }
                }, 'button', this));
            }

            if (this.get('autoLoad')) {
                this.mask();
                data.load();
            }
        },

        /**
         * @param {EventFacade} e
         * @private
         */
        _onCellClick: function (e) {
            var tag = e.target.get('tagName'), model, modelId;
            if ('TD' === tag) {
                modelId = e.target.get('parentNode').getAttribute('modelId');
                if (Y.Lang.isValue(modelId)) {
                    model = this.getModel(modelId);
                    if (model) {
                        e.preventDefault();
                        this.fire('row-select', model);
                    }
                }
            }
        },

        /**
         * @private
         */
        _initializeColumns: function () {
            var columns = this.get('columns'),
                index = 0;

            if (!columns || columns.length === 0) {
                return;
            }

            Y.each(columns, function (column) {
                this._columns.push(new OJST.ui.widgets.form.GridColumn({
                    grid: this,
                    index: index++,
                    name: column.name,
                    label: column.labelHtml ? column.label : escape(column.label),
                    fill: column.fill,
                    html: column.html,
                    width: parseFloat(column.width),
                    render: column.render,
                    format: column.format
                }));
            }, this);
        },

        /**
         * @return {Object}
         * @private
         */
        _getModelListMeta: function () {
            var data = this.get('data');

            if (!data) {
                return EMPTY_DATA;
            }

            return {
                startIndex: data.get('startIndex') || EMPTY_DATA.startIndex,
                total: data.get('total') || EMPTY_DATA.total,
                sortBy: data.get('sortBy') || EMPTY_DATA.sortBy,
                sortOrder: data.get('sortOrder') || EMPTY_DATA.sortOrder,
                pageSize: data.get('pageSize') || EMPTY_DATA.pageSize
            };
        },

        /**
         * @return {Node}
         * @private
         */
        _createHeader: function () {
            var table = Y.Node.create(TPL.HEADER),
                columnsCell = [];

            Y.each(this._columns, function (column) {
                columnsCell.push(sub(TPL.HEADER_COLUMN_CELL, {
                    label: column.get('label'),
                    width: column.getTableCellFixedWidth()
                }));
            }, this);

            // scroll bar space
            columnsCell.push(sub(TPL.HEADER_COLUMN_SCROLL_BAR_SPACE, {
                width: OJST.ui.utils.Html.getScrollBarWidth() + 'px'
            }));

            this._columnsNode = table.one('tr');

            if (columnsCell.length > 0) {
                this._columnsNode.setHTML(columnsCell.join(''));
            }

            return table;
        },

        /**
         * @return {Node}
         * @private
         */
        _createFooter: function () {
            var container = Y.Node.create(TPL.FOOTER),
                data = this._getModelListMeta();

            this._pageSizesNode = this._renderPagingSizes(data);
            this._pageIndexesNode = this._renderPagingIndexes(data);
            this._buttonsNode = this._renderButtons();

            container.append(this._buttonsNode);
            container.append(this._pageSizesNode);
            container.append(this._pageIndexesNode);

            return container;
        },

        /**
         * @private
         */
        _syncColumnsSize: function () {
            if (this._rows.length === 0 || this._columns.length === 0) {
                return;
            }

            var columnCellsNode = this._columnsNode.all('th'),
                rowCellsNode = this._rows[0].node.all('td'),
                bodyNode = this.get('layout').getCenterNode(),
                scrollBarSpaceNode = columnCellsNode.item(columnCellsNode.size() - 1),
                size = rowCellsNode.size(),
                index = 0;

            this._tableNode.setStyle('width', this._bodyNode.getStyle('width'));

            if (bodyNode.get('scrollHeight') > bodyNode.get('clientHeight')) {
                scrollBarSpaceNode.setStyle('display', 'table-cell');
            } else {
                scrollBarSpaceNode.setStyle('display', 'none');
            }

            columnCellsNode.each(function (columnNode) {
                if (index < size) {
                    rowCellsNode.item(index++).setStyle('width',
                        (parseInt(columnNode.getComputedStyle('width'), 10) + (index === size ? 1 : 0)) + 'px');
                }
            }, this);
        },

        /**
         * @private
         */
        _updateFooter: function () {
            var data = this._getModelListMeta();
            this._pageSizesNode.setHTML(this._renderPagingSizes(data));
            this._pageIndexesNode.setHTML(this._renderPagingIndexes(data));
        },

        /**
         * @return {Y.Node}
         * @private
         */
        _renderControlsNode: function () {
            return Y.Node.create(TPL.PAGING_CONTROLS);
        },

        /**
         * @param {Object} data
         * @return {Node}
         * @private
         */
        _renderPagingSizes: function (data) {
            var pageSizes = this.get('pageSizes') || [15, 30, 60],
                pageSizeHtml = [];

            if (data.pageSize !== 15 && data.pageSize !== 30 && data.pageSize !== 60) {
                pageSizes.splice(0, 0, data.pageSize);
            }

            pageSizeHtml.push(sub(TPL.PAGING_SIZE_TITLE, { title: STR.size }));

            Y.each(pageSizes, function (size) {
                pageSizeHtml.push(sub(TPL.PAGING_SIZE_ELEMENT, {
                    clazz: data.pageSize === size ? ' class="active disabled"' : '',
                    size: size
                }));
            });

            return Y.Node.create(sub(TPL.PAGING_SIZE, { sizes: pageSizeHtml.join('') }));
        },

        /**
         * @param {Object} data
         * @return {Node}
         * @private
         */
        _renderPagingIndexes: function (data) {
            var pageIndexes = [],
                pageIndexMax = parseInt(data.total / data.pageSize, 10) + 1,
                pageIndexCurrent = Math.min(pageIndexMax - 1, parseInt(data.startIndex / data.pageSize, 10)),
                i;

            pageIndexes.push(sub(TPL.PAGING_INDEX_TITLE, { title: STR.page }));

            if (pageIndexMax <= 9) { // i.e. [ 1 .. 3 4 5 6 7 .. 9 ]
                for (i = 0; i < pageIndexMax; i++) {
                    pageIndexes.push(sub(TPL.PAGING_INDEX_ELEMENT, {
                        clazz: pageIndexCurrent === i ? ' class="active disabled"' : '',
                        index: i + 1
                    }));
                }
            } else {
                if (pageIndexCurrent > 2) {
                    pageIndexes.push(sub(TPL.PAGING_INDEX_ELEMENT, { index: 1 }));
                }

                if (pageIndexCurrent > 3) {
                    pageIndexes.push(TPL.PAGING_INDEX_DIVIDER);
                }

                for (i = Math.max(0, pageIndexCurrent - 2); i < pageIndexMax && i < pageIndexCurrent + 3; i++) {
                    pageIndexes.push(sub(TPL.PAGING_INDEX_ELEMENT, {
                        clazz: pageIndexCurrent === i ? ' class="active disabled"' : '',
                        index: i + 1
                    }));
                }

                if (pageIndexCurrent + 3 < pageIndexMax) {
                    pageIndexes.push(TPL.PAGING_INDEX_DIVIDER);
                    pageIndexes.push(sub(TPL.PAGING_INDEX_ELEMENT, { index: pageIndexMax }));
                }
            }

            return Y.Node.create(sub(TPL.PAGING_INDEX, { indexes: pageIndexes.join('') }));
        },

        /**
         * @return {Node}
         * @private
         */
        _renderButtons: function () {
            var buttons = this.get('buttons') || [],
                buttonsHtml = [],
                showButtonsText = this.get('showButtonsText');

            buttons.push({
                label: showButtonsText ? STR.refresh : '',
                icon: 'refresh',
                handler: function () {
                    var data = this.get('data');
                    if (data) {
                        this.mask();
                        data.load();
                    }
                },
                scope: this
            });

            if (buttons && buttons.length > 0) {
                Y.each(buttons, function (button) {
                    buttonsHtml.push(Y.Lang.sub(TPL.BUTTON, {
                        index: buttonsHtml.length,
                        label: showButtonsText ? button.label || '' : '',
                        icon: button.icon ? sub(TPL.BUTTON_ICON, {name: button.icon}) : ''
                    }));
                }, this);

                this._buttons = buttons;

                return Y.Node.create(sub(TPL.BUTTONS, {buttons: buttonsHtml.join('')}));
            }

            return undefined;
        },

        /**
         * @param {Model} model
         * @param {number} index
         * @private
         */
        _addRow: function (model, index) {
            var row = {
                node: this._renderRow(model),
                model: model
            };
            this._rows.splice(index, 0, row);
            this._rowsNode.insert(row.node, index);
        },

        /**
         * @param {number} index
         * @private
         */
        _removeRow: function (index) {
            var removed = this._rows.splice(index, 1);
            if (removed) {
                removed[0].node.remove(true);
            }
        },

        /**
         * @param {Model} model
         * @return {string}
         * @private
         */
        _renderRowCells: function (model) {
            var rendered = [];

            Y.each(this._columns, function (column) {
                var columnName = column.get('name'),
                    modelValue = model.get(columnName),
                    renderedValue = modelValue,
                    formatter;

                if (column.hasRender()) {
                    renderedValue = column.render(modelValue, model);
                } else if (column.hasFormat()) {
                    renderedValue = column.format(modelValue);
                }

                rendered.push(sub(TPL.BODY_ROW_CELL, {
                    content: column.allowHtml() ? renderedValue : escape(renderedValue)
                }));
            }, this);

            return rendered.join('');
        },

        /**
         * @param {Model} model
         * @return {Node}
         * @private
         */
        _renderRow: function (model) {
            return Y.Node.create(sub(TPL.BODY_ROW, {
                modelId: model.get('id'),
                cells: this._renderRowCells(model)
            }));
        },

        /**
         * @param {Node} node
         * @param {Model} model
         * @private
         */
        _updateRow: function (node, model) {
            node.setHTML(this._renderRowCells(model));
        },

        /**
         * @private
         */
        _renderRows: function () {
            var data = this.get('data');
            this._removeRows();
            if (data) {
                data.each(function (model) {
                    var row = this._renderRow(model);
                    this._rows.push({
                        node: row,
                        model: model
                    });
                    this._rowsNode.append(row);
                }, this);
            }

            if (this._sizeSynced) {
                this._syncColumnsSize();
            }
        },

        /**
         * @private
         */
        _removeRows: function () {
            Y.each(this._rows, function (row) {
                if (row.node) {
                    row.node.remove(true);
                }
            });
            this._rows = [];
        }

    }, {
        ATTRS: {
            autoLoad: {
                validator: Y.Lang.isBoolean
            },
            columns: {
                validator: Y.Lang.isArray,
                writeOnce: 'initOnly'
            },
            buttons: {
                validator: Y.Lang.isArray,
                writeOnce: 'initOnly'
            },
            delegates: {
                validator: Y.Lang.isObject,
                writeOnce: 'initOnly'
            },
            data: {
                validator: Y.Lang.isObject,
                writeOnce: 'initOnly'
            },
            sortable: {
                validator: Y.Lang.isBoolean
            },
            paging: {
                validator: Y.Lang.isBoolean
            },
            pageSize: {
                validator: Y.Lang.isNumber
            },
            pageSizes: {
                validator: Y.Lang.isArray
            },
            showButtonsText: {
                validator: Y.Lang.isBoolean,
                value: false
            }
        }
    });


}, OJST.VERSION, {requires: [
    OJST.ns.widgets.AbstractWidget,
    'escape', 'widget', 'widget-child', 'datatype'
]});