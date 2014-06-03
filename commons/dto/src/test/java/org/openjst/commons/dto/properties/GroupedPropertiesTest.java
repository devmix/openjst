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

package org.openjst.commons.dto.properties;

import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class GroupedPropertiesTest {

    @Test(groups = "unit")
    @SuppressWarnings("ConstantConditions")
    public void testHashMap() {
        final GroupedProperties<String, String, Integer> g = new HashMapGroupedProperties<String, String, Integer>();
        g
                .ensureGroup("1") // group 1
                .set("1_1", 1)
                .set("1_2", 2)
                .ensureGroup("1_1") // subgroup 1 of first group
                .set("1_1_1", 1)
                .set("1_1_2", 2).back()
                .ensureGroup("1_2") // subgroup 2 of first group
                .set("1_2_1", 1)
                .set("1_2_2", 2).back(2)

                .ensureGroup("2") // group 2
        ;

        assertThat(g.empty()).isTrue();
        assertThat(g.size()).isEqualTo(0);
        assertThat(g.sizeGroups()).isEqualTo(2);
        assertThat(g.group("0")).isNull();

        assertThat(g.containsGroup("1")).isTrue();
        assertThat(g.group("1").empty()).isFalse();
        assertThat(g.group("1").size()).isEqualTo(2);
        assertThat(g.group("1").get("1_1")).isEqualTo(1);
        assertThat(g.group("1").get("1_2")).isEqualTo(2);
        assertThat(g.group("1").group("1_1").size()).isEqualTo(2);
        assertThat(g.group("1").group("1_1").get("1_1_1")).isEqualTo(1);
        assertThat(g.group("1").group("1_1").get("1_1_2")).isEqualTo(2);

        assertThat(g.group("2").empty()).isTrue();
    }
}
