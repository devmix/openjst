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
YUI.add(OJST.ns.views.page.accounts.List, function (Y) {
    "use strict";

    /**
     * @class List
     * @namespace OJST.ui.views.page.accounts
     * @constructor
     * @extends  OJST.ui.views.page.Abstract
     */
    OJST.ui.views.page.accounts.List = Y.Base.create('viewsPageAccountsList', OJST.ui.views.page.Abstract, [], {

        /** @override */
        createForm: function () {
            var list = new OJST.ui.models.AccountList({persistentId: 'pageAccounts'});
            return new OJST.ui.widgets.form.Grid({
                autoLoad: true,
                data: list,
                columns: [
                    { name: "authId", label: OJST.i18n.label('account-id'), width: 150},
                    { name: "name", label: OJST.i18n.label('name'), fill: true },
                    { name: "actions", label: OJST.i18n.label('actions'), html: true, width: 50,
                        render: function (v, m) {
                            return '<div style="text-align:center; white-space: nowrap;">'
                                + '<i class="glyphicon glyphicon-trash" modelId="' + m.get('id') + '"></i>'
                                + '</div>';
                        }
                    }
                ],
                buttons: [
                    {
//                        label: OJST.i18n.label('add'),
                        icon: 'plus',
                        handler: function () {
                            OJST.app.saveRoute('/accounts/');
                        },
                        scope: this
                    }
                ],
                on: {
                    'row-select': function (model) {
                        OJST.app.saveRoute('/accounts/' + model.get('id'));
                    }
                },
                delegates: {
                    'click:i.glyphicon-trash': function (e) {
                        e.preventDefault();
                        OJST.ui.widgets.Alerts.confirm(OJST.i18n.label('confirm'), OJST.i18n.msg('removeAccount'), function (button) {
                            if ('yes' === button) {
                                var record = this.getModel(e.target.getAttribute('modelId'));
                                if (record) {
                                    record.destroy({remove: true});
                                }
                            }
                        }, this);
                    }
                }
            });
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.views.page.Abstract,
    OJST.ns.widgets.form.Grid,
    OJST.ns.models.Account,
    OJST.ns.widgets.Alerts
]});