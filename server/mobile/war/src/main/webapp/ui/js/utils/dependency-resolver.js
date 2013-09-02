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
YUI.add(OJST.ns.utils.DependencyResolver, function (Y) {
    "use strict";

    /**
     * @name ModuleState
     * @type {{active: boolean, available: boolean}}
     */

    /**
     * @name ModuleStateList
     * @type {{module: ModuleState}}
     */

    /**
     * @name ModuleActiveCheckCallback
     * @type {function(string)}
     */

    /**
     * @class DependencyResolver
     * @namespace OJST.ui.utils
     * @constructor
     */
    OJST.ui.utils.DependencyResolver = OJST.core.Lang.createClass({
        /**
         * @private
         */
        initializer: function () {
            /**
             * @type {{module: {dependency1:boolean,dependencyN:boolean}}
             * @private
             */
            this._forward = {};
            /**
             * @type {{dependency: {module1:boolean,moduleN:boolean}}
             * @private
             */
            this._reverse = {};
        },

        /**
         * @public
         */
        reset: function () {
            this._forward = {};
            this._reverse = {};
        },

        /**
         * @param {string|string[]} modules
         * @param {string[]} dependencies
         * @public
         */
        add: function (modules, dependencies) {
            if (!dependencies || dependencies.length === 0) {
                return;
            }

            var depIndex, depLength, modIndex, modLength, module, dependency, forwardData, reverseData;

            modules = modules ? (Y.Lang.isArray(modules) ? modules : [modules]) : [];
            dependencies = dependencies ? (Y.Lang.isArray(dependencies) ? dependencies : [dependencies]) : [];

            for (modIndex = 0, modLength = modules.length; modIndex < modLength; modIndex++) {
                module = modules[modIndex];

                forwardData = this._forward[module];
                if (!forwardData) {
                    this._forward[module] = forwardData = {};
                }

                for (depIndex = 0, depLength = dependencies.length; depIndex < depLength; depIndex++) {
                    dependency = dependencies[depIndex];
                    reverseData = this._reverse[dependency];
                    if (!reverseData) {
                        this._reverse[dependency] = reverseData = {};
                    }
                    reverseData[module] = true;
                    forwardData[dependency] = true;
                }
            }
        },

        /**
         * @param {ModuleActiveCheckCallback} checkCallback
         * @param {object} [scope]
         * @returns {ModuleStateList}
         * @public
         */
        resolveModulesStates: function (checkCallback, scope) {
            var visited = {}, forwardData = this._forward, module;
            for (module in forwardData) {
                if (forwardData.hasOwnProperty(module)) {
                    this._checkStateOfModule(module, checkCallback, scope, visited);
                }
            }
            return visited;
        },

        /**
         * @param {string} entryModule
         * @param {ModuleActiveCheckCallback} checkCallback
         * @param {object} [scope]
         * @returns {ModuleStateList}
         * @public
         */
        resolveDependenciesStates: function (entryModule, checkCallback, scope) {
            var visited = {}, reverseData = this._reverse[entryModule], depModule;
            for (depModule in reverseData) {
                if (reverseData.hasOwnProperty(depModule)) {
                    this._checkStateOfModule(depModule, checkCallback, scope, visited);
                }
            }
            return visited;
        },

        /**
         * @param {string} module
         * @param {ModuleActiveCheckCallback} checkCallback
         * @param {object} scope
         * @param {ModuleStateList} visited
         * @returns {ModuleState}
         * @private
         */
        _checkStateOfModule: function (module, checkCallback, scope, visited) {
            var state = visited[module],
                available = true, forwardData, depState, depModule;

            if (!Y.Lang.isUndefined(state)) {
                return state;
            }

            visited[module] = state = {active: checkCallback.call(scope || this, module), available: false};

            forwardData = this._forward[module];
            for (depModule in forwardData) {
                if (forwardData.hasOwnProperty(depModule)) {
                    depState = this._checkStateOfModule(depModule, checkCallback, scope, visited);
                    available = available && (depState.available && depState.active);
                }
            }

            state.available = available;

            return state;
        }
    });

}, OJST.VERSION, {requires: []});