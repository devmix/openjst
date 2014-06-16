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

package org.openjst.commons.properties.validators;

import org.apache.commons.lang3.StringUtils;
import org.openjst.commons.properties.Caches;
import org.openjst.commons.properties.Property;
import org.openjst.commons.properties.exceptions.PropertyConversionException;
import org.openjst.commons.properties.exceptions.PropertyValidationException;
import org.openjst.commons.properties.restrictions.List;
import org.openjst.commons.properties.restrictions.Number;
import org.openjst.commons.properties.restrictions.Pattern;
import org.openjst.commons.properties.restrictions.Validator;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

import static org.openjst.commons.properties.Property.Type;
import static org.openjst.commons.properties.converters.Converters.basic;

/**
 * @author Sergey Grachev
 */
final class StandardValidator implements Validator {

    @Override
    public void validate(final Property property, @Nullable final Object value) {
        final Annotation[] annotations = Caches.restrictionsOf(property);
        if (annotations.length == 0) {
            return;
        }

        for (final Annotation annotation : annotations) {
            final boolean valid;
            if (annotation instanceof Number.Min) {
                valid = validate(property.type(), value, ((Number.Min) annotation));
            } else if (annotation instanceof Number.Max) {
                valid = validate(property.type(), value, ((Number.Max) annotation));
            } else if (annotation instanceof Number.Range) {
                valid = validate(property.type(), value, ((Number.Range) annotation));
            } else if (annotation instanceof Pattern.String) {
                valid = validate(property.type(), value, ((Pattern.String) annotation));
            } else if (annotation instanceof List.String) {
                valid = validate(property.type(), value, ((List.String) annotation));
            } else if (annotation instanceof List.Number) {
                valid = validate(property.type(), value, ((List.Number) annotation));
            } else if (annotation instanceof List.Enum) {
                valid = validate(property.type(), value, ((List.Enum) annotation));
            } else {
                valid = true;
            }

            if (!valid) {
                throw new PropertyValidationException(property.type(), value, annotation);
            }
        }
    }

    private static boolean validate(final Type type, @Nullable final Object value, final Number.Min restriction) {
        return basic().asDouble(type, value) >= restriction.value();
    }

    private static boolean validate(final Type type, @Nullable final Object value, final Number.Max restriction) {
        return basic().asDouble(type, value) <= restriction.value();
    }

    private static boolean validate(final Type type, @Nullable final Object value, final Number.Range restriction) {
        if (restriction.value().length == 0) {
            return true;
        }

        final double v = basic().asDouble(type, value);
        final double[] values = restriction.value();
        for (int min = 0, max = 1, length = values.length; min < length; min += 2, max += 2) {
            if (v >= values[min] && (max == length || v <= values[max])) {
                return true;
            }
        }

        return false;
    }

    private static boolean validate(final Type type, @Nullable final Object value, final Pattern.String restriction) {
        final String v = basic().asString(type, value);
        //noinspection ConstantConditions
        return StringUtils.isBlank(v) ?
                StringUtils.isBlank(restriction.value()) : Caches.patternOf(restriction).matcher(v).matches();
    }

    private static boolean validate(final Type type, @Nullable final Object value, final List.String restriction) {
        final String v = basic().asString(type, value);
        for (final String item : restriction.value()) {
            if (item.equals(v)) {
                return true;
            }
        }
        return false;
    }

    private boolean validate(final Type type, @Nullable final Object value, final List.Number restriction) {
        final double v = basic().asDouble(type, value);
        for (final double item : restriction.value()) {
            if (item == v) {
                return true;
            }
        }
        return false;
    }

    private boolean validate(final Type type, @Nullable final Object value, final List.Enum restriction) {
        try {
            basic().asEnum(type, value, restriction.value());
        } catch (final PropertyConversionException e) {
            return false;
        }
        return true;
    }
}
