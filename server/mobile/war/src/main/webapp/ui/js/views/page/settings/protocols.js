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
YUI.add(OJST.ns.views.page.settings.Protocols, function (Y) {
    "use strict";

    /**
     * @class Protocols
     * @namespace OJST.ui.views.page.settings
     * @constructor
     * @extends OJST.ui.views.page.settings.Abstract
     */
    OJST.ui.views.page.settings.Protocols = Y.Base.create('viewsPageSettingsProtocols', OJST.ui.views.page.settings.Abstract, [], {
        /** @override */
        createForm: function () {
            return new OJST.ui.widgets.SettingsEditor({
                model: new OJST.ui.models.SettingGroup({ name: 'protocols' })
            });
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.views.page.settings.Abstract
]});