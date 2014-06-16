/*
 * Copyright (C) 2013-2014 OpenJST Project
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

package org.openjst.commons.properties.converters;

import org.joda.time.LocalTime;
import org.openjst.commons.properties.Property;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.openjst.commons.properties.Property.Type.STRING;
import static org.openjst.commons.properties.Property.Type.TIME;

/**
 * @author Sergey Grachev
 */
public final class BasicConverterTest {

    @Test(groups = "unit")
    public void testTime() {
        final Property.Converter converter = Converters.basic();
        assertThat(converter.asString(TIME, new LocalTime(10, 10, 10, 10))).isEqualTo("10:10:10:010");
        assertThat(converter.asString(TIME, new LocalTime(10, 10, 10))).isEqualTo("10:10:10");
        assertThat(converter.asString(TIME, new LocalTime(10, 10))).isEqualTo("10:10");

        assertThat(converter.asTime(STRING, "10:10:10:010")).isEqualTo(new LocalTime(10, 10, 10, 10));
        assertThat(converter.asTime(STRING, "10:10:10")).isEqualTo(new LocalTime(10, 10, 10));
        assertThat(converter.asTime(STRING, "10:10")).isEqualTo(new LocalTime(10, 10));
    }

}
