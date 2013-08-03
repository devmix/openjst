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
YUI.add(OJST.ns.models.Account, function (Y) {
    "use strict";

    /**
     * @class Account
     * @namespace OJST.ui.models
     * @constructor
     * @extends OJST.ui.models.Base
     */
    OJST.ui.models.Account = Y.Base.create('modelsAccount', OJST.ui.models.Base, [], {
        root: 'ui-api/accounts'
    }, {
        ATTRS: {
            id: {
                validator: Y.Lang.isNumber
            },
            authId: {validator: Y.Lang.isString},
            name: {validator: Y.Lang.isString}
        }
    });

    /**
     * @class AccountList
     * @namespace OJST.ui.models
     * @constructor
     * @extends OJST.ui.models.BaseList
     */
    OJST.ui.models.AccountList = Y.Base.create('modelsAccountList', OJST.ui.models.BaseList, [], {
        model: OJST.ui.models.Account
    });

}, OJST.VERSION, {requires: [
    OJST.ns.models.Base
]});