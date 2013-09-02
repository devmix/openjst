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

    var Classes = OJST.ui.utils.Classes,

        DEFAULT_PAGE_SIZE = 15,
        COOKIE_GROUP = 'modelList',
        COOKIE_OPTIONS = {
            expires: new Date(32503680000000) // 3000-01-01 00:00:0000
        },
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
     * @param {*} obj
     * @param {Function} clazz
     * @param {boolean} [createInstance]
     * @returns {*}
     * @static
     * @private
     */
    function convertObjectToModel(obj, clazz, createInstance) {
        if (!Y.Lang.isValue(obj) || !Y.Lang.isFunction(clazz)) {
            return obj;
        }

        var attributes = clazz.ATTRS, field, attribute, converter, modelType, i, length;

        if (Y.Lang.isArray(obj)) {
            for (i = 0, length = obj.length; i < length; i++) {
                obj[i] = convertObjectToModel(obj[i], clazz, true);
            }
            return obj;
        }

        for (field in obj) {
            if (obj.hasOwnProperty(field)) {
                attribute = attributes[field];
                if (attribute) {
                    if (attribute.type) {
                        converter = CONVERTERS[attribute.type];
                        if (converter) {
                            obj[field] = converter(obj[field], attribute.format);
                        } else {
                            /*jslint evil:true*/
                            modelType = eval(attribute.type);
                            /*jslint evil:false*/
                            if (Y.Lang.isFunction(modelType) && Classes.isFunctionInstanceOf(modelType, Y.Model)) {
                                obj[field] = convertObjectToModel(obj[field], modelType, true);

                            } else {
                                obj[field] = modelType(obj[field], attribute.format);
                            }
                        }
                    } else if (attribute.converter && Y.Lang.isFunction(attribute.converter)) {
                        obj[field] = attribute.converter(obj[field], attribute.format);
                    }
                }
            }
        }

        /*jslint newcap:true*/
        return createInstance ? new clazz(obj) : obj;
    }

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
                    return convertObjectToModel(result.value, this.constructor, false);
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
                data = OJST.ui.utils.Framework.isValue(id) ? Y.Cookie.getSubs(COOKIE_GROUP + '.' + id, COOKIE_OPTIONS) : undefined;
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
                    Y.Cookie.setSub(COOKIE_GROUP + '.' + id, key, value, COOKIE_OPTIONS);
                }
            }
        },

        /**
         * @param {object[]} list
         * @return {object[]}
         * @private
         */
        _convertValues: function (list) {
            var i, length;
            for (i = 0, length = list.length; i < length; i++) {
                list[i] = convertObjectToModel(list[i], this.model, false);
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
    OJST.ns.utils.Classes,
    'cookie', 'model', 'model-list', 'model-sync-rest', 'json-parse', 'datatype'
]});