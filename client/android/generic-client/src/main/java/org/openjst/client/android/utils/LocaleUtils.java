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

package org.openjst.client.android.utils;

import android.content.Context;
import org.openjst.client.android.R;
import org.openjst.client.android.commons.ApplicationContext;
import org.openjst.commons.conversion.units.InformationUnits;
import org.openjst.commons.conversion.units.Units;
import org.openjst.commons.dto.tuples.Pair;

/**
 * @author Sergey Grachev
 */
public final class LocaleUtils {

    private LocaleUtils() {
    }

    public static String unit(final Pair<Double, InformationUnits> value) {
        return String.format("%.2f %s", value.first(), unitAsString(value.second()));
    }

    public static String unitCut(final double value, final InformationUnits units) {
        return unit(Units.reduce(value, units));
    }

    public static String unitAsString(final InformationUnits unit) {
        final Context context = ApplicationContext.getApplication();
        switch (unit) {
            case BYTE:
                return context.getString(R.string.units_information_byte);
            case KIBIBYTE:
                return context.getString(R.string.units_information_kib);
            case MEBIBYTE:
                return context.getString(R.string.units_information_mib);
            case GIBIBYTE:
                return context.getString(R.string.units_information_gib);
            case TEBIBYTE:
                return context.getString(R.string.units_information_tib);
            case PEBIBYTE:
                return context.getString(R.string.units_information_pib);
            case EXBIBYTE:
                return context.getString(R.string.units_information_eib);
        }
        return "";
    }
}
