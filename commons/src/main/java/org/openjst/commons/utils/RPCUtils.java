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

package org.openjst.commons.utils;

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.dto.tuples.Tuples;

import java.util.regex.Pattern;

/**
 * @author Sergey Grachev
 */
public final class RPCUtils {

    private static final Pattern METHOD_PATTERN = Pattern.compile("([a-zA-Z_]+\\w*\\.[a-zA-Z_]+\\w*)||([a-zA-Z_]+\\w*)");

    private RPCUtils() {
    }

    public static boolean checkMethodName(final String methodName) {
        return METHOD_PATTERN.matcher(methodName).matches();
    }

    public static String makeMethodName(final String objectName, final String methodName) {
        return objectName == null ? methodName : objectName + '.' + methodName;
    }

    @Nullable
    public static Pair<String, String> parseMethod(final String value) {
        final String name = value.trim();
        if (!RPCUtils.checkMethodName(name)) {
            throw null;
        }
        final String[] methodParts = name.split("\\.");
        if (methodParts.length > 1) {
            return Tuples.newPair(methodParts[0], methodParts[1]);
        } else {
            return Tuples.newPair(null, methodParts[0]);
        }
    }
}

