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
YUI.add(OJST.modules.views.PageAccountUser, function (Y) {
    "use strict";

    /**
     * @class PageAccountUser
     * @namespace OJST.ui.views
     * @constructor
     * @extends OJST.ui.views.PageAbstractAccount
     */
    OJST.ui.views.PageAccountUser = Y.Base.create('viewsPageAccountUser', OJST.ui.views.PageAbstractAccount, [], {

        /** @override */
        getTabsTitle: function () {
            return OJST.i18n.label('user');
        },

        /** @override */
        isNewModel: function () {
            var id = this.get('userId');
            return !Y.Lang.isValue(id) || '' === id;
        },

        /** @override */
        createForm: function () {
            var accountId = this.get('accountId'), userId = this.get('userId'), model;
            if (this.isNewModel()) {
                model = new OJST.ui.models.User({ id: null, account: new OJST.ui.models.Account({id: Number(accountId)}) });
            } else {
                model = new OJST.ui.models.User({ id: Number(userId) });
            }
            return new OJST.ui.widgets.form.Form({
                horizontal: true,
                model: model,
                auto: true,
                useDefaultButtons: true,
                children: [
                    {
                        type: OJST.ui.widgets.form.TextField,
                        name: 'name',
                        label: OJST.i18n.label('name'),
                        required: true
                    },
                    {
                        type: OJST.ui.widgets.form.TextField,
                        name: 'authId',
                        label: OJST.i18n.label('authId'),
                        required: true
                    },
                    {
                        type: OJST.ui.widgets.form.TextField,
                        name: 'password',
                        label: 'Password',
                        isPassword: true,
                        placeholder: OJST.i18n.label('passwordPlaceholder')
                    },
                    {
                        type: OJST.ui.widgets.form.ListField,
                        name: 'role',
                        label: OJST.i18n.label('role'),
                        data: [
                            ['ADMIN', OJST.i18n.label('administrator')],
                            ['USER', OJST.i18n.label('user')]
                        ],
                        required: true
                    },
                    {
                        type: OJST.ui.widgets.form.ListField,
                        name: 'language',
                        label: OJST.i18n.label('language'),
                        data: [
                            ['EN', OJST.i18n.label('english')],
                            ['RU', OJST.i18n.label('russian')]
                        ],
                        value: 'RU'
                    }
                ],
                on: {
                    'create': function (m) {
                        OJST.app.saveRoute('/accounts/' + accountId + '/users/' + m.get('id'));
                    },
                    'close': function () {
                        OJST.app.saveRoute('/accounts/' + accountId + '/users');
                    },
                    'error': function (e) {
                        if (e.status === OJST.ui.models.STATUS.NOT_UNIQUE) {
                            var fields = [];
                            Y.each(e.errorData, function (field) {
                                fields.push(OJST.i18n.label(field));
                            });
                            OJST.ui.widgets.Alerts.alert(OJST.i18n.label('error'),
                                Y.Lang.sub(OJST.i18n.msg('errorUserNotUnique'), {fields: fields.join(',')}));
                        }
                    }
                }
            });
        }

    });

}, OJST.VERSION, {
    requires: [
        OJST.modules.views.PageAbstract,
        OJST.modules.models.User,
        OJST.modules.widgets.form.Form,
        OJST.modules.widgets.form.TextField,
        OJST.modules.widgets.form.ListField
    ]});