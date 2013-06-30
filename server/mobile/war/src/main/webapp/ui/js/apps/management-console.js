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
YUI.add(OJST.modules.apps.ManagementConsole, function (Y) {
    "use strict";

    OJST.ui.apps.ManagementConsole = Y.Base.create('appsManagementConsole', OJST.ui.apps.Abstract, [], {
        views: {
            ManagementConsole: {
                type: OJST.ui.views.PageManagementConsole
            },
            Accounts: {
                type: OJST.ui.views.PageAccounts
            },
            Account: {
                type: OJST.ui.views.PageAccount,
                parent: 'Accounts'
            },
            AccountUsers: {
                type: OJST.ui.views.PageAccountUsers,
                parent: 'Accounts'
            },
            AccountUser: {
                type: OJST.ui.views.PageAccountUser,
                parent: 'AccountUsers'
            }
        },
        transitions: {
            navigate: 'slideRight',
            toChild: 'slideLeft',
            toParent: 'slideRight'
        },

        /**
         * @param {Object} cfg
         * @protected
         */
        initializer: function (cfg) {
            this.set('brand', OJST.PROJECT_NAME);
            this.set('navigation', {
                buttons: [
                    {
                        label: OJST.i18n.label('home'),
                        route: '/'
                    },
                    {
                        label: OJST.i18n.label('accounts'),
                        route: '/accounts'
                    }
                ],
                menuLabel: OJST.app.session.user.name,
                menu: [
                    OJST.app.session.user.name,
                    {label: OJST.i18n.label('changePassword'), handler: function () {
                        console.log('TODO Change password...');
                    }},
                    {label: OJST.i18n.label('changeSettings'), handler: function () {
                        console.log('TODO Change settings...');
                    }},
                    '-',
                    {label: OJST.i18n.label('logout'), handler: function () {
                        Y.io('/mobile/rest/ui/session/logout', {
                            method: 'POST',
                            on: {
                                complete: function () {
                                    window.location.reload();
                                }
                            }
                        });
                    }}
                ]
            });
        },

        _changeTab: function (index) {
            this.set('tabIndex', index);
            this.get('navigationBar').item(0).activate(index + 1, true);
        },

        showManagementConsole: function (req) {
            this._changeTab(0);
            this.showView('ManagementConsole', req.params);
        },
        showAccounts: function (req) {
            this._changeTab(1);
            this.showView('Accounts', req.params);
        },
        showAccount: function (req) {
            this._changeTab(1);
            this.showView('Account', req.params);
        },
        showAccountUsers: function (req) {
            this._changeTab(1);
            this.showView('AccountUsers', req.params);
        },
        showAccountUser: function (req) {
            this._changeTab(1);
            this.showView('AccountUser', req.params);
        }

    }, {
        ATTRS: {
            routes: {
                value: [
                    {path: '/accounts', callbacks: 'showAccounts'},
                    {path: '/accounts/:accountId', callbacks: 'showAccount'},
                    {path: '/accounts/:accountId/users', callbacks: 'showAccountUsers'},
                    {path: '/accounts/:accountId/users/:userId', callbacks: 'showAccountUser'},
                    {path: '*', callbacks: 'showManagementConsole'}
                ]
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
    OJST.modules.apps.Abstract,
    OJST.modules.views.PageManagementConsole,
    OJST.modules.views.PageAccounts,
    OJST.modules.views.PageAccount,
    OJST.modules.views.PageAccountUsers,
    OJST.modules.views.PageAccountUser,
    'io-base'
]});