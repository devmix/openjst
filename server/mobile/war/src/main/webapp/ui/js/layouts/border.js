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
YUI.add(OJST.ns.layouts.Border, function (Y) {
    "use strict";

    var isArray = Y.Lang.isArray,
        each = Y.each;

    /**
     * @class Border
     * @namespace OJST.ui.layouts.Border
     * @constructor
     * @extends Y.Base
     */
    OJST.ui.layouts.Border = Y.Base.create('layoutsBorder', Y.Base, [], {

        /** @override */
        initializer: function () {
            /**
             * @type {boolean}
             * @private
             */
            this._sizeSynced = !this.get('hideBeforeSizeSynced');
            /**
             * @type {boolean}
             * @private
             */
            this._rendered = false;
        },

        /**
         * @param {{region: ('left'|'right'|'center'|'top'|'bottom'), element: (Y.Node|Y.Widget|Y.View)}[]} items
         * @return {OJST.ui.layouts.Border}
         * @public
         */
        add: function (items) {
            each(isArray(items) ? items : [items], function (item) {
                this.set(item.region, item.element);
            }, this);

            return this;
        },

        /**
         * @param {Y.Node} container
         * @return {Y.Node}
         * @public
         */
        render: function (container) {
            var //container = this.get('container'),
                left = this.get('left'),
                right = this.get('right'),
                center = this.get('center'),
                top = this.get('top'),
                bottom = this.get('bottom');

            if (!container.get('id')) {
                container.set('id', Y.guid());
            }

            this.set('container', container);

            // hide before size of container will be computed
            if (!this._sizeSynced) {
                container.setStyle('visibility', 'hidden');
            }

            // header

            if (top) {
                this._topNode = this._renderComponent(top, container);
                this._safeFloatSet(this._topNode, 'left');
                this._topNode.setStyle('clear', 'both');
            }

            // row between top and bottom

            this._middleNode = container;

            // left panel

            if (left) {
                this._leftNode = this._renderComponent(left, this._middleNode);
                this._safeFloatSet(this._leftNode, 'left');
                this._leftNode.setStyle('clear', 'left');
            }

            // main container

            if (center) {
                this._centerNode = this._renderComponent(center, this._middleNode);
            } else {
                this._centerNode = this._renderComponent(Y.Node.create('<div/>'), this._middleNode);
            }
            this._safeFloatSet(this._centerNode, 'left');
            this._centerNode.setStyles({
//                display: 'table-cell',
                display: 'inline',
                overflowX: 'hidden',
                overflowY: 'auto'
            });

            // right panel

            if (right) {
                this._rightNode = this._renderComponent(right, this._middleNode);
                this._safeFloatSet(this._rightNode, 'left');
                this._rightNode.setStyle('clear', 'right');
            }

            // footer

            if (bottom) {
                this._bottomNode = this._renderComponent(bottom, container);
                this._safeFloatSet(this._bottomNode, 'left');
                this._bottomNode.setStyle('clear', 'both');
            }

            this._rendered = true;

            return container;
        },

        /**
         * @param {Y.Node} el
         * @return {Number|number}
         * @private
         */
        _borderX: function (el) {
            return (parseInt(el.getComputedStyle('borderLeftWidth'), 10) || 0)
                + (parseInt(el.getComputedStyle('borderRightWidth'), 10) || 0);
        },

        /**
         * @param {Y.Node} el
         * @return {Number|number}
         * @private
         */
        _borderY: function (el) {
            return (parseInt(el.getComputedStyle('borderTopWidth'), 10) || 0)
                + (parseInt(el.getComputedStyle('borderBottomWidth'), 10) || 0);
        },

        /**
         * @param {Y.Node} el
         * @return {Number|number}
         * @private
         */
        _marginX: function (el) {
            return (parseInt(el.getStyle('marginLeft'), 10) || 0)
                + (parseInt(el.getComputedStyle('marginRight'), 10) || 0);
        },

        /**
         * @param {Y.Node} el
         * @return {Number|number}
         * @private
         */
        _marginY: function (el) {
            return (parseInt(el.getComputedStyle('marginTop'), 10) || 0)
                + (parseInt(el.getComputedStyle('marginBottom'), 10) || 0);
        },

        /**
         * @param {Y.Node} el
         * @return {Number|number}
         * @private
         */
        _paddingX: function (el) {
            return (parseInt(el.getStyle('paddingLeft'), 10) || 0)
                + (parseInt(el.getComputedStyle('paddingRight'), 10) || 0);
        },

        /**
         * @param {Y.Node} el
         * @return {Number|number}
         * @private
         */
        _paddingY: function (el) {
            return (parseInt(el.getStyle('paddingTop'), 10) || 0)
                + (parseInt(el.getComputedStyle('paddingBottom'), 10) || 0);
        },

        /**
         * @private
         */
        _syncSize: function () {
            var container = this.get('container'),
                containerHeight = container.get('offsetHeight') - this._borderY(container) - this._paddingY(container),
                containerWidth = container.get('offsetWidth') - this._borderX(container) - this._paddingX(container),
                rightWidth, leftWidth, topHeight, bottomHeight, centerHeight, centerWidth;

            if (this._bottomNode) {
                this._bottomNode.setStyle('width', (containerWidth - this._marginX(this._bottomNode) - this._borderX(this._bottomNode)) + 'px');
            }
            if (this._topNode) {
                this._topNode.setStyle('width', (containerWidth - this._marginX(this._topNode) - this._borderX(this._topNode) ) + 'px');
            }
            if (this._leftNode) {
                this._leftNode.setStyle('height', (containerHeight - this._marginY(this._leftNode) - this._borderY(this._leftNode)) + 'px');
            }
            if (this._rightNode) {
                this._rightNode.setStyle('height', (containerHeight - this._marginY(this._rightNode) - this._borderY(this._topNode)) + 'px');
            }

            rightWidth = this._rightNode ? this._rightNode.get('offsetWidth') + this._marginX(this._rightNode) : 0;
            leftWidth = this._leftNode ? this._leftNode.get('offsetWidth') + this._marginX(this._leftNode) : 0;

            topHeight = (this._topNode ? this._topNode.get('offsetHeight')
                + this._marginY(this._topNode) + this._borderY(this._topNode) : 0);

            bottomHeight = this._bottomNode ? this._bottomNode.get('offsetHeight')
                + this._marginY(this._bottomNode) + this._borderY(this._bottomNode) : 0;

            centerHeight = containerHeight - topHeight - bottomHeight
                - this._marginY(this._centerNode) - this._borderY(this._centerNode);

            centerWidth = containerWidth - leftWidth - rightWidth
                - this._marginX(this._centerNode) - this._borderX(this._centerNode);

            this._centerNode.setStyle('width', centerWidth + 'px');
            this._centerNode.setStyle('height', centerHeight + 'px');

            this._syncElementSize([this.get('top'), this.get('left'), this.get('right'), this.get('center'), this.get('bottom')]);
        },

        /**
         * @param {Y.Node[]} elements
         * @private
         */
        _syncElementSize: function (elements) {
            each(elements, function (element) {
                if (element && element.syncSize) {
                    element.syncSize();
                }
            }, this);
        },

        /**
         *
         * @param {Y.Node|Y.Widget|Y.View} component
         * @param {Y.Node} container
         * @return {Y.Node}
         * @private
         */
        _renderComponent: function (component, container) {
            var rendered = false,
                node = null;

            if (component instanceof Y.Widget) {
                if (!component.get('rendered')) {
                    component.render(container);
                    rendered = true;
                }
                node = component.get('boundingBox');
            } else if (component instanceof Y.View) {
                node = component.render().get('container');
            } else {
                node = component;
            }

            if (!rendered) {
                container.append(node);
            }

            return node;
        },

        /**
         * @param {Y.Node} node
         * @param {'left'|'right'} value
         * @private
         */
        _safeFloatSet: function (node, value) {
            try {
                node.setStyle('float', value);
            } catch (ignore) {
            }
            node.setStyle('position', 'relative');
        },

        /**
         * @public
         */
        syncSize: function () {
            if (!this._rendered) {
                return;
            }

            this._syncSize();

            if (!this._sizeSynced) {
                var container = this.get('container');
                container.setStyle('visibility', 'visible');
                this._sizeSynced = true;
            }
        },

        /**
         * @public
         */
        clear: function () {
            var container = this.get('container');
            if (container) {
                container.empty();
            }
        },

        /**
         * @return {Y.Node}
         * @public
         */
        getCenterNode: function () {
            return this._centerNode;
        },

        /**
         * @return {Y.Node}
         * @public
         */
        getTopNode: function () {
            return this._topNode;
        }

    }, {
        ATTRS: {
            id: {

            },
            center: {

            },
            top: {

            },
            bottom: {

            },
            left: {

            },
            right: {

            },
            hideBeforeSizeSynced: {
                value: false,
                validator: Y.Lang.isBoolean
            }
        }
    });

}, OJST.VERSION, {requires: [
    'view', 'widget', 'node'
]});