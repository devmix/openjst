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

import org.codehaus.plexus.util.FileUtils;
import org.openjst.commons.dto.constants.Constants;
import org.openjst.server.commons.maven.utils.ManifestUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;

/**
 * @author Sergey Grachev
 */
public abstract class Resource {

    protected File baseDir;
    protected File file;
    protected File fileCompressed;
    protected SoftReference<String> content;
    protected SoftReference<String> contentCompressed;

    public Resource(final File baseDir, final String file) {
        this.baseDir = baseDir;
        this.file = new File(baseDir, file);
        this.fileCompressed = new File(baseDir, ManifestUtils.compressedFile(file));
    }

    public boolean exists() {
        return file.exists();
    }

    public String getRelativePath() {
        return ManifestUtils.relativePath(baseDir, file);
    }

    public String getCompressedRelativePath() {
        return ManifestUtils.relativePath(baseDir, fileCompressed);
    }

    public File getCompressedFile() {
        return fileCompressed;
    }

    public File getFile() {
        return file;
    }

    public String getContent() throws IOException {
        String s = content != null ? content.get() : null;
        if (s == null) {
            s = FileUtils.fileRead(file);
            content = new SoftReference<String>(s);
        }
        return s;
    }

    public boolean isCompressed() {
        return fileCompressed.exists();
    }

    public void writeCompressed(final String content) throws IOException {
        FileUtils.fileWrite(fileCompressed, content);
        contentCompressed = new SoftReference<String>(content);
    }

    public String getCompressedContent() throws IOException {
        if (!fileCompressed.exists()) {
            return Constants.stringsEmpty();
        }

        String s = contentCompressed != null ? contentCompressed.get() : null;
        if (s == null) {
            s = FileUtils.fileRead(fileCompressed);
            contentCompressed = new SoftReference<String>(s);
        }

        return s;
    }

    public File getBaseDir() {
        return baseDir;
    }
}
