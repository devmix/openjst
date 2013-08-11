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
YUI.add(OJST.ns.views.page.accounts.Summary, function (Y) {
    "use strict";

    /**
     * @class Summary
     * @namespace OJST.ui.views.page.accounts
     * @constructor
     * @extends OJST.ui.views.page.accounts.Abstract
     */
    OJST.ui.views.page.accounts.Summary = Y.Base.create('viewsPageAccountsSummary', OJST.ui.views.page.accounts.Abstract, [], {

        /** @override */
        getFormTitle: function () {
            return null;
        },

        /** @override */
        isNewModel: function () {
            return !OJST.ui.utils.Framework.isValue(this.get('accountId'));
        },

        /** @override */
        createForm: function () {
            return new OJST.ui.widgets.ModelViewer({
                model: new OJST.ui.models.AccountSummary({id: parseInt(this.get('accountId'), 10)}),
                template: [
                    {section: 'panels', columns: 2, content: [
                        [OJST.i18n.label('brief'), [
                            [OJST.i18n.label('name'), 'name'],
                            [OJST.i18n.label('authId'), 'authId'],
                            [OJST.i18n.label('apiKey'), 'apiKey'],
                            [OJST.i18n.label('is-system-account'), 'system'],
                            [OJST.i18n.label('usersCount'), 'usersCount'],
                            [OJST.i18n.label('clientsCount'), 'clientsCount']
                        ]],
                        [OJST.i18n.label('connection'), [
                            [OJST.i18n.label('online'), 'online'],
                            [OJST.i18n.label('protocol'), 'lastProtocolType'],
                            [OJST.i18n.label('date'), 'lastOnlineTime'],
                            [OJST.i18n.label('remote-host'), 'lastRemoteHost']
                        ]],
                        [OJST.i18n.label('messages'), [
                            [OJST.i18n.label('to-client'), 'messagesToClient'],
                            [OJST.i18n.label('to-server'), 'messagesToServer'],
                            [OJST.i18n.label('from-client'), 'messagesFromClient'],
                            [OJST.i18n.label('from-server'), 'messagesFromServer']
                        ]],
                        [OJST.i18n.label('updates'), [
                            [OJST.i18n.label('updates-count'), 'updatesCount'],
                            [OJST.i18n.label('mobile-client-version'), 'androidVersion']
                        ]]
                    ]}
                ]
            });
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.utils.Framework,
    OJST.ns.models.AccountSummary,
    OJST.ns.views.page.accounts.Abstract,
    OJST.ns.widgets.ModelViewer
]});