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
YUI.add(OJST.ns.models.Update, function (Y) {
    "use strict";

    /**
     * @class Update
     * @namespace OJST.ui.models
     * @constructor
     * @extends OJST.ui.models.Base
     */
    OJST.ui.models.Update = Y.Base.create('modelUpdate', OJST.ui.models.Base, [], {
        root: 'ui-api/updates'
    }, {
        ATTRS: {
            accountId: {validator: Y.Lang.isNumber},
            id: {validator: Y.Lang.isNumber},
            os: {validator: Y.Lang.isString},
            version: {validator: Y.Lang.isString},
            uploadDate: {validator: Y.Lang.isDate, type: 'date'},
            description: {validator: Y.Lang.isString},
            uploadId: {validator: Y.Lang.isString}
        }
    });

    /**
     * @class UpdateList
     * @namespace OJST.ui.models
     * @constructor
     * @extends OJST.ui.models.BaseList
     */
    OJST.ui.models.UpdateList = Y.Base.create('modelsUpdateList', OJST.ui.models.BaseList, [], {
        model: OJST.ui.models.Update,
        url: 'ui-api/updates?accountId={accountId}'
    }, {
        ATTRS: {
            accountId: {validator: Y.Lang.isNumber}
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.models.Base
]});