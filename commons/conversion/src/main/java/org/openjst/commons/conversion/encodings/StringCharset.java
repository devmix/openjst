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

package org.openjst.commons.conversion.encodings;

import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;

/**
 * @author Sergey Grachev
 */
public final class StringCharset {

    private StringCharset() {
    }

    @Nullable
    public static byte[] asUtf8Bytes(@Nullable final String s) {
        if (s == null) {
            return null;
        }

        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException("Can't convert string to UTF-8 presentation");
        }
    }
}
