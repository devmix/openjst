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
YUI.add(OJST.ns.utils.Html, function (Y) {
    "use strict";

    var scrollBarWidth;

    /**
     * @class Html
     * @namespace OJST.ui.utils
     * @static
     */
    OJST.ui.utils.Html = {
        /**
         * @param {Y.Node} node
         * @return {Number}
         * @static
         */
        getBorderWidths: function (node) {
            return (parseInt(node.getComputedStyle('borderLeftWidth'), 10) || 0)
                + (parseInt(node.getComputedStyle('borderRightWidth'), 10) || 0);
        },

        /**
         * @param {Y.Node} node
         * @return {Number}
         * @static
         */
        getMarginWidths: function (node) {
            return (parseInt(node.getComputedStyle('marginLeft'), 10) || 0)
                + (parseInt(node.getComputedStyle('marginRight'), 10) || 0);
        },

        /**
         * @param {Y.Node} node
         * @return {Number}
         * @static
         */
        getPaddingWidths: function (node) {
            return (parseInt(node.getComputedStyle('paddingLeft'), 10) || 0)
                + (parseInt(node.getComputedStyle('paddingRight'), 10) || 0);
        },

        /**
         * @return {Number}
         * @static
         */
        getScrollBarWidth: function () {
            if (scrollBarWidth) {
                return scrollBarWidth;
            }

            var inner, outer, w1, w2;

            inner = document.createElement('p');
            inner.style.width = "100%";
            inner.style.height = "200px";

            outer = document.createElement('div');
            outer.style.position = "absolute";
            outer.style.top = "0px";
            outer.style.left = "0px";
            outer.style.visibility = "hidden";
            outer.style.width = "200px";
            outer.style.height = "150px";
            outer.style.overflow = "hidden";
            outer.appendChild(inner);

            document.body.appendChild(outer);
            w1 = inner.offsetWidth;
            outer.style.overflow = 'scroll';
            w2 = inner.offsetWidth;
            if (w1 === w2) {
                w2 = outer.clientWidth;
            }

            document.body.removeChild(outer);

            scrollBarWidth = (w1 - w2);

            return scrollBarWidth;
        }
    };

}, OJST.VERSION, {});