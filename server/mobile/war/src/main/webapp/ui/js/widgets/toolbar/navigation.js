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
YUI.add(OJST.ns.widgets.toolbar.Navigation, function (Y) {
    "use strict";

    var CLASS = {
            active: 'active'
        },
        TPL = {
            DROP_DOWN_MENU: '<a class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0)">{label} <b class="caret"></b></a>',
            DROP_DOWN_MENU_ITEM: '<a role="menuitem" href="javascript:void(0)">{label}</a>'
        };

    /**
     * @class NavigationDropDownMenuItem
     * @namespace OJST.ui.widgets.toolbar
     * @constructor
     * @extends Y.Widget
     */
    OJST.ui.widgets.toolbar.NavigationDropDownMenuItem = Y.Base.create('dropDownMenuItem', Y.Widget, [Y.WidgetChild], {

        CONTENT_TEMPLATE: null,
        BOUNDING_TEMPLATE: '<li role="presentation"></li>',

        /** @override */
        initializer: function () {
            /**
             * @type {Y.EventHandle[]}
             * @private
             */
            this._subscribers = [];
        },

        /** @override */
        destructor: function () {
            OJST.ui.utils.Framework.detach(this._subscribers);
            delete this._subscribers;
        },

        /** @override */
        renderUI: function () {
            var contentBox = this.get("boundingBox"),
                type = this.get('kind'),
                label = this.get('label');

            if (type === '-') {
                contentBox.addClass('divider');
            } else if (type === '!') {
                contentBox.addClass('dropdown-header');
                contentBox.setContent(Y.Escape.html(label));
            } else {
                contentBox.appendChild(Y.Node.create(Y.Lang.sub(TPL.DROP_DOWN_MENU_ITEM, { label: label || '' })));
            }
        },

        /** @override */
        bindUI: function () {
            this._subscribers.push(this.on("click", this._onClick));
        },

        /**
         * @param {Y.EventFacade} e
         * @private
         */
        _onClick: function (e) {
            e.preventDefault();
            var handler = this.get('handler');
            if (handler) {
                handler.call(this.get('scope') || this);
            }
        }

    }, {
        ATTRS: {
            label: {
                validator: Y.Lang.isString
            },
            kind: {
                validator: Y.isString,
                writeOnce: 'initOnly'
            },
            tabIndex: {
                validator: Y.isNumber,
                value: -1
            },
            handler: {
                validator: Y.isFunction
            },
            scope: {
                validator: Y.isObject
            }
        }
    });

    /**
     * @class NavigationDropDownMenu
     * @namespace OJST.ui.widgets.toolbar
     * @constructor
     * @extends Y.Widget
     * @uses Y.WidgetParent
     * @uses Y.WidgetChild
     */
    OJST.ui.widgets.toolbar.NavigationDropDownMenu = Y.Base.create('dropDownMenu', Y.Widget, [Y.WidgetParent, Y.WidgetChild], {

        CONTENT_TEMPLATE: '<ul role="menu" class="dropdown-menu"></ul>',
        BOUNDING_TEMPLATE: '<li class="dropdown"></li>',

        /** @override */
        renderUI: function () {
            this.get("boundingBox")
                .appendChild(Y.Node.create(Y.Lang.sub(TPL.DROP_DOWN_MENU, {label: this.get('label')})));
        }

    }, {
        ATTRS: {
            defaultChildType: {
                value: OJST.ui.widgets.toolbar.NavigationDropDownMenuItem
            },
            label: {
                validator: Y.Lang.isString
            }
        }
    });

    /**
     * @class NavigationGroupItem
     * @namespace OJST.ui.widgets.toolbar
     * @constructor
     * @extends Y.Widget
     * @uses Y.WidgetParent
     */
    OJST.ui.widgets.toolbar.NavigationGroupItem = Y.Base.create("groupItem", Y.Widget, [Y.WidgetChild], {

        CONTENT_TEMPLATE: null,
        BOUNDING_TEMPLATE: '<li></li>',

        /** @override */
        renderUI: function () {
            var contentBox = this.get("boundingBox");
            contentBox.appendChild(Y.Node.create(Y.Lang.sub('<a href="javascript:void(0)"><b>{label}</b></a>', {
                label: this.get('label') || ''
            })));
        },

        /**
         * @public
         */
        activate: function () {
            this.get(OJST.STATIC.CBX).addClass(CLASS.active);
        },

        /**
         * @public
         */
        deactivate: function () {
            this.get(OJST.STATIC.CBX).removeClass(CLASS.active);
        }

    }, {
        ATTRS: {
            label: {
                validator: Y.Lang.isString
            },
            kind: { writeOnce: 'initOnly' },
            handler: { writeOnce: 'initOnly' },
            tabIndex: {
                value: -1
            },
            scope: { writeOnce: 'initOnly' }
        }
    });

    /**
     * @class NavigationGroup
     * @namespace OJST.ui.widgets.toolbar
     * @constructor
     * @extends Y.Widget
     * @uses Y.WidgetParent
     * @uses Y.WidgetChild
     */
    OJST.ui.widgets.toolbar.NavigationGroup = Y.Base.create("group", Y.Widget, [Y.WidgetParent, Y.WidgetChild], {

        CONTENT_TEMPLATE: null,
        BOUNDING_TEMPLATE: '<ul class="nav navbar-nav"></ul>',

        /** @override */
        initializer: function () {
            /**
             * @type {Y.EventHandle[]}
             * @private
             */
            this._subscribers = [];
        },

        /** @override */
        destructor: function () {
            OJST.ui.utils.Framework.detach(this._subscribers);
            delete this._subscribers;
        },

        /** @override */
        renderUI: function () {
            var align = this.get('align');
            if (align) {
                this.get("contentBox").addClass('pull-' + align);
            }
        },

        /** @override */
        bindUI: function () {
            this.on("groupItem:click", this._onButtonClick);
        },

        /**
         * @param index of tab
         * @param silent call handler or not
         * @public
         */
        activate: function (index, silent) {
            var item = this.item(index),
                handler, handlerScope, currentActive;

            if (!item) {
                return;
            }

            handler = item.get('handler');
            handlerScope = item.get('scope');
            if (!silent && handler) {
                handler.call(handlerScope || this);
            }

            currentActive = this.get('active');
            if (currentActive) {
                currentActive.deactivate();
            }

            item.activate();

            this.set('active', item);
        },

        /**
         * @param {EventFacade} e
         * @private
         */
        _onButtonClick: function (e) {
            e.preventDefault();
            this.activate(this.indexOf(e.target));
        }

    }, {
        ATTRS: {
            defaultChildType: {
                value: OJST.ui.widgets.toolbar.NavigationGroupItem
            },
            align: { writeOnce: 'initOnly' },
            active: {
                validator: function (v) {
                    return v instanceof OJST.ui.widgets.toolbar.NavigationGroupItem;
                }
            }
        }
    });

    /**
     * @class Navigation
     * @namespace OJST.ui.widgets.toolbar
     * @constructor
     * @extends Y.Widget
     */
    OJST.ui.widgets.toolbar.Navigation = Y.Base.create('widgets-toolbar-navigation', Y.Widget, [Y.WidgetParent], {

        //navbar-fixed-top
        BOUNDING_TEMPLATE: '<nav class="navbar navbar-default" role="navigation">' +
            '   <div class="navbar-header">' +
            '       <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">' +
            '           <span class="sr-only">Toggle navigation</span>' +
            '           <span class="icon-bar"></span>' +
            '           <span class="icon-bar"></span>' +
            '           <span class="icon-bar"></span>' +
            '       </button>' +
            '       <a class="navbar-brand" href="/"></a>' +
            '   </div>' +
            '</nav>',

        CONTENT_TEMPLATE: '<div class="collapse navbar-collapse navbar-ex1-collapse"></div>',

        /** @override */
        renderUI: function () {
            var brand = this.get('brand');
            if (brand) {
                this.get(OJST.STATIC.BBX).one('.navbar-brand').set('text', brand);
            }
        }

    }, {
        ATTRS: {
            defaultChildType: {
                value: OJST.ui.widgets.toolbar.NavigationGroup
            },
            brand: { writeOnce: 'initOnly' }
        }
    });

}, OJST.VERSION, {requires: [
    'escape', 'widget', 'widget-child', 'widget-parent'
]});