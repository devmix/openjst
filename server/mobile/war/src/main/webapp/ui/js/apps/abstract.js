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
YUI.add(OJST.modules.apps.Abstract, function (Y) {
    "use strict";

    var CLASS = {
            CONTAINER: 'application'
        },
        TPL = {
            FOOTER: '<footer id="footer"><div class="navbar"><div class="navbar-inner"></div></div></footer>'
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
            OJST.singleton.Application = this;
            /**
             * @type {Y.EventHandle[]}
             * @private
             */
            this._subscribers = [];

            this._subscribers.push(this.after('activeViewChange', this._onAfterActiveViewChange, this));
            this._subscribers.push(Y.before(this._onBeforeRender, this, 'render'));
            this._subscribers.push(Y.after(this._onAfterRender, this, 'render'));
        },

        /** @override */
        destructor: function () {
            OJST.ui.utils.Framework.detach(this._subscribers);
            delete this._subscribers;
            delete OJST.singleton.Application;
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
         * @param {Y.Node} container
         * @param {Y.Node} viewContainer
         * @param {Object} navigationCfg
         * @return {OJST.ui.widgets.NavigationBar}
         * @private
         */
        _createNavigationBar: function (container, viewContainer, navigationCfg) {
            var buttons = [], menuItems = [], navigationBar;

            if (navigationCfg.buttons && navigationCfg.buttons.length > 0) {
                buttons.push({ kind: '-' });
                Y.each(navigationCfg.buttons, function (button) {
                    buttons.push({
                        label: button.label,
                        handler: button.handler || (button.route ? function () {
                            this.save(button.route);
                        } : null),
                        scope: this
                    });
                }, this);
                buttons.push({ kind: '-' });
            }

            if (navigationCfg.menu && navigationCfg.menu.length > 0) {
                Y.each(navigationCfg.menu, function (menu) {
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

            navigationBar = new OJST.ui.widgets.NavigationBar({
                brand: this.get('brand'),
                children: [
                    { children: buttons },
                    {
                        align: 'right',
                        children: [
                            { kind: '-' },
                            {
                                childType: OJST.ui.widgets.NavigationBarDropDownMenu,
                                label: navigationCfg.menuLabel,
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
                viewContainer = this.get('viewContainer'),
                navigation = this.get('navigation');

            viewContainer.addClass(CLASS.CONTAINER);

            this._fluid = this.get('fluid');

            if (navigation) {
                this.set('navigationBar', this._createNavigationBar(container, viewContainer, navigation));
            }

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
            navigationBar: {
                validator: function (v) {
                    return v instanceof OJST.ui.widgets.NavigationBar;
                }
            },
            fluid: {
                writeOnce: 'initOnly',
                value: true,
                validator: Y.Lang.isBoolean
            }
        }

    });

}, OJST.VERSION, {
    requires: [
        OJST.modules.widgets.NavigationBar,
        'app'
    ]});