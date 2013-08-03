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
/*jslint nomen:true, node:true, white:true, browser:true, plusplus:true, stupid: true*/
YUI.add(OJST.ns.models.Base, function (Y) {
    "use strict";

    var DEFAULT_PAGE_SIZE = 15,
        COOKIE_GROUP = 'modelList',
        RESULT_ARGS = {
            START_INDEX: 'startIndex',
            TOTAL: 'total',
            PAGE_SIZE: 'pageSize',
            SEARCH: 'search'
        },
        QUERY_ARGS = {
            START_INDEX: 'startIndex',
            PAGE_SIZE: 'pageSize',
            SEARCH: 'search'
        },
        STATUS = {
            OK: 'OK',
            NOT_UNIQUE: 'NOT_UNIQUE',
            NOT_EXISTS: 'NOT_EXISTS',
            INTERNAL_SERVER_ERROR: 'INTERNAL_SERVER_ERROR'
        },
        CONVERTERS = {
            date: function (v) {
                return Y.Date.parse(Y.Lang.isDate(v) ? v : new Date(v));
            }
        };

    /**
     * @type {{OK: string, NOT_UNIQUE: string, NOT_EXISTS: string, INTERNAL_SERVER_ERROR: string}}
     */
    OJST.ui.models.STATUS = STATUS;

    /**
     * @class Base
     * @namespace OJST.ui.models
     * @constructor
     * @extends Y.Model
     * @uses Y.ModelSync.REST
     */
    OJST.ui.models.Base = Y.Base.create('modelsBase', Y.Model, [Y.ModelSync.REST], {

        /** @override */
        initializer: function () {
            /**
             * @type {boolean}
             * @private
             */
            this._lastOperationFailed = undefined;
        },

        /** @override */
        parse: function (response) {
            this._lastOperationFailed = false;

            if (!Y.Lang.isString(response)) {
                return response || [];
            }

            try {
                var result = Y.JSON.parse(response);
                this._lastOperationFailed = result.status !== STATUS.OK;
                if (this._lastOperationFailed) {
                    this._fireServerError(result);
                } else {
                    return result.list && result.list.length > 0 ? result.list[0] : undefined;
                }
            } catch (e) {
                this._lastOperationFailed = true;
                this.fire('error', { error: e, response: response, src: 'parse' });
            }

            return null;
        },

        /**
         * @return {boolean}
         * @public
         */
        isLastOperationFailed: function () {
            return  this._lastOperationFailed;
        },

        /**
         * @param {object} result
         * @private
         */
        _fireServerError: function (result) {
//            var message;

//            switch (result.status) {
//                case STATUS.NOT_UNIQUE:
//                    message = Y.Lang.sub(OJST.i18n.msg('errorNotUnique'),
//                        {fields: result.errorData ? result.errorData.join(',') : ''});
//                    break;
//
//                case STATUS.NOT_EXISTS:
//                    message = OJST.i18n.msg('errorNotExists');
//                    break;
//            }

            this.fire('error', { status: result.status, errorData: result.errorData, src: 'server' });
        }

    }, {
        ATTRS: {
            readOnly: {
                value: false,
                validator: Y.Lang.isBoolean
            }
        }
    });

    /**
     * @class BaseList
     * @namespace OJST.ui.models
     * @constructor
     * @extends Y.ModelList
     * @uses Y.ModelSync.REST
     */
    OJST.ui.models.BaseList = Y.Base.create('modelsBaseList', Y.ModelList, [Y.ModelSync.REST], {

        /** @override */
        initializer: function () {
            /**
             * @type {{string: {type: string, format: string, converter: function}}}
             * @private
             */
            this._fieldTypes = this._getTypesOfModelFields();

            this._loadState();
        },

        /** @override */
        parse: function (response) {
            if (typeof response !== 'string') {
                return response || [];
            }

            try {
                var result = Y.JSON.parse(response);
                this.set(QUERY_ARGS.START_INDEX, result.startIndex);
                this.set(QUERY_ARGS.PAGE_SIZE, result.pageSize || DEFAULT_PAGE_SIZE);
                this.set(QUERY_ARGS.SEARCH, result.search);
                this.set(RESULT_ARGS.TOTAL, result.total);
                return this._convertValues(result.list || []);
            } catch (ex) {
                this.fire('error', {
                    error: ex,
                    response: response,
                    src: 'parse'
                });
                return null;
            }
        },

        /** @override */
        getURL: function (action, options) {
            var url = Y.Lang.trim(Y.ModelSync.REST.prototype.getURL.apply(this, arguments)),
                parameters = [],
                startParametersIndex = url.indexOf('?');

            if (url && url.length > 0) {
                Y.each(QUERY_ARGS, function (p) {
                    var value = this.get(p);
                    if (url.indexOf(p + '=') === -1 && value) {
                        parameters.push(p + "=" + encodeURIComponent(value));
                    }
                }, this);

                if (parameters.length > 0) {
                    url += (startParametersIndex !== -1 ? '&' : '?') + parameters.join('&');
                }
            }

            return url;
        },

        /**
         * @private
         */
        _loadState: function () {
            var id = this.get('persistentId'),
                data = OJST.ui.utils.Framework.isValue(id) ? Y.Cookie.getSubs(COOKIE_GROUP + '.' + id) : undefined;
            if (data) {
                if (data[QUERY_ARGS.START_INDEX]) {
                    this.set(QUERY_ARGS.START_INDEX, parseInt(data[QUERY_ARGS.START_INDEX], 10));
                }
                if (data[QUERY_ARGS.PAGE_SIZE]) {
                    this.set(QUERY_ARGS.PAGE_SIZE, parseInt(data[QUERY_ARGS.PAGE_SIZE], 10));
                }
                if (data[QUERY_ARGS.SEARCH]) {
                    this.set(QUERY_ARGS.SEARCH, data[QUERY_ARGS.SEARCH]);
                }
            }
        },

        /**
         * @param {string} key
         * @param {*} value
         * @private
         */
        _storeState: function (key, value) {
            if (key) {
                var id = this.get('persistentId');
                if (id) {
                    Y.Cookie.setSub(COOKIE_GROUP + '.' + id, key, value);
                }
            }
        },

        /**
         * @type {{string: {type: string, format: string, converter: function}}}
         * @private
         */
        _getTypesOfModelFields: function () {
            var types = {};
            if (this.model) {
                Y.each(this.model.ATTRS, function (v, k) {
                    if (v.type || v.converter) {
                        types[k] = {type: v.type, format: v.format, converter: v.converter};
                    }
                });
            }
            return types;
        },

        /**
         * @param {object[]} list
         * @return {object[]}
         * @private
         */
        _convertValues: function (list) {
            if (!this._fieldTypes || this._fieldTypes.length === 0) {
                return [];
            }

            var i, length, model, field, c;
            for (i = 0, length = list.length; i < length; i++) {
                model = list[i];
                for (field in this._fieldTypes) {
                    if (this._fieldTypes.hasOwnProperty(field) && model.hasOwnProperty(field)) {
                        c = this._fieldTypes[field];
                        if (c.type && CONVERTERS[c.type]) {
                            model[field] = CONVERTERS[c.type](model[field], c.format);
                        } else if (c.converter && Y.Lang.isFunction(c.converter)) {
                            model[field] = c.converter(model[field], c.format);
                        }
                    }
                }
            }

            return list;
        }

    }, {
        DEFAULT_PAGE_SIZE: DEFAULT_PAGE_SIZE,
        ATTRS: {
            startIndex: {
                value: 0,
                validator: Y.Lang.isNumber,
                setter: function (v) {
                    this._storeState(QUERY_ARGS.START_INDEX, v);
                    return v;
                }
            },
            total: {
                value: 0,
                validator: Y.Lang.isNumber,
                setter: function (v) {
                    this._storeState(QUERY_ARGS.SEARCH, v);
                    return v;
                }
            },
            pageSize: {
                value: DEFAULT_PAGE_SIZE,
                validator: Y.Lang.isNumber,
                setter: function (v) {
                    this._storeState(QUERY_ARGS.PAGE_SIZE, v);
                    return v;
                }
            },
            search: {
                validator: Y.Lang.isString
            },
            persistentId: {
                validator: Y.Lang.isString
            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.utils.Framework,
    'cookie', 'model', 'model-list', 'model-sync-rest', 'json-parse', 'datatype'
]});