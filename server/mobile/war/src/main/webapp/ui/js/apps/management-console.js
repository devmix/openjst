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
/*jslint nomen:true, node:true, white:true, browser:true, plusplus:true, stupid: true*/
YUI.add(OJST.ns.apps.ManagementConsole, function (Y) {
    "use strict";

    /**
     * @class ManagementConsole
     * @namespace OJST.ui.apps
     * @constructor
     * @extends OJST.ui.apps.Abstract
     */
    OJST.ui.apps.ManagementConsole = Y.Base.create('appsManagementConsole', OJST.ui.apps.Abstract, [], {

        transitions: {
            navigate: 'slideRight',
            toChild: 'slideLeft',
            toParent: 'slideRight'
        }

    }, {
        ATTRS: {
            brand: {
                value: OJST.i18n.label('brand')
            },
            pages: {
                value: [
                    ['*', OJST.ui.views.page.ManagementConsole, OJST.i18n.label('home')],
                    ['/accounts', OJST.ui.views.page.accounts.List, OJST.i18n.label('accounts'), [
                        [':accountId', OJST.ui.views.page.accounts.Account],
                        [':accountId/users', OJST.ui.views.page.accounts.users.List, [
                            [':userId', OJST.ui.views.page.accounts.users.User]
                        ]],
                        [':accountId/summary', OJST.ui.views.page.accounts.Summary]
                    ]],
                    ['/dashboard', OJST.ui.views.page.dashboard.connections.Summary, OJST.i18n.label('dashboard'), [
                        ['connections', OJST.ui.views.page.dashboard.connections.Summary, [
                            ['accounts', OJST.ui.views.page.dashboard.connections.Accounts],
                            ['clients', OJST.ui.views.page.dashboard.connections.Clients]
                        ]]
                    ]],
                    ['/settings', OJST.ui.views.page.settings.Interface, OJST.i18n.label('settings'), [
                        ['interface', OJST.ui.views.page.settings.Interface],
                        ['protocols', OJST.ui.views.page.settings.Protocols]
                    ]]
                ]
            },
            menu: {
                value: {
                    menuLabel: OJST.session.getUser().getName(),
                    menu: [
                        OJST.session.getUser().getRole(),
                        {label: OJST.i18n.label('changePassword'), handler: function () {
                            console.log('TODO Change password...');
                        }},
                        {label: OJST.i18n.label('changeSettings'), handler: function () {
                            console.log('TODO Change settings...');
                        }},
                        '-',
                        {label: OJST.i18n.label('logout'), handler: function () {
                            Y.io('ui-api/session/logout', {
                                method: 'POST',
                                on: {
                                    complete: function () {
                                        window.location.reload();
                                    }
                                }
                            });
                        }}
                    ]
                }
            },
            tabView: {
                writeOnce: 'initOnly'
            },
            tabIndex: {
                value: 0,
                validator: Y.Lang.isNumber
            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.utils.Framework,
    OJST.ns.utils.Html,
    OJST.ns.apps.Abstract,
    OJST.ns.views.page.ManagementConsole,
    OJST.ns.views.page.accounts.List,
    OJST.ns.views.page.accounts.Account,
    OJST.ns.views.page.accounts.Summary,
    OJST.ns.views.page.accounts.users.List,
    OJST.ns.views.page.accounts.users.User,
    OJST.ns.views.page.dashboard.connections.Summary,
    OJST.ns.views.page.dashboard.connections.Accounts,
    OJST.ns.views.page.dashboard.connections.Clients,
    OJST.ns.views.page.settings.Interface,
    OJST.ns.views.page.settings.Protocols,
    'io-base'
]});