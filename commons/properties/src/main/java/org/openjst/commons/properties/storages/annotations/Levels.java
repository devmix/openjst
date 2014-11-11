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

package org.openjst.commons.properties.storages.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Sergey Grachev
 */
@Retention(RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Levels {

    int min() default 0;

    int max() default 0xFF;

    @SuppressWarnings("ClassExplicitlyAnnotation")
    static final class Instance implements Levels {

        private final int min;
        private final int max;

        public Instance(final int min, final int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public int min() {
            return min;
        }

        @Override
        public int max() {
            return max;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Levels.class;
        }
    }
}