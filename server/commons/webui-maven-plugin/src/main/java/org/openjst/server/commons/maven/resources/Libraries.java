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

package org.openjst.server.commons.maven.resources;

import org.openjst.server.commons.maven.manifest.jaxb.LibrariesType;
import org.openjst.server.commons.maven.manifest.jaxb.LibraryType;

import java.io.File;
import java.io.IOException;

/**
 * @author Sergey Grachev
 */
public final class Libraries extends Resources<LibraryResources> {

    private File root;
    private String namespace;

    public Libraries(final File baseDir) {
        super(baseDir);
    }

    @Override
    public void clear() {
        super.clear();
        root = null;
        namespace = null;
    }

    public void scan(final LibrariesType libraries) throws IOException {
        clear();

        if (libraries == null) {
            return;
        }

        namespace = libraries.getNamespace();

        root = new File(baseDir, libraries.getRoot());
        if (!root.exists()) {
            throw new IOException("Root directory of libraries not exists " + root);
        }

        for (final LibraryType item : libraries.getLibrary()) {
            final LibraryResources library = new LibraryResources(root, item.getName());
            library.scan(item);
            resources.add(library);
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public File getRoot() {
        return root;
    }
}
