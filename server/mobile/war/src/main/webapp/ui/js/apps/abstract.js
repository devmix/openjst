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
YUI.add(OJST.ns.apps.Abstract, function (Y) {
    "use strict";

    var CLASS = {
            CONTAINER: 'application'
        },
        TPL = {
            FOOTER: '<footer id="footer"><nav class="navbar navbar-default"><div class="navbar-inner"></div></nav></footer>'
        };

    /**
     * @class Abstract
     * @namespace OJST.ui.apps
     * @constructor
     * @extends Y.App
     */
    OJST.ui.apps.Abstract = Y.Base.create('appsAbstract', Y.App, [], {

        /** @override */
        initializer: function (cfg) {
            OJST.app.assignYUIApplication(this);

            this.views = {};

            /**
             * @type {{route: string, label: string}[]}
             * @private
             */
            this._buttons = [];
            /**
             * @type {Y.EventHandle[]}
             * @private
             */
            this._subscribers = [];

            this.subscribe(this.after('activeViewChange', this._onAfterActiveViewChange, this));
            this.subscribe(Y.before(this._onBeforeRender, this, 'render'));
            this.subscribe(Y.after(this._onAfterRender, this, 'render'));

            this._initializePages();
        },

        /** @override */
        destructor: function () {
            OJST.ui.utils.Framework.detach(this._subscribers);
            delete this._subscribers;
            OJST.app.assignYUIApplication(null);
        },

        /**
         * @param {Y.EventHandle} handler
         * @public
         */
        subscribe: function (handler) {
            this._subscribers.push(handler);
        },

        /**
         * @protected
         */
        renderUI: function () {
            return undefined;
        },

        /**
         * @protected
         */
        bindUI: function () {
            return undefined;
        },

        /**
         * @protected
         */
        syncUI: function () {
            return undefined;
        },

        /**
         * @param {number} index
         * @private
         */
        _onChangeTab: function (index) {
            this.set('tabIndex', index);
            this.get('navigationBar').item(0).activate(index, true);
        },

        /**
         * @param {number} tabIndex
         * @param {{path: string, callbacks: function}[]} routes
         * @param {string} root
         * @param {string} path
         * @param {function} clazz
         * @param {function} parentClazz
         * @param {object[]} subPages
         * @private
         */
        _addPage: function (tabIndex, routes, root, path, clazz, parentClazz, subPages) {
            var i, length, page,
                viewName = clazz.NAME,
                thisPath = path.charAt(0) === '/' || path === '*' ? path : root + '/' + path,
                route = {
                    path: thisPath,
                    callbacks: function (request) {
                        this._onChangeTab(tabIndex);
                        this.showView(viewName, request.params);
                    }
                };

            this.views[viewName] = {
                type: clazz,
                parent: parentClazz ? parentClazz.NAME : undefined
            };

            if ('*' === path) {
                routes.push(route);
            } else {
                routes.splice(0, 0, route);
            }

            if (subPages && subPages.length > 0) {
                for (i = 0, length = subPages.length; i < length; i++) {
                    page = subPages[i];
                    this._addPage(tabIndex, routes, thisPath, page[0], page[1], clazz, page.length > 2 ? page[2] : undefined);
                }
            }
        },

        /**
         * @private
         */
        _initializePages: function () {
            var i, length, page, pages = this.get('pages'), routes = [];
            for (i = 0, length = pages.length; i < length; i++) {
                page = pages[i];
                this._buttons.push({route: page[0], label: page[2]});
                this._addPage(i, routes, null, page[0], page[1], null, page.length > 3 ? page[3] : undefined);
            }
            this.set('routes', routes);
        },

        /**
         * @param {Y.Node} container
         * @param {Y.Node} viewContainer
         * @param {Object} menuCfg
         * @return {OJST.ui.widgets.toolbar.Navigation}
         * @private
         */
        _createNavigationBar: function (container, viewContainer, menuCfg) {
            var buttons = [], menuItems = [], navigationBar;

            if (this._buttons && this._buttons.length > 0) {
                Y.each(this._buttons, function (button) {
                    buttons.push({
                        label: button.label,
                        handler: button.handler || (button.route ? function () {
                            this.save(button.route);
                        } : null),
                        scope: this
                    });
                }, this);
            }

            if (menuCfg.menu && menuCfg.menu.length > 0) {
                Y.each(menuCfg.menu, function (menu) {
                    if (menu === '-') {
                        menuItems.push({ kind: '-' });
                    } else if (Y.Lang.isString(menu)) {
                        menuItems.push({ kind: '!', label: menu });
                    } else {
                        menuItems.push({
                            label: menu.label,
                            handler: menu.handler,
                            scope: this
                        });
                    }
                }, this);
            }

            navigationBar = new OJST.ui.widgets.toolbar.Navigation({
                brand: this.get('brand'),
                children: [
                    { children: buttons },
                    {
                        align: 'right',
                        children: [
                            {
                                childType: OJST.ui.widgets.toolbar.NavigationDropDownMenu,
                                label: menuCfg.menuLabel,
                                children: menuItems
                            }
                        ]
                    }
                ]
            });

            navigationBar.render();
            container.insertBefore(navigationBar.get(OJST.STATIC.BBX), viewContainer);
            return navigationBar;
        },

        /**
         * @param {Y.Node} container
         * @return {Y.Node}
         * @private
         */
        _createFooter: function (container) {
            return container.appendChild(Y.Node.create(TPL.FOOTER));
        },

        /**
         * @private
         */
        _onBeforeRender: function () {
            var container = this.get('container'),
                viewContainer = this.get('viewContainer');

            viewContainer.addClass(CLASS.CONTAINER);

            this._fluid = this.get('fluid');

            this.set('navigationBar', this._createNavigationBar(container, viewContainer, this.get('menu')));
            this.set('footer', this._createFooter(container, viewContainer));
        },

        /**
         * @private
         */
        _onAfterRender: function () {
            this.renderUI();
            this.bindUI();
            this._bindUI();
            this.syncUI();
        },

        /**
         * @private
         */
        _bindUI: function () {
            if (this._fluid) {
                this._subscribers.push(Y.after('resize', this._updateSizeOfViewContainerAsync, window, this));
//                        this._subscribers.push(Y.on('domready', this._syncSizeOfViewContainer,  this));
                var viewContainer = this.get('viewContainer');
//                viewContainer.setStyle('overflowY', 'auto');
                viewContainer.setStyle('overflowY', 'hidden');
                viewContainer.setStyle('overflowX', 'hidden');
            }
        },

        _marginX: function (el) {
            return  (parseInt(el.getStyle('paddingLeft'), 10) || 0)
                + (parseInt(el.getComputedStyle('paddingRight'), 10) || 0)
                + (parseInt(el.getStyle('marginLeft'), 10) || 0)
                + (parseInt(el.getComputedStyle('marginRight'), 10) || 0);
        },

        _marginY: function (el) {
            return (parseInt(el.getComputedStyle('paddingTop'), 10) || 0)
                + (parseInt(el.getComputedStyle('paddingBottom'), 10) || 0)
                + (parseInt(el.getComputedStyle('marginTop'), 10) || 0)
                + (parseInt(el.getComputedStyle('marginBottom'), 10) || 0);
        },

        _updateSizeOfViewContainer: function () {
            var view = this.get('activeView'),
                footer = this.get('footer'),
                viewContainer = this.get('viewContainer'),
                parentHeight = viewContainer.get('parentNode').get('offsetHeight'),
                offsetTop = viewContainer.get('offsetTop'),
                footerHeight = footer ? footer.get('offsetHeight') + 4 : 0,
                viewContainerHeight = parentHeight - offsetTop - footerHeight,
                viewHeight = viewContainerHeight - this._marginY(viewContainer);

            viewContainer.set('offsetHeight', viewContainerHeight);
            view.get('container').set('offsetHeight', viewHeight);

            if (view.syncSize) {
                view.syncSize();
            }
        },

        /**
         * @param {EventFacade} e
         * @param {boolean} withoutDelay
         * @private
         */
        _updateSizeOfViewContainerAsync: function (e, withoutDelay) {
            if (this._lazyResize) {
                this._lazyResize.cancel();
                delete this._lazyResize;
            }
            var viewContainer = this.get('viewContainer');
            viewContainer.setStyle('visibility', 'hidden');
            this._lazyResize = Y.later(withoutDelay ? 0 : 50, this, function () {
                this._updateSizeOfViewContainer();
                viewContainer.setStyle('visibility', 'visible');
            });
        },

        /**
         * @param {EventFacade} e
         * @private
         */
        _onAfterActiveViewChange: function (e) {
            this._updateSizeOfViewContainer();
        }

    }, {

        ATTRS: {
            brand: {
                validator: Y.Lang.isString
            },
            footer: {
            },
            pages: {
                validator: Y.Lang.isArray
            },
            menu: {
                validator: Y.Lang.isObject
            },
            navigationBar: {
                validator: function (v) {
                    return v instanceof OJST.ui.widgets.toolbar.Navigation;
                }
            },
            fluid: {
                writeOnce: 'initOnly',
                value: true,
                validator: Y.Lang.isBoolean
            }
        }

    });

}, OJST.VERSION, {requires: [
    OJST.ns.widgets.toolbar.Navigation,
    'app'
]});