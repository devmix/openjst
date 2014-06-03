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

package org.openjst.commons.security.checksum;

import javax.annotation.Nullable;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * @author Sergey Grachev
 */
public final class PBKDF2WithHmacSHA1 {

    public static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final byte[] DEFAULT_SALT = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};

    private PBKDF2WithHmacSHA1() {
    }

    public static byte[] checksum(final String s, @Nullable final byte[] salt, final int iterations, final int hashSize) {
        final PBEKeySpec spec = new PBEKeySpec(
                s.toCharArray(), salt == null ? DEFAULT_SALT : salt, iterations, hashSize * 8);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
