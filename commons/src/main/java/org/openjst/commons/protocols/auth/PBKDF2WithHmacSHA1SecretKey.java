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

package org.openjst.commons.protocols.auth;

import org.openjst.commons.checksum.PBKDF2WithHmacSHA1;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * @author Sergey Grachev
 */
final class PBKDF2WithHmacSHA1SecretKey implements SecretKey {

    public static final int HASH_SIZE = 32;
    public static final int ITERATIONS = 64;

    private final byte[] key;

    public PBKDF2WithHmacSHA1SecretKey(final String key, @Nullable final byte[] salt) {
        this.key = PBKDF2WithHmacSHA1.checksum(key, salt, ITERATIONS, HASH_SIZE);
    }

    public PBKDF2WithHmacSHA1SecretKey(final byte[] key) {
        this.key = Arrays.copyOf(key, key.length);
    }

    @Override
    public SecretKeys getType() {
        return SecretKeys.PBKDF2WithHmacSHA1;
    }

    @Override
    public byte[] get() {
        return key;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PBKDF2WithHmacSHA1SecretKey that = (PBKDF2WithHmacSHA1SecretKey) o;

        return Arrays.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return key != null ? Arrays.hashCode(key) : 0;
    }

    @Override
    public String toString() {
        return "PBKDF2WithHmacSHA1SecretKey{" + Integer.toHexString(hashCode()) + '}';
    }
}
