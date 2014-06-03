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
import org.openjst.commons.dto.tuples.Tuples;

/**
 * @author Sergey Grachev
 */
public enum MassUnits {

    // metric

    MILLIGRAM(0.0000001),
    GRAM(0.001),
    KILOGRAM(1), // BASE
    CENTNER(45.359237),
    TONNE(1000),

    // imperial

    POUND(0.45359237);

    public static final MassUnits BASE = KILOGRAM;

    private final double value;

    private MassUnits(final double value) {
        this.value = value;
    }

    public double convert(final double value, final MassUnits to) {
        return value * this.value / to.value;
    }

    public Pair<Double, MassUnits> reduce(final double value, final MassUnits... allowed) {
        if (value < this.value) {
            return Tuples.newPair(value, this);
        }

        MassUnits to = this;
        final double baseValue = value * this.value / BASE.value;
        for (final MassUnits unit : allowed.length == 0 ? values() : allowed) {
            if (baseValue >= unit.value && to.value < unit.value) {
                to = unit;
            }
        }

        return Tuples.newPair(baseValue / to.value, to);
    }
}
