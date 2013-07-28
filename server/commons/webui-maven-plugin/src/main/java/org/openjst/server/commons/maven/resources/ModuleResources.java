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

import org.openjst.server.commons.maven.manifest.jaxb.ModulesType;
import org.openjst.server.commons.maven.manifest.jaxb.SourcesWithRootType;
import org.openjst.server.commons.maven.manifest.jaxb.StylesWithRootType;
import org.openjst.server.commons.maven.utils.ManifestUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public final class ModuleResources extends Resources<ModuleResource> {

    private final Set<ModuleResource> styleResources = new LinkedHashSet<ModuleResource>();
    private String namespace;
    private File sourcesRoot;
    private File stylesRoot;

    public ModuleResources(final File baseDir) {
        super(baseDir);
    }

    @Override
    public void clear() {
        super.clear();
        styleResources.clear();
        sourcesRoot = null;
        stylesRoot = null;
        namespace = null;
    }

    public void scan(final ModulesType modules) throws IOException {
        clear();

        if (modules == null) {
            return;
        }

        namespace = modules.getNamespace();

        readSources(modules.getSources());
        readStyles(modules.getStyles());
    }

    private void readSources(final SourcesWithRootType sources) throws IOException {
        if (sources == null) {
            return;
        }

        sourcesRoot = new File(baseDir, sources.getRoot());
        if (!sourcesRoot.exists()) {
            throw new IOException("Root directory of sources of modules not exists " + sourcesRoot);
        }

        final String[] list = getFiles(sourcesRoot, sources.getIncludes(), sources.getExcludes(), true);
        for (final String item : list) {
            if (ManifestUtils.isSourceModule(item)) {
                resources.add(new ModuleResource(sourcesRoot, item));
            }
        }
    }

    private void readStyles(final StylesWithRootType styles) throws IOException {
        if (styles == null) {
            return;
        }

        stylesRoot = new File(baseDir, styles.getRoot());
        if (!stylesRoot.exists()) {
            throw new IOException("Root directory of styles of modules not exists " + stylesRoot);
        }

        final String[] list = getFiles(stylesRoot, styles.getIncludes(), styles.getExcludes(), true);
        for (final String item : list) {
            if (ManifestUtils.isStyleModule(item)) {
                styleResources.add(new ModuleResource(stylesRoot, item));
            }
        }
    }

    public Set<ModuleResource> getStyleResources() {
        return styleResources;
    }

    public String getNamespace() {
        return namespace;
    }

    public boolean isEmpty() {
        return super.isEmpty() && styleResources.isEmpty();
    }

    public String getRelativeSourcesRoot() {
        return ManifestUtils.relativePath(baseDir, sourcesRoot);
    }

    public String getRelativeStylesRoot() {
        return ManifestUtils.relativePath(baseDir, stylesRoot);
    }
}
