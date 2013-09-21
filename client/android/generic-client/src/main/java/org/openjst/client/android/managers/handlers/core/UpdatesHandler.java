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

package org.openjst.client.android.managers.handlers.core;

import android.app.Application;
import android.widget.Toast;
import org.openjst.client.android.commons.ApplicationContext;

import java.util.Date;

/**
 * @author Sergey Grachev
 */
public final class UpdatesHandler {

    private final Application application;

    public UpdatesHandler(final Application application) {
        this.application = application;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void add(final Long updateId, final String os, final String version, final Date uploadDate, final String description) {
        if ("ANDROID".equals(os)) {
            ApplicationContext.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(application, "add " + updateId + " " + version + " " + uploadDate + " " + description, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void update(final Long updateId, final String os, final String version, final Date uploadDate, final String description) {
        if ("ANDROID".equals(os)) {
            ApplicationContext.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(application, "update " + updateId + " " + version + " " + uploadDate + " " + description, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void remove(final Long updateId, final String os) {
        if ("ANDROID".equals(os)) {
            ApplicationContext.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(application, "remove " + updateId, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
