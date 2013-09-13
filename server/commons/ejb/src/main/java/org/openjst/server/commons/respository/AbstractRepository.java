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

package org.openjst.server.commons.respository;

import org.openjst.server.commons.respository.exceptions.ContentRepositoryException;

import javax.annotation.Resource;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * @author Sergey Grachev
 */
public abstract class AbstractRepository implements ContentRepository {

    @Resource(mappedName = "java:/jcr/openjst")
    private Repository repository;

    protected <R> R execute(final OperationWithResult<R> operation) {
        try {
            final Session session = getSession();
            try {
                final R result = operation.execute(session);
                session.save();
                return result;
            } finally {
                closeSession(session);
            }
        } catch (Exception e) {
            throw new ContentRepositoryException(e);
        }
    }

    protected void execute(final Operation operation) {
        try {
            final Session session = getSession();
            try {
                operation.execute(session);
                session.save();
            } finally {
                closeSession(session);
            }
        } catch (Exception e) {
            throw new ContentRepositoryException(e);
        }
    }

    protected Node makeFoldersPath(Node root, final String... path) throws RepositoryException {
        for (final String folder : path) {
            root = root.hasNode(folder) ? root.getNode(folder) : root.addNode(folder);
        }
        return root;
    }

    private void closeSession(final Session session) {
        if (session != null && session.isLive()) {
            session.logout();
        }
    }

    private Session getSession() throws Exception {
        return repository.login(getWorkspaceName());
    }

    public interface OperationWithResult<R> {
        R execute(Session session) throws Exception;

    }

    public interface Operation {
        void execute(Session session) throws Exception;
    }
}
