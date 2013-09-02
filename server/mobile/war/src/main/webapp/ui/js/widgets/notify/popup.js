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
YUI.add(OJST.ns.widgets.notify.Popup, function (Y) {
    "use strict";

    var TPL = {
        ALERT: '<div class="yui3-widgets-notify-popup alert"></div>',
        REMOVE_BTN: '<button type="button" class="close" style="margin-left: 8px;">&times;</button>'
    };

    /**
     * @class Popup
     * @namespace OJST.ui.widgets.notify
     * @constructor
     * @extends Y.Base
     */
    OJST.ui.widgets.notify.Popup = Y.Base.create('widgets-notify-popup', Y.Base, [], {

        /** @override */
        initializer: function () {
            /**
             * @type {Y.Node}
             * @private
             */
            this._node = undefined;
            /**
             * @type {Y.Anim}
             * @private
             */
            this._anim = undefined;
            /**
             * @type {Timer}
             * @private
             */
            this._hideTimer = undefined;
            /**
             * @type {boolean}
             * @private
             */
            this._active = false;
            /**
             * @type {{message: string, type: string}[]}
             * @private
             */
            this._queue = [];
        },

        /** @override */
        destructor: function () {
            this.hide();
        },

        /**
         * @param {string} message
         * @param {'success'|'info'|'warning'|'danger'} type
         * @param {number} [hideDelay]
         * @public
         */
        show: function (message, type, hideDelay) {
            if (this._active) {
                this._queue.push([message, type]);
                return;
            }

            this._active = true;
            this._node = this._createPopupNode(message, type);

            this.get('parent').append(this._node);

            this.syncSize();
            this._runAnimation(hideDelay);
        },

        /**
         * @param {boolean} [clearQueue = false]
         * @public
         */
        hide: function (clearQueue) {
            if (this._active) {
                if (this._hideTimer) {
                    this._hideTimer.cancel();
                }
                if (this._anim) {
                    this._anim.stop();
                }
                if (this._node) {
                    this._node.remove(true);
                }
            }

            if (clearQueue) {
                this._queue = [];
            }

            this._node = undefined;
            this._hideTimer = undefined;
            this._anim = undefined;
            this._active = false;
        },

        /**
         * @public
         */
        showNext: function () {
            this.hide();

            if (this._queue.length > 0) {
                var next = this._queue.splice(0, 1)[0];
                this.show(next[0], next[1]);
            }
        },

        /**
         * @public
         */
        syncSize: function () {
            if (!this._active) {
                return;
            }

            var parentNode = this.get('parent'),
                parentWidth = parentNode.get('offsetWidth'),
                popupWidth = this._node.get('offsetWidth');

            if (popupWidth + 8 > parentWidth) { // popupWidth + (left + right margins)
                popupWidth = parentWidth - 8;
                this._node.setStyle('width', popupWidth + 'px');
            }

            this._node.setStyle('left', (parentWidth / 2 - popupWidth / 2) + 'px');
        },

        /**
         * @param {string} message
         * @param {'success'|'info'|'warning'|'danger'} type
         * @returns {Y.Node}
         * @private
         */
        _createPopupNode: function (message, type) {
            var node = Y.Node.create(TPL.ALERT)
                .addClass('alert-' + type)
                .setStyles({
                    position: 'absolute',
                    top: '-10000px',
                    left: '-10000px'
                })
                .set('innerHTML', TPL.REMOVE_BTN + message);

            node.delegate('click', this.showNext, 'button.close', this);

            return node;
        },

        /**
         * @param {number} hideDelay
         * @private
         */
        _runAnimation: function (hideDelay) {
            this._node.setStyle('top', -this._node.get('offsetHeight') + 'px');

            this._anim = new Y.Anim({
                node: this._node,
                duration: 0.4,
                to: { top: 4, opacity: 1 },
                on: {
                    end: Y.bind(function () {
                        if (!this._anim.get('runHide') && this.get('autoHide')) {
                            this._hideTimer = Y.later(hideDelay || this.get('delayBeforeHide'), this, function () {
                                this._anim
                                    .set('runHide', true)
                                    .set('to', { opacity: 0 })
                                    .run();
                            });
                        } else {
                            this.showNext();
                        }
                    }, this)
                }
            });

            this._anim.run();
        }

    }, {
        ATTRS: {
            parent: {
                writeOnce: 'initOnly',
                validator: function (v) {
                    return v instanceof Y.Node;
                }
            },
            message: {
                validator: Y.Lang.isString
            },
            type: {
                validator: Y.Lang.isString
            },
            autoHide: {
                validator: Y.Lang.isBoolean,
                value: true
            },
            delayBeforeHide: {
                validator: Y.Lang.isNumber,
                value: 1000
            }
        }
    });

}, OJST.VERSION, {requires: [
    'base', 'anim'
]});