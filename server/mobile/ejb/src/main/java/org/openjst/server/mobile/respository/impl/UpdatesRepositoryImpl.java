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

import org.openjst.server.commons.respository.AbstractRepository;
import org.openjst.server.mobile.Environment;
import org.openjst.server.mobile.cdi.beans.FilesManager;
import org.openjst.server.mobile.respository.UpdatesRepository;

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
@Stateless(name = UpdatesRepositoryImpl.NAME)
public class UpdatesRepositoryImpl extends AbstractRepository implements UpdatesRepository {

    public static final String NAME = "UpdatesRepository";

    @Inject
    private FilesManager filesManager;

    @Override
    public String getWorkspaceName() {
        return Environment.Repository.DEFAULT;
    }

    @Override
    public boolean isExists() {
        System.out.println("isExists");
        return execute(new OperationWithResult<Boolean>() {
            @Override
            public Boolean execute(final Session session) throws Exception {
                return session.getRootNode().hasNode("client/updates");
            }
        });
    }

    @Override
    public void initialize() {
        System.out.println("initialize");
        execute(new Operation() {
            @Override
            public void execute(final Session session) throws Exception {
                makeFoldersPath(session.getRootNode(), "client", "updates");
            }
        });
    }

    @Override
    public void store(final Long accountId, final Long updateId, final String uploadId) {
        execute(new Operation() {
            @Override
            public void execute(final Session session) throws Exception {
                final InputStream stream = filesManager.getTemporaryFileStream(uploadId);

                final Node folder = makeFoldersPath(session.getNode("/client/updates"),
                        "android", String.valueOf(accountId));

                final String fileName = String.valueOf(updateId);
                final Node file = folder.hasNode(fileName) ? folder.getNode(fileName) : folder.addNode(fileName, "nt:file");

                final Node contentNode = file.hasNode("jcr:content")
                        ? file.getNode("jcr:content") : file.addNode("jcr:content", "nt:resource");
                final Binary binary = session.getValueFactory().createBinary(stream);
                contentNode.setProperty("jcr:data", binary);
                contentNode.setProperty("jcr:mimeType", "application/octet-stream");
                contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
                // TODO checksum
            }
        });
    }

    @Override
    public void remove(final Long accountId, final Long updateId) {
        execute(new Operation() {
            @Override
            public void execute(final Session session) throws Exception {
                final String path = "/client/updates/android/" + accountId + "/" + updateId;
                if (session.nodeExists(path)) {
                    session.removeItem(path);
                }
            }
        });
    }
}
