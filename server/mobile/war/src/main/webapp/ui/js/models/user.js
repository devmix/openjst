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
YUI.add(OJST.modules.models.User, function (Y) {
    "use strict";

    /**
     * @class User
     * @namespace OJST.ui.models
     * @constructor
     * @extends OJST.ui.models.Base
     */
    OJST.ui.models.User = Y.Base.create('modelUser', OJST.ui.models.Base, [], {
        root: 'ui-api/users'
    }, {
        ATTRS: {
            id: {value: null},
            authId: {value: null},
            name: {value: null},
            role: {value: null},
            language: {value: 'EN'},
            account: {value: null}
        }
    });

    /**
     * @class UserList
     * @namespace OJST.ui.models
     * @constructor
     * @extends OJST.ui.models.BaseList
     */
    OJST.ui.models.UserList = Y.Base.create('modelsUserList', OJST.ui.models.BaseList, [], {
        model: OJST.ui.models.User,
        url: 'ui-api/users?accountId={accountId}'
    }, {
        ATTRS: {
            accountId: {}
        }
    });

}, OJST.VERSION, {
    requires: [
        OJST.modules.models.Base
    ]});