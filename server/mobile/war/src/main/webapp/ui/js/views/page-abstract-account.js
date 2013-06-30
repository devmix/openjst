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
YUI.add(OJST.modules.views.PageAbstractAccount, function (Y) {
    "use strict";

    /**
     * @class PageAbstractAccount
     * @namespace OJST.ui.views
     * @constructor
     * @extends OJST.ui.views.PageAbstract
     */
    OJST.ui.views.PageAbstractAccount = Y.Base.create('viewsPageAbstractAccount', OJST.ui.views.PageAbstract, [], {

        /** @override */
        getTabsTitle: function () {
            return OJST.i18n.label('account');
        },

        /** @override */
        createTabs: function () {
            var accountId = this.get('accountId');
            return {
                title: this.getTabsTitle(),
                width: 150,
                items: [
                    {label: OJST.i18n.label('parameters'), route: '/accounts/' + accountId,
                        active: this instanceof OJST.ui.views.PageAccount
                    },

                    {label: OJST.i18n.label('users'), route: '/accounts/' + accountId + '/users',
                        active: this instanceof OJST.ui.views.PageAccountUsers,
                        visible: !this.isNewModel()
                    },

                    {label: OJST.i18n.label('user'),
                        active: this instanceof OJST.ui.views.PageAccountUser,
                        visible: !this.isNewModel() && this instanceof OJST.ui.views.PageAccountUser,
                        level: 1
                    }
                ]
            };
        }

    });

}, OJST.VERSION, {
    requires: [
        OJST.modules.views.PageAbstract
    ]});