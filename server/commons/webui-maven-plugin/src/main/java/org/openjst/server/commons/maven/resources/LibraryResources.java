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

import org.openjst.server.commons.maven.manifest.jaxb.LibraryType;
import org.openjst.server.commons.maven.manifest.jaxb.SourcesType;
import org.openjst.server.commons.maven.manifest.jaxb.StylesType;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public class LibraryResources extends Resources<LibraryResource> {

    private final Set<LibraryResource> styleResources = new LinkedHashSet<LibraryResource>();
    private final String name;
    private File root;
    private boolean skipValidate;
    private LibraryResource joinSourcesAs;
    private LibraryResource joinStylesAs;

    public LibraryResources(final File baseDir, final String name) {
        super(baseDir);
        this.name = name;
    }

    @Override
    public void clear() {
        super.clear();
        styleResources.clear();
        root = null;
        joinSourcesAs = null;
        joinStylesAs = null;
    }

    public void scan(final LibraryType library) throws IOException {
        root = new File(baseDir, library.getRoot());
        if (!root.exists()) {
            throw new IOException("Root directory of library not exists " + root);
        }

        skipValidate = library.isSkipValidate();

        readSources(library.getSources());
        readStyles(library.getStyles());

        if (library.getJoinAs() != null) {
            if (!resources.isEmpty()) {
                joinSourcesAs = new LibraryResource(root, library.getJoinAs() + ".js");
            }
            if (!styleResources.isEmpty()) {
                joinStylesAs = new LibraryResource(root, library.getJoinAs() + ".css");
            }
        }
    }

    private void readSources(final SourcesType sources) throws IOException {
        if (sources == null) {
            return;
        }

        final String[] list = getFiles(root, sources.getIncludes(), sources.getExcludes(), true);
        for (final String item : list) {
            resources.add(new LibraryResource(root, item));
        }
    }

    private void readStyles(final StylesType styles) throws IOException {
        if (styles == null) {
            return;
        }

        final String[] list = getFiles(root, styles.getIncludes(), styles.getExcludes(), true);
        for (final String item : list) {
            styleResources.add(new LibraryResource(root, item));
        }
    }

    public boolean isEmpty() {
        return super.isEmpty() && styleResources.isEmpty();
    }

    public Set<LibraryResource> getStyleResources() {
        return styleResources;
    }

    public String getName() {
        return name;
    }

    public File getRoot() {
        return root;
    }

    public boolean isSkipValidate() {
        return skipValidate;
    }

    @Nullable
    public LibraryResource getJoinSourcesAs() {
        return joinSourcesAs;
    }

    @Nullable
    public LibraryResource getJoinStylesAs() {
        return joinStylesAs;
    }
}
