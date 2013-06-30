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

/*global YUI, OJST*/
/*jslint nomen:true, node:true, white:true, browser:true, plusplus:true*/
YUI.add(OJST.modules.views.PageManagementConsole, function (Y) {
    "use strict";

    /**
     * @class PageManagementConsole
     * @namespace OJST.ui.views
     * @constructor
     * @extends OJST.ui.views.PageAbstract
     */
    OJST.ui.views.PageManagementConsole = Y.Base.create('viewsPageManagementConsole', OJST.ui.views.PageAbstract, [], {

        /** @override */
        createForm: function () {
            return Y.Node.create(
                '<div class="container-fluid"><div class="row-fluid">' +
                    '<section class="span12">' +
                    '   <div class="page-header">' +
                    '       <h1>Management Console</h1>' +
                    '   </div>' +
                    '   <p>TODO</p>' +
                    '</section>' +
                    '</div></div>'
            );
        }

    });

}, OJST.VERSION, {
    requires: [
        OJST.modules.views.PageAbstract
    ]});