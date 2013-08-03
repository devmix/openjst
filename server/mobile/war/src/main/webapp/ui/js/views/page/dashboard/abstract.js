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
YUI.add(OJST.ns.views.page.dashboard.Abstract, function (Y) {
    "use strict";

    /**
     * @class Abstract
     * @namespace OJST.ui.views.page.dashboard
     * @constructor
     * @extends  OJST.ui.views.page.Abstract
     */
    OJST.ui.views.page.dashboard.Abstract = Y.Base.create('viewsPageDashboardAbstract', OJST.ui.views.page.Abstract, [], {

        /** @override */
        createTabs: function () {
            return {
                title: OJST.i18n.label('dashboard'),
                width: 150,
                items: [
                    {label: OJST.i18n.label('connections'), route: '/dashboard/connections',
                        active: this instanceof OJST.ui.views.page.dashboard.connections.Summary
                    },
                    {label: OJST.i18n.label('accounts'), route: '/dashboard/connections/accounts',
                        active: this instanceof OJST.ui.views.page.dashboard.connections.Accounts, level: 1
                    },
                    {label: OJST.i18n.label('clients'), route: '/dashboard/connections/clients',
                        active: this instanceof OJST.ui.views.page.dashboard.connections.Clients, level: 1
                    }
                ]
            };
        }

    });

}, OJST.VERSION, {requires: [
    OJST.ns.views.page.Abstract
]});