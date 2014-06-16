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

package org.openjst.commons.properties.restrictions;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Sergey Grachev
 */
public interface Number {

    @Retention(RUNTIME)
    @Target({TYPE, FIELD})
    @Restriction
    @interface Max {
        double value();
    }

    @Retention(RUNTIME)
    @Target({TYPE, FIELD})
    @Restriction
    @interface Min {
        double value();
    }

    /**
     * Examples:<br>
     * [min_1]<br>
     * [min_1, max_1, min_2]<br>
     * [min_1, max_1[,..., min_N, max_N]]
     */
    @Retention(RUNTIME)
    @Target({TYPE, FIELD})
    @Restriction
    @interface Range {
        double[] value();
    }
}
