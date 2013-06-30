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
YUI.add(OJST.modules.views.PageAccount, function (Y) {
    "use strict";

    /**
     * @class PageAccount
     * @namespace OJST.ui.views
     * @constructor
     * @extends OJST.ui.views.PageAbstractAccount
     */
    OJST.ui.views.PageAccount = Y.Base.create('viewsPageAccount', OJST.ui.views.PageAbstractAccount, [], {

        /** @override */
        isNewModel: function () {
            var id = this.get('accountId');
            return !Y.Lang.isValue(id) || '' === id;
        },

        /** @override */
        createForm: function () {
            var id = this.get('accountId'),
                model = new OJST.ui.models.Account({id: this.isNewModel() ? null : Number(id)});

            return new OJST.ui.widgets.form.Form({
                horizontal: true,
                useDefaultButtons: true,
                model: model,
                auto: true,
                children: [
                    {
                        type: OJST.ui.widgets.form.TextField,
                        name: 'name',
                        label: OJST.i18n.label('name')
                    },
                    {
                        type: OJST.ui.widgets.form.TextField,
                        name: 'authId',
                        label: OJST.i18n.label('authId')
                    }
                ],
                on: {
                    'create': function (m) {
                        OJST.singleton.Application.save('/accounts/' + m.get('id'));
                    },
                    'close': function () {
                        OJST.singleton.Application.save('/accounts');
                    },
                    'error': function (e) {
                        if (e.status === OJST.ui.models.STATUS.NOT_UNIQUE) {
                            var fields = [];
                            Y.each(e.errorData, function (field) {
                                fields.push(OJST.i18n.label(field));
                            });
                            OJST.ui.widgets.Alerts.alert(OJST.i18n.label('error'),
                                Y.Lang.sub(OJST.i18n.msg('errorAccountNotUnique'), {fields: fields.join(',')}));
                        }
                    }
                }
            });
        }

    });

}, OJST.VERSION, {
    requires: [
        OJST.modules.views.PageAbstractAccount,
        OJST.modules.models.Account,
        OJST.modules.widgets.form.Form,
        OJST.modules.widgets.form.TextField,
        OJST.modules.widgets.Alerts
    ]});