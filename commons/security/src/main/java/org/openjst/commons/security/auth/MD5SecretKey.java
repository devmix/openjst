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

import org.openjst.commons.security.checksum.MD5;

import java.util.Arrays;

/**
 * @author Sergey Grachev
 */
final class MD5SecretKey implements SecretKey {

    private final byte[] key;

    public MD5SecretKey(final byte[] key, final boolean encode) {
        if (encode) {
            this.key = MD5.checksum(key);
        } else {
            this.key = Arrays.copyOf(key, key.length);
        }
    }

    public SecretKeys getType() {
        return SecretKeys.MD5;
    }

    public byte[] get() {
        return key;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MD5SecretKey that = (MD5SecretKey) o;
        return Arrays.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return key != null ? Arrays.hashCode(key) : 0;
    }

    @Override
    public String toString() {
        return "MD5SecretKey{" + Integer.toHexString(hashCode()) + '}';
    }
}
