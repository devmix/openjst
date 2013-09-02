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
YUI.add(OJST.ns.views.page.settings.Abstract, function (Y) {
    "use strict";

    /**
     * @class Abstract
     * @namespace OJST.ui.views.page.settings
     * @constructor
     * @extends  OJST.ui.views.page.Abstract
     */
    OJST.ui.views.page.settings.Abstract = Y.Base.create('viewsPageSettingsAbstract', OJST.ui.views.page.Abstract, [], {

        /** @override */
        createTabs: function () {
            return {
                title: OJST.i18n.label('settings'),
                width: 150,
                items: [
                    {label: OJST.i18n.label('interface'), route: '/settings/interface',
                        active: this instanceof OJST.ui.views.page.settings.Interface
                    },
                    {label: OJST.i18n.label('protocols'), route: '/settings/protocols',
                        active: this instanceof OJST.ui.views.page.settings.Protocols
                    }
                ]
            };
        }

    });

}, OJST.VERSION, {requires: [
    OJST.ns.views.page.Abstract
]});