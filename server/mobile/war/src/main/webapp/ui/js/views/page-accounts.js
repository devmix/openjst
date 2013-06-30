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
YUI.add(OJST.modules.views.PageAccounts, function (Y) {
    "use strict";

    /**
     * @class PageAccounts
     * @namespace OJST.ui.views
     * @constructor
     * @extends OJST.ui.views.PageAbstract
     */
    OJST.ui.views.PageAccounts = Y.Base.create('viewsPageAccounts', OJST.ui.views.PageAbstract, [], {

        /** @override */
        createForm: function () {
            var list = new OJST.ui.models.AccountList();
            return new OJST.ui.widgets.form.Grid({
                autoLoad: true,
                data: list,
                columns: [
                    { name: "authId", label: OJST.i18n.label('account-id'), width: 150},
                    { name: "name", label: OJST.i18n.label('name'), fill: true },
                    { name: "actions", label: OJST.i18n.label('actions'), html: true, width: 50,
                        render: function (v, m) {
                            return '<div style="text-align:center; white-space: nowrap;">'
                                + '<i class="icon-trash" modelId="' + m.get('id') + '"></i>'
                                + '</div>';
                        }
                    }
                ],
                buttons: [
                    {
                        label: OJST.i18n.label('add'),
                        handler: function () {
                            OJST.singleton.Application.save('/accounts/');
                        },
                        scope: this
                    }
                ],
                on: {
                    'row-select': function (model) {
                        OJST.singleton.Application.save('/accounts/' + model.get('id'));
                    }
                },
                delegates: {
                    'click:i.icon-trash': function (e) {
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

}, OJST.VERSION, {
    requires: [
        OJST.modules.views.PageAbstract,
        OJST.modules.widgets.form.Grid,
        OJST.modules.models.Account,
        OJST.modules.widgets.Alerts
    ]});