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

import org.codehaus.plexus.util.DirectoryScanner;
import org.openjst.server.commons.maven.manifest.jaxb.ExcludesType;
import org.openjst.server.commons.maven.manifest.jaxb.IncludesType;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

/**
 * @author Sergey Grachev
 */
abstract class Resources<T> {

    protected final Set<T> resources = new LinkedHashSet<T>();
    protected final File baseDir;

    public Resources(final File baseDir) {
        this.baseDir = baseDir;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void clear() {
        resources.clear();
    }

    protected String[] getFiles(final File source, @Nullable final IncludesType includes, @Nullable final ExcludesType excludes, final boolean excludeCompressed) {
        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(source);
        scanner.addDefaultExcludes();

        final List<String> includesList = includes != null ? includes.getInclude() : new ArrayList<String>(0);
        if (includesList.isEmpty()) {
            includesList.add("**/**");
        }

        final List<String> excludesList = excludes != null ? excludes.getExclude() : new ArrayList<String>(1);
        if (excludeCompressed) {
            excludesList.add("**/*-min.*");
        }
        if (!excludesList.isEmpty()) {
            scanner.setExcludes(excludesList.toArray(new String[excludesList.size()]));
        }

        final Set<String> result = new LinkedHashSet<String>(includesList.size());
        for (final String include : includesList) {
            scanner.setIncludes(new String[]{include});
            scanner.scan();
            Collections.addAll(result, scanner.getIncludedFiles());
        }

        return result.toArray(new String[result.size()]);
    }

    public Set<T> getResources() {
        return resources;
    }

    public boolean isEmpty() {
        return resources.isEmpty();
    }
}
