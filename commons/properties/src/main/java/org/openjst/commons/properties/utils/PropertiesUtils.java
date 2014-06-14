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

package org.openjst.commons.properties.utils;

import org.apache.commons.lang3.StringUtils;
import org.openjst.commons.properties.Property;
import org.openjst.commons.properties.annotations.Group;
import org.openjst.commons.properties.annotations.Levels;

/**
 * @author Sergey Grachev
 */
@Levels // default
public final class PropertiesUtils {

    private static final Levels DEFAULT_LEVEL = PropertiesUtils.class.getAnnotation(Levels.class);

    private PropertiesUtils() {
    }

    public static String createKeyByAnnotations(final Property property, final String separator) {
        final Class clazz = property.getClass();

        final String name;
        if (clazz.isEnum()) {
            name = ((Enum) property).name();
        } else {
            name = clazz.getSimpleName();
        }

        final Group group = (Group) clazz.getAnnotation(Group.class);
        if (group != null) {
            final StringBuilder sb = new StringBuilder();
            if (StringUtils.isNotBlank(group.first())) {
                sb.append(group.first());
            }

            if (StringUtils.isNotBlank(group.second())) {
                if (sb.length() > 0) {
                    sb.append(separator);
                }
                sb.append(group.second());
            }

            if (sb.length() > 0) {
                sb.append(separator);
            }

            return sb.append(name).toString().toLowerCase();
        }

        return name.toLowerCase();
    }

    public static String createKeyByAnnotations(final Property property) {
        return createKeyByAnnotations(property, ".");
    }

    public static Levels levelOf(final Property property) {
        final Class clazz = property.getClass();
        Levels level = null;
        if (clazz.isEnum()) {
            try {
                level = clazz.getField(((Enum) property).name()).getAnnotation(Levels.class);
            } catch (final NoSuchFieldException ignore) {
            }
        } else {
            level = (Levels) clazz.getAnnotation(Levels.class);
        }
        return level == null ? DEFAULT_LEVEL : level;
    }

    public static int unsigned(final int level) {
        return (level >= 0 ? 0x80 : -0x80) + (level & 0xFF);
    }
}
