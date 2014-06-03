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
public enum InformationUnits {

    BYTE(1),
    KIBIBYTE(1024),
    MEBIBYTE(1024 * 1024),
    GIBIBYTE(1024 * 1024 * 1024),
    TEBIBYTE((double) 1024 * 1024 * 1024 * 1024),
    PEBIBYTE((double) 1024 * 1024 * 1024 * 1024 * 1024),
    EXBIBYTE((double) 1024 * 1024 * 1024 * 1024 * 1024 * 1024);

    public static final InformationUnits BASE = BYTE;

    private final double value;

    private InformationUnits(final double value) {
        this.value = value;
    }

    public double convert(final double value, final InformationUnits to) {
        return value * this.value / to.value;
    }

    public Pair<Double, InformationUnits> reduce(final double value, final InformationUnits... allowed) {
        if (value < this.value) {
            return Tuples.newPair(value, this);
        }

        InformationUnits to = this;
        final double baseValue = value * this.value / BASE.value;
        for (final InformationUnits unit : allowed.length == 0 ? values() : allowed) {
            if (baseValue >= unit.value && to.value < unit.value) {
                to = unit;
            }
        }

        return Tuples.newPair(baseValue / to.value, to);
    }
}
