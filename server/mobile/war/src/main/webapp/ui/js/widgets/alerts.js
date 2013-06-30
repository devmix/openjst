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
YUI.add(OJST.modules.widgets.Alerts, function (Y) {
    "use strict";

    var confirmDialog, alertDialog;

    /**
     * @class Alerts
     * @namespace OJST.ui.widgets
     * @static
     */
    OJST.ui.widgets.Alerts = {

        /**
         * @param {string} title
         * @param {string} message
         * @return {Y.Panel}
         * @static
         * @public
         */
        alert: function (title, message) {
            if (alertDialog) {
                alertDialog.destroy();
            }

            alertDialog = new Y.Panel({
                headerContent: title,
                bodyContent: message,
                zIndex: 6,
                centered: true,
                modal: true,
                focused: false,
                render: true,
                buttons: [
                    {
                        value: OJST.i18n.label('ok'),
                        section: Y.WidgetStdMod.FOOTER,
                        isDefault: true,
                        action: function (e) {
                            e.preventDefault();
                            alertDialog.destroy();
                            alertDialog = null;
                        }
                    }
                ]

            });

            return alertDialog;
        },

        /**
         * @param {string} message
         * @param {string} title
         * @param {function} [callback]
         * @param {object} [scope]
         * @return {Y.Panel}
         */
        confirm: function (title, message, callback, scope) {
            if (confirmDialog) {
                confirmDialog.destroy();
            }

            confirmDialog = new Y.Panel({
                headerContent: title,
                bodyContent: message,
                zIndex: 6,
                centered: true,
                modal: true,
                focused: false,
                render: true,
                buttons: [
                    {
                        value: OJST.i18n.label('yes'),
                        section: Y.WidgetStdMod.FOOTER,
                        isDefault: true,
                        action: function (e) {
                            e.preventDefault();
                            if (callback) {
                                callback.call(scope || this, 'yes');
                            }
                            confirmDialog.destroy();
                            confirmDialog = null;
                        }
                    },
                    {
                        value: OJST.i18n.label('no'),
                        section: Y.WidgetStdMod.FOOTER,
                        action: function (e) {
                            e.preventDefault();
                            if (callback) {
                                callback.call(scope || this, 'no');
                            }
                            confirmDialog.destroy();
                            confirmDialog = null;
                        }
                    }
                ]

            });

            return confirmDialog;
        }
    };

}, OJST.VERSION, {
    requires: [
        'panel'
    ]});