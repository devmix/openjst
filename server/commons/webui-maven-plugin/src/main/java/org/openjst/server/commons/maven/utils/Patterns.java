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

import java.util.regex.Pattern;

/**
 * @author Sergey Grachev
 */
public final class Patterns {

    public static final String STRING_EXT_REPLACE = "\\.\\w{2,3}$";
    public static final Pattern COMPRESSED = Pattern.compile(".*[-\\.]?min\\.\\w{2,3}");
    public static final Pattern JS_MODULE_FILE_NAME = Pattern.compile("(/?[a-z\\d](?:-[a-z\\d])*[a-z\\d]*(?:[\\.js])?)*");
    public static final Pattern CSS_MODULE_FILE_NAME = Pattern.compile("(/?[a-z\\d](?:-[a-z\\d])*[a-z\\d]*(?:[\\.css])?)*");
    public static final Pattern MODULE_REQUIRES = Pattern.compile(
            ".*,\\s*OJST\\.VERSION\\s*,\\s*\\{\\s*requires\\s*:\\s*\\[\\s*(.*)\\s*\\]\\s*\\}.*$",
            Pattern.DOTALL + Pattern.MULTILINE + Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
    public static final Pattern MODULE_REQUIRES_SPLITTER = Pattern.compile(",(?=([^'\"]*['\"][^'\"]*['\"])*[^'\"]*$)");

    private Patterns() {
    }
}
