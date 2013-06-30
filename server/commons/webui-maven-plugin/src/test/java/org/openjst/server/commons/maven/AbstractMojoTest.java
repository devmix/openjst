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

import java.io.File;
import java.io.IOException;

/**
 * @author Sergey Grachev
 */
public abstract class AbstractMojoTest {

    public static File makeTempResourcesDirectory() throws IOException {
        final File resourcesPath = new File(AbstractMojoTest.class.getResource("/ui").getPath());
        final File resourcesPathTemp = new File(FileUtils.getTempDirectoryPath(), "web-maven-plugin-test-" + System.nanoTime());
        if (resourcesPathTemp.exists()) {
            FileUtils.deleteDirectory(resourcesPathTemp);
        }

        FileUtils.copyDirectory(resourcesPath, resourcesPathTemp);

        return resourcesPathTemp;
    }
}
