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

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
public interface List {

    @Retention(RUNTIME)
    @Target({TYPE, FIELD})
    @Restriction
    @interface String {
        java.lang.String[] value();

        static final class Instance implements String {
            private final java.lang.String[] value;

            public Instance(final java.lang.String... value) {
                this.value = value;
            }

            @Override
            public java.lang.String[] value() {
                return value;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return String.class;
            }
        }
    }

    @Retention(RUNTIME)
    @Target({TYPE, FIELD})
    @Restriction
    @interface Number {
        double[] value();

        static final class Instance implements Number {
            private final double[] value;

            public Instance(final double... value) {
                this.value = value;
            }

            @Override
            public double[] value() {
                return value;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Number.class;
            }
        }
    }

    @Retention(RUNTIME)
    @Target({TYPE, FIELD})
    @Restriction
    @interface Enum {
        Class<? extends java.lang.Enum> value();

        static final class Instance implements Enum {
            private final Class<? extends java.lang.Enum> value;

            public Instance(final Class<? extends java.lang.Enum> value) {
                this.value = value;
            }

            @Override
            public Class<? extends java.lang.Enum> value() {
                return value;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Enum.class;
            }
        }
    }
}
