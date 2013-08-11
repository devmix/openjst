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
YUI.add(OJST.ns.views.page.Abstract, function (Y) {
    "use strict";

    var TPL = {
        EMPTY_FORM: '<span></span>'
    };

    /**
     * @class Abstract
     * @namespace OJST.ui.views.page
     * @constructor
     * @extends OJST.ui.views.AbstractView
     */
    OJST.ui.views.page.Abstract = Y.Base.create('viewsPageAbstract', OJST.ui.widgets.AbstractView, [], {

            /** @override */
            initializer: function () {
                /**
                 * @type {Y.EventHandle[]}
                 * @private
                 */
                this._subscribers = [];
                /**
                 * @type {OJST.ui.widgets.Sidebar}
                 * @private
                 */
                this._sidebarWidget = undefined;
                /**
                 * @type {OJST.ui.widgets.Breadcrumbs}
                 * @private
                 */
                this._breadcrumbsWidget = undefined;
                /**
                 *
                 * @type {OJST.ui.widgets.Container}
                 * @private
                 */
                this._centerContainer = undefined;
                /**
                 * @type {*}
                 * @private
                 */
                this._form = undefined;

                this.set('layout', 'border');
            },

            /** @override */
            destructor: function () {
                OJST.ui.utils.Framework.detach(this._subscribers);

                if (this._sidebarWidget) {
                    this._sidebarWidget.destroy();
                }
                if (this._breadcrumbsWidget) {
                    this._breadcrumbsWidget.destroy();
                }
                if (this._form) {
                    this._form.destroy();
                }
                if (this._centerContainer) {
                    this._centerContainer.destroy();
                }

                var layout = this.get('layout');
                if (layout) {
                    layout.clear();
                }

                delete this._sidebarWidget;
                delete this._breadcrumbsWidget;
                delete this._form;
                delete this._centerContainer;
            },

            /**
             * @return {Object}
             * @protected
             */
            createTabs: function () {
                return undefined;
            },

            /**
             * @return {Object}
             * @protected
             */
            createBreadcrumbs: function () {
                return undefined;
            },

            /**
             * @return {Object}
             * @protected
             */
            createForm: function () {
                return undefined;
            },

            /**
             * @return {boolean|undefined}
             * @protected
             */
            isNewModel: function () {
                return undefined;
            },

            /**
             * @return {string}
             * @protected
             */
            getTabsTitle: function () {
                return undefined;
            },

            /**
             * @return {string}
             * @protected
             */
            getFormTitle: function () {
                var isNewModel = this.isNewModel();
                return Y.Lang.isValue(isNewModel)
                    ? (isNewModel ? OJST.i18n.label('create') : OJST.i18n.label('edit')) : null;
            },

            /**
             * @return { OJST.ui.views.page.Abstract}
             * @protected
             */
            render: function () {
                var container = this.get('container'),
                    layout = this.get('layout'),
                    breadcrumbs = this.createBreadcrumbs(),
                    formTitle = this.getFormTitle(),
                    tabs = this.createTabs(),
                    breadcrumbsCfg, tabsCfg;

                this._form = this.createForm();

                if (formTitle || (breadcrumbs && breadcrumbs.items && breadcrumbs.items.length > 0)) {
                    breadcrumbsCfg = {
                        title: formTitle,
                        children: []
                    };

                    if (breadcrumbs && breadcrumbs.items && breadcrumbs.items.length > 0) {
                        Y.each(breadcrumbs.items, function (item) {
                            breadcrumbsCfg.children.push({
                                label: item.label,
                                handler: item.handler || (item.route ? function () {
                                    OJST.app.saveRoute(item.route);
                                } : null),
                                scope: item.scope
                            });
                        });
                    }

                    this._breadcrumbsWidget = new OJST.ui.widgets.Breadcrumbs(breadcrumbsCfg);
                }

                if (tabs && ((tabs.items && tabs.items.length > 0) || tabs.title)) {
                    tabsCfg = {
                        title: tabs.title,
                        width: tabs.width,
                        children: []
                    };

                    if (tabs.items && tabs.items.length > 0) {
                        Y.each(tabs.items, function (item) {
                            tabsCfg.children.push({
                                label: item.label,
                                active: item.active,
                                visible: item.visible,
                                level: item.level,
                                handler: item.handler || (item.route ? function () {
                                    OJST.app.saveRoute(item.route);
                                } : null),
                                scope: item.scope
                            });
                        });
                    }

                    this._sidebarWidget = new OJST.ui.widgets.Sidebar(tabsCfg);

                    layout.add({region: 'left', element: this._sidebarWidget});
                }

                if (this._form) {
                    this._centerContainer = new OJST.ui.widgets.Container({
                        layout: 'border',
                        items: [
                            {region: 'top', element: this._breadcrumbsWidget},
                            {region: 'center', element: this._form || Y.Node.create(TPL.EMPTY_FORM)}
                        ]
                    });
                    layout.add({region: 'center', element: this._centerContainer});
                }

                layout.render(container);

                this.renderUI();

                return this;
            },

            /**
             * @protected
             */
            renderUI: function () {
                return undefined;
            },

            /**
             * @public
             * @param {String} text
             */
            setBreadcrumbsTitle: function (text) {
                if (this._breadcrumbsWidget) {
                    this._breadcrumbsWidget.set('title', text);
                }
            },

            /**
             * @return {*}
             * @public
             */
            getForm: function () {
                return this._form;
            }

        },
        {
            ATTRS: {}
        }
    );

}, OJST.VERSION, {requires: [
    OJST.ns.widgets.Container,
    OJST.ns.widgets.LoadingPanel,
    OJST.ns.widgets.Breadcrumbs,
    OJST.ns.widgets.Sidebar,
    OJST.ns.widgets.AbstractView,
    'view', 'button', 'anim'
]});