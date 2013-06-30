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

package org.openjst.server.commons.maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * @author Sergey Grachev
 */
public final class BuildMojoTest extends AbstractMojoTest {

    @SuppressWarnings("unchecked")
    @Test(groups = "manual")
    public void testFullLifecycle() throws IOException, MojoFailureException, MojoExecutionException {
        final File resourcesPath = makeTempResourcesDirectory();
        final BuildMojo mojo = new BuildMojo("UTF-8", resourcesPath, resourcesPath, true, null, null, false, false,
                Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap());
        try {
            mojo.execute();
        } finally {
            FileUtils.deleteDirectory(resourcesPath);
        }
    }
}
