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
YUI.add(OJST.modules.utils.Framework, function (Y) {
    "use strict";

    var Lang = Y.Lang;

    /**
     * @class Framework
     * @namespace OJST.ui.utils
     * @static
     */
    OJST.ui.utils.Framework = {
        /**
         * @param {Y.EventHandle[]|Y.EventHandle} handlers
         * @static
         */
        detach: function (handlers) {
            Y.each(!Lang.isArray(handlers) ? [handlers] : handlers, function (h) {
                h.detach();
            });
        },

        /**
         * @param {*} v
         * @return {boolean}
         */
        isValue: function (v) {
            return Y.Lang.isValue(v) && String(v).trim() !== '';
        },

        /**
         * @param {Y.Node} node
         * @param {string} text
         * @param {top | bottom | left | right} placement
         */
        setToolTip: function (node, text, placement) {
            node.setAttribute('data-toggle', 'tooltip');
            node.setAttribute('title', text);
            $(node.getDOMNode()).tooltip(Y.merge({}, {
                delay: { show: 500, hide: 100 },
                title: text,
                placement: placement
            }));
        },

        /**
         * @param {Y,Node} node
         */
        removeToolTip: function (node) {
            if (node) {
                node.removeAttribute('data-toggle');
                node.removeAttribute('title');
                $(node.getDOMNode()).tooltip('destroy');
            }
        }
    };

}, OJST.VERSION, {requires: []});