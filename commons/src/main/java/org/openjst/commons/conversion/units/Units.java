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

/**
 * See  <a href="http://en.wikipedia.org/wiki/Units_of_measurement">Units of measurement</a>,
 * <a href="http://ru.wikipedia.org/wiki/Единицы_измерения">Единицы измерения</a>,
 *
 * @author Sergey Grachev
 */
public final class Units {

    private Units() {
    }

    public static double convert(final double value, final TimeUnits from, final TimeUnits to) {
        return from.convert(value, to);
    }

    public static double convert(final double value, final LengthUnits from, final LengthUnits to) {
        return from.convert(value, to);
    }

    public static double convert(final double value, final MassUnits from, final MassUnits to) {
        return from.convert(value, to);
    }

    public static double convert(final double value, final InformationUnits from, final InformationUnits to) {
        return from.convert(value, to);
    }

    public static Pair<Double, TimeUnits> reduce(final double value, final TimeUnits unit, final TimeUnits... allowed) {
        return unit.reduce(value, allowed);
    }

    public static Pair<Double, InformationUnits> reduce(final double value, final InformationUnits unit, final InformationUnits... allowed) {
        return unit.reduce(value, allowed);
    }

    public static Pair<Double, MassUnits> reduce(final double value, final MassUnits unit, final MassUnits... allowed) {
        return unit.reduce(value, allowed);
    }

    public static Pair<Double, LengthUnits> reduce(final double value, final LengthUnits unit, final LengthUnits... allowed) {
        return unit.reduce(value, allowed);
    }
}
