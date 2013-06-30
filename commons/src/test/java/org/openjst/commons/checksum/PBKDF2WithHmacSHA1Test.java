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

package org.openjst.commons.checksum;

import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class PBKDF2WithHmacSHA1Test {

    private static final byte[] SALT = new byte[]{
            66, 81, -84, 110, -8, 15, 117, -14, 19, -46, -94, 112, 7, 66, -95, -41};

    private static final byte[] CORRECT_HASH_WITHOUT_SALT = new byte[]{
            111, -19, -75, -98, 39, 8, 114, -111, 105, 5, 107, -47, -125, -53, 30, -126, -89, -15, 66, -9, -38, -25, 99, 115, 26, -110, -11, -13, 78, -56, -56, -94};

    private static final byte[] CORRECT_HASH_WITH_SALT = new byte[]{
            -110, -112, 54, 21, -104, 97, -75, 17, 36, -44, -20, 65, 104, 127, -85, -76, -36, -100, 98, -50, 97, 64, -111, 48, 3, -73, 20, 119, 19, 28, -117, -40};

    private static final String PASSWORD = "test";

    @Test(groups = "unit")
    public void test() {
        assertThat(PBKDF2WithHmacSHA1.checksum(PASSWORD, PBKDF2WithHmacSHA1.DEFAULT_SALT, 64, 32))
                .isEqualTo(CORRECT_HASH_WITHOUT_SALT);

        assertThat(PBKDF2WithHmacSHA1.checksum(PASSWORD, SALT, 64, 32))
                .isEqualTo(CORRECT_HASH_WITH_SALT);
    }
}
