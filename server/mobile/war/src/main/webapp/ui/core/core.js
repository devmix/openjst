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

/*global OJST, $CONFIG$*/
/*jslint nomen:true, node:true, white:true, browser:true, plusplus:true*/
(function () {
    "use strict";

    var cfg = OJST.CONFIG;

    OJST.framework = OJST.manifest.framework;
    OJST.modules = OJST.manifest.modules.modules;
    OJST.libs = OJST.manifest.modules.libs;
    OJST.ui = OJST.manifest.ns.modules;
    OJST.ui.libs = OJST.manifest.ns.libs;

    /**
     * @type {I18n}
     */
    OJST.i18n = new OJST.core.I18n(cfg.i18n);

    /**
     * @type {CoreApplication}
     */
    OJST.app = new OJST.core.CoreApplication(cfg.app);

    /**
     * @type {Session}
     */
    OJST.session = new OJST.core.Session(cfg.user);

    /**
     * @type {CorePreferences}
     */
    OJST.preferences = new OJST.core.CorePreferences(cfg.preferences);

    // set framework properties

    OJST.framework.skin = 'sam';

    if (OJST.preferences.useDebugScripts()) {
        OJST.framework.filter = 'raw';
        OJST.framework.groups.openjst.filter = 'raw';
    }

    OJST.framework.maxURLLength = 0xFFFF;
    OJST.framework.combine = OJST.preferences.useComboServicesForScripts();

    OJST.framework.groups.openjst.maxURLLength = 0xFFFF;
    OJST.framework.groups.openjst.combine = OJST.preferences.useComboServicesForScripts();

}());

