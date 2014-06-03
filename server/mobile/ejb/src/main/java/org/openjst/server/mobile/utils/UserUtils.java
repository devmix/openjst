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

package org.openjst.server.mobile.utils;

import org.apache.commons.lang3.StringUtils;
import org.openjst.commons.conversion.encodings.Base64;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.dto.tuples.Tuples;
import org.openjst.commons.security.auth.SecretKey;
import org.openjst.commons.security.auth.SecretKeys;

import java.text.ParseException;

/**
 * @author Sergey Grachev
 */
public final class UserUtils {

    private static final SecretKey EMPTY_SECRET_KEY = SecretKeys.NONE.create("".getBytes());

    private UserUtils() {
    }

    public static Pair<String, String> parseAuthId(final String s) throws ParseException {
        if (StringUtils.isBlank(s)) {
            return Tuples.emptyPair();
        }

        final int i = s.indexOf('@');
        if (i == -1) {
            throw new ParseException(s, 0);
        }

        return Tuples.newPair(s.substring(0, i), s.substring(i + 1));
    }

    public static SecretKey parseSecretKey(final String s) {
        if (StringUtils.isBlank(s)) {
            return EMPTY_SECRET_KEY;
        }

        final int i = s.indexOf('|');
        if (i != -1) {
            try {
                return SecretKeys.valueOf(s.substring(0, i)).create(Base64.decodeFast(s.substring(i + 1)));
            } catch (Exception ignore) {
                // return as plain
            }
        }

        return SecretKeys.PLAIN.create(s.getBytes());
    }

    public static String encodePassword(final String password, final SecretKeys encoder, final byte[] salt) {
        final SecretKey key = encoder.encode(password, salt);
        return key.getType().name() + '|' + Base64.encodeToString(key.get(), false);
    }

    public static String encodePassword(final SecretKey key) {
        return key.getType().name() + '|' + Base64.encodeToString(key.get(), false);
    }
}
