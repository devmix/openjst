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
YUI.add(OJST.ns.views.page.dashboard.connections.Clients, function (Y) {
    "use strict";

    /**
     * @class Clients
     * @namespace OJST.ui.views.page.dashboard.connections
     * @constructor
     * @extends OJST.ui.views.page.dashboard.Abstract
     */
    OJST.ui.views.page.dashboard.connections.Clients = Y.Base.create('viewsPageDashboardConnectionsClients', OJST.ui.views.page.dashboard.Abstract, [], {

        /** @override */
        createForm: function () {
            var list = new OJST.ui.models.connection.ClientList({ persistentId: 'pageDashboardConnectionsClients' });
            return new OJST.ui.widgets.form.Grid({
                autoLoad: true,
                data: list,
                columns: [
                    {name: 'name', label: OJST.i18n.label('name'), fill: true, render: function (v, m) {
                        return v + ' (' + m.get('authId') + ')';
                    }},
                    {name: 'lastOnlineTime', label: OJST.i18n.label('lastOnlineTime'), width: 100, format: '%Y-%m-%d %H:%M'},
                    {name: 'lastProtocolType', label: OJST.i18n.label('lastProtocolType'), width: 100},
                    {name: 'lastRemoteHost', label: OJST.i18n.label('lastRemoteHost'), width: 100}
                ]
            });
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.models.connection.Client,
    OJST.ns.views.page.dashboard.Abstract
]});