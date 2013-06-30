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

package org.openjst.commons.conversion.units;

import org.openjst.commons.dto.tuples.Pair;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class UnitsTest {

    @Test(groups = "unit")
    public void testTime() {
        // TODO SG
    }

    @Test(groups = "unit")
    public void testTimeReduce() {
        assertReduce(2, TimeUnits.MINUTE, 120, TimeUnits.SECOND,
                TimeUnits.SECOND, TimeUnits.MILLISECOND, TimeUnits.MINUTE);

        assertReduce(59, TimeUnits.SECOND, 59, TimeUnits.SECOND,
                TimeUnits.SECOND, TimeUnits.MILLISECOND, TimeUnits.MINUTE);
    }

    @Test(groups = "unit")
    public void testLength() {
        // TODO SG
    }

    @Test(groups = "unit")
    public void testLengthReduce() {
        final Pair<Double, LengthUnits> pair = LengthUnits.METER.reduce(
                1000, LengthUnits.METER, LengthUnits.MILLIMETER, LengthUnits.KILOMETER);
        assertThat(pair.first()).isEqualTo(1);
        assertThat(pair.second()).isEqualTo(LengthUnits.KILOMETER);
        // TODO SG
    }

    @Test(groups = "unit")
    public void testMass() {
        // TODO SG
    }

    @Test(groups = "unit")
    public void testInformationConvert() {
        assertThat(Units.convert(512, InformationUnits.BYTE, InformationUnits.KIBIBYTE)).isEqualTo(0.5);
        assertThat(Units.convert(1, InformationUnits.KIBIBYTE, InformationUnits.BYTE)).isEqualTo(1024);
        assertThat(Units.convert(1, InformationUnits.MEBIBYTE, InformationUnits.BYTE)).isEqualTo(1048576);
        // TODO SG
    }

    @Test(groups = "unit")
    public void testInformationReduce() {
        assertReduce(1, InformationUnits.MEBIBYTE, 1024, InformationUnits.KIBIBYTE,
                InformationUnits.KIBIBYTE, InformationUnits.BYTE, InformationUnits.MEBIBYTE);

        assertReduce(1023, InformationUnits.KIBIBYTE, 1023, InformationUnits.KIBIBYTE,
                InformationUnits.KIBIBYTE, InformationUnits.BYTE, InformationUnits.MEBIBYTE);

        assertReduce(1, InformationUnits.MEBIBYTE, 1024 * 1024, InformationUnits.BYTE,
                InformationUnits.KIBIBYTE, InformationUnits.BYTE, InformationUnits.MEBIBYTE, InformationUnits.GIBIBYTE);

        assertReduce(1, InformationUnits.GIBIBYTE, 1024 * 1024, InformationUnits.KIBIBYTE,
                InformationUnits.KIBIBYTE, InformationUnits.BYTE, InformationUnits.MEBIBYTE, InformationUnits.GIBIBYTE, InformationUnits.TEBIBYTE);

        assertReduce(1, InformationUnits.GIBIBYTE, 1024 * 1024 * 1024, InformationUnits.BYTE,
                InformationUnits.KIBIBYTE, InformationUnits.BYTE, InformationUnits.MEBIBYTE, InformationUnits.GIBIBYTE);
    }

    private void assertReduce(final int expectedValue, final InformationUnits expectedUnit,
                              final int value, final InformationUnits unit, final InformationUnits... allowed) {
        final Pair<Double, InformationUnits> pair = unit.reduce(value, allowed);
        assertThat(pair.first()).isEqualTo(expectedValue);
        assertThat(pair.second()).isEqualTo(expectedUnit);
    }

    private static void assertReduce(final int expectedValue, final TimeUnits expectedUnit,
                                     final int value, final TimeUnits unit, final TimeUnits... allowed) {
        final Pair<Double, TimeUnits> pair = unit.reduce(value, allowed);
        assertThat(pair.first()).isEqualTo(expectedValue);
        assertThat(pair.second()).isEqualTo(expectedUnit);
    }
}
