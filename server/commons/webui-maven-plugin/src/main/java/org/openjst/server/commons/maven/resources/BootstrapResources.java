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

import org.jetbrains.annotations.Nullable;
import org.openjst.server.commons.maven.manifest.jaxb.CoreType;
import org.openjst.server.commons.maven.manifest.jaxb.ExcludesType;
import org.openjst.server.commons.maven.manifest.jaxb.IncludesType;
import org.openjst.server.commons.maven.manifest.jaxb.SourcesType;

import java.io.File;
import java.io.IOException;

/**
 * @author Sergey Grachev
 */
public final class BootstrapResources extends Resources<BootstrapResource> {

    private File root;
    private BootstrapResource entry;
    private BootstrapResource joinAs;

    public BootstrapResources(final File baseDir) {
        super(baseDir);
    }

    @Override
    public void clear() {
        super.clear();
        root = null;
        entry = null;
        joinAs = null;
    }

    public void scan(final CoreType bootstrap) throws IOException {
        clear();

        if (bootstrap == null) {
            return;
        }

        root = new File(baseDir, bootstrap.getRoot());
        if (!root.exists()) {
            throw new IOException("Root directory of bootstrap sources not exists " + root);
        }

        entry = new BootstrapResource(root, bootstrap.getEntry());
        if (!entry.exists()) {
            throw new IOException("Bootstrap entry not exists " + entry);
        }

        if (bootstrap.getJoinAs() != null) {
            joinAs = new BootstrapResource(root, bootstrap.getJoinAs() + ".js");
        }

        readSources(bootstrap.getSources());
    }

    @Nullable
    public BootstrapResource getJoinAs() {
        return joinAs;
    }

    private void readSources(@Nullable final SourcesType code) {
        final IncludesType includes;
        final ExcludesType excludes;
        if (code != null) {
            includes = code.getIncludes();
            excludes = code.getExcludes();
        } else {
            includes = null;
            excludes = null;
        }

        final String entryName = entry.getRelativePath();
        final String[] list = getFiles(root, includes, excludes, true);
        for (final String item : list) {
            if (!entryName.equals(item)) {
                resources.add(new BootstrapResource(root, item));
            }
        }
        resources.add(entry);
    }
}
