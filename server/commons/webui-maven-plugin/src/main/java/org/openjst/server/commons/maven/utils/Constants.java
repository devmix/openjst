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

package org.openjst.server.commons.maven.utils;

/**
 * @author Sergey Grachev
 */
public final class Constants {

    public static final String LINE_SEPARATOR = "--------------------------------------------------------------------------------";

    public static final String MIN_JS = "-min.js";
    public static final String MIN_CSS = "-min.css";

    public static final String CASCADING_STYLE_SHEETS = "Cascading Style Sheets";
    public static final String JAVA_SCRIPT = "JavaScript";

    public static final String ERROR_CANT_DELETE_OLD_FILE = "Can't delete old file %s";
    public static final String ERROR_CANT_RENAME_FILE_TO = "Can't rename file %s to %s";
    public static final String ERROR_CANT_CREATE_DIRECTORY = "Can't create directory %s";
    public static final String ERROR_CANT_WRITE_FILE = "Can't write file %s";
    public static final String ERROR_CANT_READ_FILE = "Can't read file %s";
    public static final String ERROR_CANT_CREATE_COMPRESSOR = "Can't create compressor";
    public static final String ERROR_CANT_CREATE_VALIDATOR = "Can't create validator";
    public static final String ERROR_CANT_CREATE_CSS_COMPRESSOR = "Can't create CSS compressor";
    public static final String ERROR_CANT_COMPRESS_FILE = "Can't compress file %s";
    public static final String ERROR_CANT_BUILD_MANIFEST = "Can't build manifest";
    public static final String ERROR_CANT_GET_DEPENDENCIES_FROM = "Can't get dependencies from '%s'";
    public static final String ERROR_INCORRECT_MODULE_NAME = "Incorrect module name '%s'";
    public static final String ERROR_STOPPING_BECAUSE_THERE_IS_AN_ERROR_AND_CAN_T_BE_IGNORED = "Stopping because there is an error and can't be ignored";

    public static final String MSG_DO_VALIDATE = "Validate JavaScript: %s";
    public static final String MSG_DO_COMPRESS = "Compress %s: %s";
    public static final String MSG_DO_RENAME = "Rename %s: %s";
    public static final String MSG_DO_SEARCH_MODULES = "Search %s modules: %s";
    public static final String MSG_SKIP_UNSUPPORTED_RESOURCE = "Skip unsupported resource: %s";
    public static final String MSG_DEPENDENCIES = "\t\t dependencies: %s";
    public static final String MSG_MODULE = "\t%s (%s)";

    private Constants() {
    }
}
