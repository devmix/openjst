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

package org.openjst.server.mobile.cdi.beans.impl;

import org.apache.commons.io.FileUtils;
import org.openjst.server.mobile.cdi.beans.FilesManager;

import javax.inject.Singleton;
import java.io.*;
import java.util.UUID;

/**
 * @author Sergey Grachev
 */
@Singleton
public class FilesManagerImpl implements FilesManager {

    @Override
    public String storeTemporaryFile(final InputStream inputStream, final String name) throws IOException {
        final String uuid = UUID.randomUUID().toString().replaceAll("\\-", "").toLowerCase();
        final File temporaryFile = new File(FileUtils.getTempDirectoryPath(), uuid);
        FileUtils.copyInputStreamToFile(inputStream, temporaryFile);
        return uuid;
    }

    @Override
    public InputStream getTemporaryFileStream(final String externalId) throws FileNotFoundException {
        final File temporaryFile = new File(FileUtils.getTempDirectoryPath(), externalId);
        return new FileInputStream(temporaryFile);
    }
}
