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

package org.openjst.commons.properties.exceptions;

import org.openjst.commons.properties.Property;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * @author Sergey Grachev
 */
public final class PropertyValidationException extends PropertyException {

    private static final long serialVersionUID = -6665350566406308337L;

    private final Annotation restriction;
    private final Property.Type type;
    private final Object value;

    public PropertyValidationException(final Property.Type type, @Nullable final Object value, final Annotation restriction) {
        this.type = type;
        this.value = value;
        this.restriction = restriction;
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getRestriction() {
        return (T) restriction;
    }

    public Property.Type getType() {
        return type;
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PropertyValidationException{" +
                "restriction=" + restriction +
                ", type=" + type +
                ", value=" + value +
                '}';
    }
}
