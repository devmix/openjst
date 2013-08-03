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
YUI.add(OJST.ns.views.page.dashboard.connections.Summary, function (Y) {
    "use strict";

    /**
     * @class Summary
     * @namespace OJST.ui.views.page.dashboard
     * @constructor
     * @extends OJST.ui.views.page.dashboard.Abstract
     */
    OJST.ui.views.page.dashboard.connections.Summary = Y.Base.create('viewsPageDashboardSummary', OJST.ui.views.page.dashboard.Abstract, [], {

        /** @override */
        createForm: function () {
            return Y.Node.create('<div class="page-header"><h1><small><center>TODO SUMMARY</center></small></h1></div>');
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.views.page.dashboard.Abstract
]});