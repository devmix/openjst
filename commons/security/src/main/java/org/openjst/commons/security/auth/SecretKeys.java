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

package org.openjst.commons.security.auth;

import org.openjst.commons.conversion.encodings.StringCharset;

import javax.annotation.Nullable;
import java.security.SecureRandom;

/**
 * @author Sergey Grachev
 */
public enum SecretKeys {

    NONE(-1) {
        @Override
        public SecretKey encode(final String key) {
            return NoneSecretKey.INSTANCE;
        }

        @Override
        public SecretKey create(final byte[] key) {
            return NoneSecretKey.INSTANCE;
        }
    },

    PLAIN(0) {
        @Override
        public SecretKey encode(final String key) {
            return new PlainSecretKey(StringCharset.asUtf8Bytes(key));
        }

        @Override
        public SecretKey create(final byte[] key) {
            return new PlainSecretKey(key);
        }
    },

    MD5(1) {
        @Override
        public SecretKey encode(final String key) {
            return new MD5SecretKey(StringCharset.asUtf8Bytes(key), true);
        }

        @Override
        public SecretKey create(final byte[] key) {
            return new MD5SecretKey(key, false);
        }
    },

    PBKDF2WithHmacSHA1(2) {
        @Override
        public SecretKey encode(final String key) {
            return new PBKDF2WithHmacSHA1SecretKey(key, null);
        }

        @Override
        public SecretKey encode(final String key, @Nullable final byte[] salt) {
            return new PBKDF2WithHmacSHA1SecretKey(key, salt);
        }

        @Override
        public SecretKey create(final byte[] key) {
            return new PBKDF2WithHmacSHA1SecretKey(key);
        }
    };

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final int id;

    SecretKeys(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public abstract SecretKey encode(String key);

    public SecretKey encode(final String key, @Nullable final byte[] salt) {
        return encode(key);
    }

    public abstract SecretKey create(byte[] key);

    public static SecretKeys valueById(final byte id) {
        switch (id) {
            case -1:
                return NONE;
            case 0:
                return PLAIN;
            case 1:
                return MD5;
        }
        throw new IllegalArgumentException("Unknown type of secret key " + id);
    }

    public static byte[] salt(final int size) {
        final byte[] result = new byte[size];
        SECURE_RANDOM.nextBytes(result);
        return result;
    }
}
