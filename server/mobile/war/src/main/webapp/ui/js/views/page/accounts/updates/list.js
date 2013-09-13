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
YUI.add(OJST.ns.views.page.accounts.updates.List, function (Y) {
    "use strict";

    var TPL = {
        ACTIONS: '<div style="text-align:center; white-space: nowrap;">'
            + '<i class="glyphicon glyphicon-trash" modelId="{id}"></i>'
            + '</div>'
    };

    /**
     * @class List
     * @namespace OJST.ui.views.page.accounts.updates
     * @constructor
     * @extends OJST.ui.views.page.accounts.Abstract
     */
    OJST.ui.views.page.accounts.updates.List = Y.Base.create('viewsPageAccountsUpdatesList', OJST.ui.views.page.accounts.Abstract, [], {

        /** @override */
        createForm: function () {
            var accountId = Number(this.get('accountId'));
            return new OJST.ui.widgets.form.Grid({
                autoLoad: true,
                data: new OJST.ui.models.UpdateList({accountId: accountId, persistentId: 'pageAccountsUpdates'}),
                columns: [
                    { name: "version", label: OJST.i18n.label('version'), width: 150, render: function (v, m) {
                        return v;
                    }},
                    { name: "uploadDate", label: OJST.i18n.label('upload-date'), format: '%Y-%m-%d %H:%M', width: 120 },
                    { name: "description", label: OJST.i18n.label('description'), fill: true },
                    { name: "actions", label: OJST.i18n.label('actions'), html: true, width: 55,
                        render: function (v, m) {
                            return Y.Lang.sub(TPL.ACTIONS, {id: m.get('id')});
                        }
                    }
                ],
                buttons: [
                    {
                        icon: 'plus',
                        handler: function () {
                            OJST.app.saveRoute('/accounts/' + accountId + '/updates/');
                        },
                        scope: this
                    }
                ],
                on: {
                    'row-select': function (model) {
                        OJST.app.saveRoute('/accounts/' + accountId + '/updates/' + model.get('id'));
                    }
                },
                delegates: {
                    'click:i.glyphicon-trash': function (e) {
                        OJST.ui.widgets.Alerts.confirm(OJST.i18n.label('confirm'), OJST.i18n.msg('removeUpdate'), function (button) {
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
    OJST.ns.views.page.accounts.Abstract,
    OJST.ns.widgets.form.Grid,
    OJST.ns.models.Update,
    OJST.ns.widgets.Alerts
]});