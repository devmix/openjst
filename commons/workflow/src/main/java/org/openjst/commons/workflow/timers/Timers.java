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

package org.openjst.commons.workflow.timers;

import org.openjst.commons.conversion.units.TimeUnits;
import org.openjst.commons.conversion.units.Units;

/**
 * @author Sergey Grachev
 */
public enum Timers {
    ONE_TIME,
    RECURRENT;

    private Timers() {
    }

    public static Timer newOneTime(final String name, final long interval, final TimeUnits units) {
        return new GenericTimer(name, ONE_TIME, System.currentTimeMillis(), (long) (Units.convert(interval, units, TimeUnits.MILLISECOND)));
    }

    public static Timer newRecurrent(final String name, final long interval, final TimeUnits units) {
        return new GenericTimer(name, RECURRENT, System.currentTimeMillis(), (long) Units.convert(interval, units, TimeUnits.MILLISECOND));
    }

    public static Timer newDueTime(final String name, final long time, final TimeUnits units) {
        final long now = System.currentTimeMillis();
        final long interval = (long) (Units.convert(time, units, TimeUnits.MILLISECOND) - now);
        return new GenericTimer(name, ONE_TIME, now, interval < 0 ? 0 : interval);
    }
}
