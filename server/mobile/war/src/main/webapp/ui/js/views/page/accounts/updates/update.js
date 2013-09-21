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
YUI.add(OJST.ns.views.page.accounts.updates.Update, function (Y) {
    "use strict";

    /**
     * @class Update
     * @namespace OJST.ui.views.page.accounts.updates
     * @constructor
     * @extends OJST.ui.views.page.account.Abstract
     */
    OJST.ui.views.page.accounts.updates.Update = Y.Base.create('viewsPageAccountsUpdatesUpdate', OJST.ui.views.page.accounts.Abstract, [], {

        /** @override */
        getTabsTitle: function () {
            return OJST.i18n.label('update');
        },

        /** @override */
        isNewModel: function () {
            return !OJST.ui.utils.Framework.isValue(this.get('updateId'));
        },

        /** @override */
        createForm: function () {
            var accountId = this.get('accountId'), updateId = this.get('updateId');
            return new OJST.ui.widgets.form.Form({
                model: new OJST.ui.models.Update({
                    id: this.isNewModel() ? null : Number(updateId),
                    accountId: Number(accountId)
                }),
                children: [
                    {
                        type: OJST.ui.widgets.form.fields.List,
                        name: 'os',
                        label: OJST.i18n.label('os'),
                        required: true,
                        data: OJST.enums.asFieldData("MobileClientOS"),
                        value: 'ANDROID'
                    },
                    {
                        type: OJST.ui.widgets.form.fields.Version,
                        name: 'version',
                        label: OJST.i18n.label('version'),
                        required: true,
                        format: [OJST.i18n.label('major'), OJST.i18n.label('minor'), OJST.i18n.label('build')]
                    },
                    {
                        type: OJST.ui.widgets.form.fields.TextArea,
                        name: 'description',
                        label: OJST.i18n.label('description'),
                        rows: 5
                    },
                    {
                        type: OJST.ui.widgets.form.fields.UploadFile,
                        name: 'uploadId',
                        label: OJST.i18n.label('file'),
                        required: true,
                        uploadUrl: 'ui-api/files/upload'
                    }
                ],
                on: {
                    'create': function (m) {
                        OJST.app.saveRoute('/accounts/' + accountId + '/updates/' + m.get('id'));
                    },
                    'close': function () {
                        OJST.app.saveRoute('/accounts/' + accountId + '/updates');
                    }
                }
            });
        }

    });

}, OJST.VERSION, {requires: [
    OJST.ns.utils.Framework,
    OJST.ns.views.page.accounts.Abstract,
    OJST.ns.models.Update,
    OJST.ns.widgets.form.Form,
    OJST.ns.widgets.form.fields.Text,
    OJST.ns.widgets.form.fields.Version,
    OJST.ns.widgets.form.fields.TextArea,
    OJST.ns.widgets.form.fields.UploadFile
]});