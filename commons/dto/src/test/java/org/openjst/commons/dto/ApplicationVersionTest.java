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

package org.openjst.commons.dto;

import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class ApplicationVersionTest {

    @Test(groups = "unit")
    public void testParse() {
        final ApplicationVersion version = ApplicationVersion.parse("1.2.3");
        assertThat(version.getMajor()).isEqualTo(1);
        assertThat(version.getMinor()).isEqualTo(2);
        assertThat(version.getBuild()).isEqualTo(3);
    }

    @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
    public void testParseIllegalArgumentException() {
        ApplicationVersion.parse(".2.3");
    }

    @Test(groups = "unit", expectedExceptions = NumberFormatException.class)
    public void testParseNumberFormatException() {
        ApplicationVersion.parse("1.d.3");
    }

    @Test(groups = "unit")
    public void testGreater() {
        assertThat(new ApplicationVersion(3, 4, 5).isGreater(new ApplicationVersion(3, 4, 5))).isFalse();
        assertThat(new ApplicationVersion(3, 4, 5).isGreater(new ApplicationVersion(2, 4, 5))).isTrue();
        assertThat(new ApplicationVersion(3, 4, 5).isGreater(new ApplicationVersion(3, 3, 5))).isTrue();
        assertThat(new ApplicationVersion(3, 4, 5).isGreater(new ApplicationVersion(3, 4, 4))).isTrue();
    }

    @Test(groups = "unit")
    public void testLess() {
        assertThat(new ApplicationVersion(3, 4, 5).isLess(new ApplicationVersion(3, 4, 5))).isFalse();
        assertThat(new ApplicationVersion(2, 4, 5).isLess(new ApplicationVersion(3, 4, 5))).isTrue();
        assertThat(new ApplicationVersion(3, 3, 5).isLess(new ApplicationVersion(3, 4, 5))).isTrue();
        assertThat(new ApplicationVersion(3, 4, 4).isLess(new ApplicationVersion(3, 4, 5))).isTrue();
    }

    @Test(groups = "unit")
    public void testEquals() {
        assertThat(new ApplicationVersion(3, 4, 5).isEquals(new ApplicationVersion(3, 4, 5))).isTrue();
        assertThat(new ApplicationVersion(2, 4, 5).isEquals(new ApplicationVersion(3, 4, 5))).isFalse();
    }
}
