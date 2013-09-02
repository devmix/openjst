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
YUI.add(OJST.ns.models.Setting, function (Y) {
    "use strict";

    /**
     * @class Setting
     * @namespace OJST.ui.models
     * @constructor
     * @extends OJST.ui.models.Base
     */
    OJST.ui.models.Setting = Y.Base.create('modelSetting', OJST.ui.models.Base, [], {
    }, {
        ATTRS: {
            name: {},
            type: {},
            value: {},
            defaultValue: {},
            requires: {}
        }
    });

    /**
     * @class SettingSection
     * @namespace OJST.ui.models
     * @constructor
     * @extends OJST.ui.models.Base
     */
    OJST.ui.models.SettingSection = Y.Base.create('modelSettingSection', OJST.ui.models.Base, [], {
    }, {
        ATTRS: {
            name: {},
            settings: {type: 'OJST.ui.models.Setting'},
            requires: {}
        }
    });

    /**
     * @class SettingGroup
     * @namespace OJST.ui.models
     * @constructor
     * @extends OJST.ui.models.Base
     */
    OJST.ui.models.SettingGroup = Y.Base.create('modelSettingGroup', OJST.ui.models.Base, [], {
        url: 'ui-api/settings/{name}'
    }, {
        ATTRS: {
            name: {},
            sections: {type: 'OJST.ui.models.SettingSection'}
        }
    });

//    /**
//     * @class SimpleSetting
//     * @namespace OJST.ui.models
//     * @constructor
//     * @extends OJST.ui.models.Base
//     */
//    OJST.ui.models.SimpleSetting = Y.Base.create('modelSimpleSetting', OJST.ui.models.Base, [], {
//    }, {
//        ATTRS: {
//            name: {},
//            value: {}
//        }
//    });
//
//    /**
//     * @class UserList
//     * @namespace OJST.ui.models
//     * @constructor
//     * @extends OJST.ui.models.BaseList
//     */
//    OJST.ui.models.SettingList = Y.Base.create('settingList', OJST.ui.models.BaseList, [], {
//        model: OJST.ui.models.SimpleSetting,
//        url: 'ui-api/settings'
//    }, {
//    });

}, OJST.VERSION, {requires: [
    OJST.ns.models.Base
]});