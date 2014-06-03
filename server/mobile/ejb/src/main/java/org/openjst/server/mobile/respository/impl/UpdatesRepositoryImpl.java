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

package org.openjst.server.mobile.respository.impl;

import org.openjst.server.commons.managers.FilesManager;
import org.openjst.server.commons.model.types.MobileClientOS;
import org.openjst.server.commons.respository.AbstractRepository;
import org.openjst.server.mobile.Environment;
import org.openjst.server.mobile.respository.UpdatesRepository;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Session;
import java.io.InputStream;
import java.util.Calendar;

/**
 * @author Sergey Grachev
 */
@Stateless
@PermitAll
public class UpdatesRepositoryImpl extends AbstractRepository implements UpdatesRepository {

    @Inject
    private FilesManager filesManager;

    @Override
    public String getWorkspaceName() {
        return Environment.Repository.DEFAULT;
    }

    @Override
    public boolean isExists() {
        return execute((Session session) -> session.getRootNode().hasNode("client/updates"));
    }

    @Override
    public void initialize() {
        execute((Session session) -> makeFoldersPath(session.getRootNode(), "client", "updates"));
    }

    @Override
    public void store(final Long accountId, final Long updateId, final String uploadId, final MobileClientOS os) {
        execute((Operation) session -> {
            final InputStream stream = filesManager.getTemporaryFileStream(uploadId);

            final Node folder = makeFoldersPath(session.getNode("/client/updates"),
                    os.name().toLowerCase(), String.valueOf(accountId));

            final String fileName = String.valueOf(updateId);
            final Node file = folder.hasNode(fileName) ? folder.getNode(fileName) : folder.addNode(fileName, "nt:file");

            final Node contentNode = file.hasNode("jcr:content")
                    ? file.getNode("jcr:content") : file.addNode("jcr:content", "nt:resource");
            final Binary binary = session.getValueFactory().createBinary(stream);
            contentNode.setProperty("jcr:data", binary);
            contentNode.setProperty("jcr:mimeType", "application/octet-stream");
            contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
            // TODO checksum
        });
    }

    @Override
    public void remove(final Long accountId, final Long updateId, final MobileClientOS os) {
        execute((Session session) -> {
            final String path = "/client/updates/" + os.name().toLowerCase() + "/" + accountId + "/" + updateId;
            if (session.nodeExists(path)) {
                session.removeItem(path);
            }
        });
    }
}
