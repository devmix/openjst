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
YUI.add(OJST.ns.widgets.form.fields.UploadFile, function (Y) {
    "use strict";

    var Framework = OJST.ui.utils.Framework,
        TPL = {
            FORM: '<form action="#" method="POST" enctype="multipart/form-data" target="{frameId}" class="{class}">'
                + '<input type="file" id="{inputId}" name="file"> '
                + '<iframe id={frameId} name={frameId} src=""></iframe>'
                + '</form>',
            INPUT: '<input type="text" class="form-control" placeholder="{placeholder}" readonly="readonly">'
                + '<span class="input-group-btn btn-group"><button class="btn btn-default" type="button"><i class="glyphicon glyphicon-upload"></i></button></span>'
        },
        STATE = {
            IDLE: 0,
            UPLOADING: 1
        },
        STR = {
            SELECT_FILE: OJST.i18n.msg('selectFileToUpload'),
            UPLOADING_FILE: OJST.i18n.msg('waitFileUploading'),
            UPLOADED: OJST.i18n.msg('fileUploadedSuccessfully'),
            UPLOADING_ERROR: OJST.i18n.msg('errorUploadFile')
        };

    OJST.ui.widgets.form.fields.UploadFileForm = Y.Base.create('widgets-form-fields-upload-file-form', Y.Base, [], {

        /**
         * @override
         */
        initializer: function () {
            /**
             * @type {Y.EventHandle[]}
             * @private
             */
            this._subscribers = [];
            /**
             *
             * @type {number}
             * @private
             */
            this._status = STATE.IDLE;
            /**
             * @type {boolean}
             * @private
             */
            this._formRendered = false;
            /**
             * @type {object}
             * @private
             */
            this._lazyClick = undefined;
        },

        /**
         * @override
         */
        destructor: function () {
            Framework.detach(this._subscribers);

            if (this._form) {
                this._form.remove();
                this._form.destroy(true);
            }

            if (this._lazyClick) {
                this._lazyClick.cancel();
            }

            delete this._subscribers;
            delete this._form;
            delete this._status;
            delete this._lazyClick;
            delete this._formRendered;
        },

        /**
         * @public
         */
        showDialog: function () {
            if (this._status === STATE.UPLOADING) {
                throw 'Busy!';
            }

            if (!this._formRendered) {
                this.renderForm();
            }

            if (this._lazyClick) {
                this._lazyClick.cancel();
            }

            this._lazyClick = Y.later(100, this, function () {
                this._file.simulate('click');
            });
        },

        /**
         * @public
         */
        renderForm: function () {
            if (this._formRendered) {
                return;
            }

            var container = this.get('container'),
                frameId = Y.guid();

            this._inputId = Y.guid();
            this._form = Y.Node.create(Y.Lang.sub(TPL.FORM, {
                'frameId': frameId,
                'inputId': this._inputId,
                'class': 'yui3-' + this.name
            }));
            this._frame = this._form.one('iframe');
            this._file = this._form.one('input');

            this._subscribers.push(this._file.on('change', function (e) {
                this._status = STATE.UPLOADING;
                this.fire('start', e.target.get('value'));
                this._form.setAttribute('action', this.get('uploadUrl'));
                this._form.submit();
            }, this));

            this._subscribers.push(this._frame.on('load', function (e) {
                if (this._status === STATE.UPLOADING) {
                    var response = this._frame.get('contentWindow').get('document').get('body').get('text');
                    if (response.indexOf('OK:') !== 0) {
                        this.fire('end', response, true);
                    } else {
                        this.fire('end', response.substr(3), false);
                    }
                    this._status = STATE.IDLE;
                }
            }, this));

            container.append(this._form);

            this._formRendered = true;
        }

    }, {
        ATTRS: {
            container: {
                validator: Framework.isNode
            },
            uploadUrl: {
                validator: Y.Lang.isString
            }
        }
    });

    /**
     * @class UploadFile
     * @namespace OJST.ui.widgets.form.fields
     * @constructor
     * @extends OJST.ui.widgets.form.Field
     */
    OJST.ui.widgets.form.fields.UploadFile = Y.Base.create('widgets-form-fields-upload-file', OJST.ui.widgets.form.Field, [], {

        /**
         * @override
         */
        initializer: function () {
            this.set('trigger', {
                icon: 'upload',
                handler: this._onTriggerClick
            });
        },

        /**
         * @override
         */
        destructor: function () {
            if (this._form) {
                this._form.destroy();
            }
            this._contentNode.destroy(true);

            delete this._form;
            delete this._control;
            delete this._button;
            delete this._contentNode;
        },

        /**
         * @override
         */
        renderControl: function (container, controlId) {
            this._contentNode = Y.Node.create(Y.Lang.sub(TPL.INPUT, {
                id: controlId,
                name: this.get('name') || controlId,
                placeholder: this.get('placeholder') || STR.SELECT_FILE
            }));
            this._control = this._contentNode.one('input.form-control');
            this._button = this._contentNode.one('button.btn');

            container.append(this._contentNode);

            this._form = new OJST.ui.widgets.form.fields.UploadFileForm({
                container: this.get(OJST.STATIC.BBX),
                uploadUrl: this.get('uploadUrl'),
                on: {
                    start: Y.bind(function (e, fileName) {
                        this.disable();
                        this._control.set('value', Y.Lang.sub(STR.UPLOADING_FILE, {name: fileName}));
                    }, this),
                    end: Y.bind(function (e, externalId, error) {
                        this.enable();
                        if (error) {
                            this._control.set('value', STR.UPLOADING_ERROR);
                        } else {
                            this.set('value', externalId);
                        }
                        this.validate();
                    }, this)
                }
            });

            return this._control;
        },

        /**
         * @override
         */
        bindUI: function () {
            OJST.ui.widgets.form.fields.UploadFile.superclass.bindUI.apply(this, arguments);

            this.subscribe(this._button.on('click', function () {
                this._form.showDialog();
            }, this));

            this.subscribe(this.after('valueChange', function () {
                var value = this.get('value');
                if (Framework.isValue(value)) {
                    this._control.set('value', STR.UPLOADED);
                }
            }, this));
        },

        /**
         * @override
         */
        disable: function () {
            OJST.ui.widgets.form.fields.UploadFile.superclass.disable.apply(this, arguments);
            if (this._button) {
                this._button.setAttribute('disabled', 'disabled');
            }
        },

        /**
         * @override
         */
        enable: function () {
            OJST.ui.widgets.form.fields.UploadFile.superclass.enable.apply(this, arguments);
            if (this._button) {
                this._button.removeAttribute('disabled');
            }
        }

    }, {
        ATTRS: {
            value: {
                validator: Y.Lang.isString
            },
            placeholder: {
                validator: Y.Lang.isString,
                writeOnce: 'initOnly'
            },
            uploadUrl: {
                validator: Y.Lang.isString
            }
        }
    });

}, OJST.VERSION, {requires: [
    OJST.ns.widgets.form.Field,
    OJST.ns.utils.Framework,
    'node-event-simulate'
]});