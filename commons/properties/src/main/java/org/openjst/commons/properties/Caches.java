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

package org.openjst.commons.properties;

import org.openjst.commons.properties.annotations.Levels;
import org.openjst.commons.properties.restrictions.Number;
import org.openjst.commons.properties.restrictions.Pattern;
import org.openjst.commons.properties.restrictions.Restriction;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Internal caches for properties
 * TODO SoftReference
 *
 * @author Sergey Grachev
 */
@Levels // default
@Number.Min(1)
public final class Caches {

    private static final Levels DEFAULT_LEVEL = Caches.class.getAnnotation(Levels.class);
    private static final Annotation[] DEFAULT_RESTRICTIONS = new Annotation[0];

    private static final WeakHashMap<Class, Levels> CACHE_LEVELS = new WeakHashMap<Class, Levels>();
    private static final WeakHashMap<Class, Map<String, Levels>> CACHE_ENUM_LEVELS = new WeakHashMap<Class, Map<String, Levels>>();

    private static final WeakHashMap<Class, Annotation[]> CACHE_RESTRICTIONS = new WeakHashMap<Class, Annotation[]>();
    private static final WeakHashMap<Class, Map<String, Annotation[]>> CACHE_ENUM_RESTRICTIONS = new WeakHashMap<Class, Map<String, Annotation[]>>();

    private static final WeakHashMap<Annotation, java.util.regex.Pattern> CACHE_PATTERNS = new WeakHashMap<Annotation, java.util.regex.Pattern>();

    private Caches() {
    }

    public static java.util.regex.Pattern patternOf(final Pattern.String restriction) {
        final java.util.regex.Pattern pattern;
        synchronized (CACHE_PATTERNS) {
            if (CACHE_PATTERNS.containsKey(restriction)) {
                pattern = CACHE_PATTERNS.get(restriction);
            } else {
                pattern = java.util.regex.Pattern.compile(restriction.value());
                CACHE_PATTERNS.put(restriction, pattern);
            }
        }
        return pattern;
    }

    public static Levels levelOf(final Property property) {
        final Class clazz = property.getClass();
        Levels level = null;
        if (clazz.isEnum()) {
            try {
                final String name = ((Enum) property).name();
                synchronized (CACHE_ENUM_LEVELS) {
                    final Map<String, Levels> enumLevels;
                    if (CACHE_ENUM_LEVELS.containsKey(clazz)) {
                        enumLevels = CACHE_ENUM_LEVELS.get(clazz);
                    } else {
                        enumLevels = new HashMap<String, Levels>(1);
                        CACHE_ENUM_LEVELS.put(clazz, enumLevels);
                    }

                    if (enumLevels.containsKey(name)) {
                        level = enumLevels.get(name);
                    } else {
                        level = clazz.getField(name).getAnnotation(Levels.class);
                        enumLevels.put(name, level);
                    }
                }
            } catch (final NoSuchFieldException ignore) {
            }
        } else {
            synchronized (CACHE_LEVELS) {
                if (CACHE_LEVELS.containsKey(clazz)) {
                    level = CACHE_LEVELS.get(clazz);
                } else {
                    level = (Levels) clazz.getAnnotation(Levels.class);
                    CACHE_LEVELS.put(clazz, level);
                }
            }
        }
        return level == null ? DEFAULT_LEVEL : level;
    }

    public static Annotation[] restrictionsOf(final Property property) {
        final Class clazz = property.getClass();
        Annotation[] result = null;
        if (clazz.isEnum()) {
            try {
                final String name = ((Enum) property).name();
                synchronized (CACHE_ENUM_RESTRICTIONS) {
                    final Map<String, Annotation[]> enumLevels;
                    if (CACHE_ENUM_RESTRICTIONS.containsKey(clazz)) {
                        enumLevels = CACHE_ENUM_RESTRICTIONS.get(clazz);
                    } else {
                        enumLevels = new HashMap<String, Annotation[]>(1);
                        CACHE_ENUM_RESTRICTIONS.put(clazz, enumLevels);
                    }

                    if (enumLevels.containsKey(name)) {
                        result = enumLevels.get(name);
                    } else {
                        result = filterRestrictions(clazz.getField(name).getAnnotations());
                        enumLevels.put(name, result);
                    }
                }
            } catch (final NoSuchFieldException ignore) {
            }
        } else {
            synchronized (CACHE_RESTRICTIONS) {
                if (CACHE_RESTRICTIONS.containsKey(clazz)) {
                    result = CACHE_RESTRICTIONS.get(clazz);
                } else {
                    result = filterRestrictions(clazz.getAnnotations());
                    CACHE_RESTRICTIONS.put(clazz, result);
                }
            }
        }
        return result == null ? DEFAULT_RESTRICTIONS : result;
    }

    private static Annotation[] filterRestrictions(final Annotation[] annotations) {
        if (annotations.length > 0) {
            final List<Annotation> restrictions = new LinkedList<Annotation>();
            for (final Annotation annotation : annotations) {
                if (annotation.annotationType().isAnnotationPresent(Restriction.class)) {
                    restrictions.add(annotation);
                }
            }
            if (!restrictions.isEmpty()) {
                return restrictions.toArray(new Annotation[restrictions.size()]);
            }
        }
        return null;
    }
}
