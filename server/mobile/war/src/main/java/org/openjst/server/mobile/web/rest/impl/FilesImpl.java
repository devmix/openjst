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

package org.openjst.server.mobile.web.rest.impl;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.openjst.server.commons.managers.FilesManager;
import org.openjst.server.commons.web.interceptors.UIService;
import org.openjst.server.mobile.web.rest.Files;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Sergey Grachev
 */
@UIService
public class FilesImpl implements Files {

    @Inject
    private FilesManager filesManager;

    @Override
    public Response upload(final HttpServletRequest request) throws Exception {
        final FileItemFactory factory = new DiskFileItemFactory();
        final ServletFileUpload upload = new ServletFileUpload(factory);

        final List<FileItem> items = upload.parseRequest(request);
        if (!items.isEmpty()) {
            for (final FileItem item : items) {
                if (!item.isFormField()) {
                    return Response.ok()
                            .entity("OK:" + filesManager.storeTemporaryFile(item.getInputStream(), item.getName())).build();
                }
            }
        }

        return Response.noContent().build();
    }

    @Override
    public Response download(final String id) throws Exception {
        return null;
    }
}
