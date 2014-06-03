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

import java.util.Arrays;

/**
 * @author Sergey Grachev
 */
final class PlainSecretKey implements SecretKey {

    private final byte[] key;

    public PlainSecretKey(final byte[] key) {
        this.key = Arrays.copyOf(key, key.length);
    }

    public SecretKeys getType() {
        return SecretKeys.PLAIN;
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
        final PlainSecretKey that = (PlainSecretKey) o;
        return Arrays.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return key != null ? Arrays.hashCode(key) : 0;
    }

    @Override
    public String toString() {
        return "PlainSecretKey{" + Integer.toHexString(hashCode()) + '}';
    }
}
