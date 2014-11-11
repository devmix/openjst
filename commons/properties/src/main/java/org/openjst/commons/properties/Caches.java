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

import org.apache.commons.lang3.StringUtils;
import org.openjst.commons.properties.annotations.Key;
import org.openjst.commons.properties.annotations.Value;
import org.openjst.commons.properties.restrictions.Pattern;
import org.openjst.commons.properties.restrictions.Restriction;
import org.openjst.commons.properties.storages.annotations.Levels;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import static org.openjst.commons.properties.Property.Converter;
import static org.openjst.commons.properties.Property.Wrapper;

/**
 * TODO SoftReference
 *
 * @author Sergey Grachev
 */
public final class Caches {

    private static final Levels DEFAULT_LEVEL = new Levels.Instance(0, 0xFF);
    private static final Property.Type DEFAULT_TYPE = Property.Type.STRING;
    private static final Annotation[] DEFAULT_RESTRICTIONS = new Annotation[0];

    private static final WeakHashMap<Property, Levels> CACHE_LEVELS = new WeakHashMap<Property, Levels>();
    private static final WeakHashMap<Property, Annotation[]> CACHE_RESTRICTIONS = new WeakHashMap<Property, Annotation[]>();
    private static final WeakHashMap<Property, String> CACHE_KEYS = new WeakHashMap<Property, String>();
    private static final WeakHashMap<Property, Object> CACHE_NULL_AS = new WeakHashMap<Property, Object>();
    private static final WeakHashMap<Property, Property.Type> CACHE_TYPE = new WeakHashMap<Property, Property.Type>();
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

    public static String keyOf(final Property property) {
        synchronized (CACHE_KEYS) {
            if (CACHE_KEYS.containsKey(property)) {
                return CACHE_KEYS.get(property);
            }
        }

        final Class clazz = property.getClass();

        final String name;
        if (clazz.isEnum()) {
            name = ((Enum) property).name();
        } else {
            name = clazz.getSimpleName();
        }

        final Key key = (Key) clazz.getAnnotation(Key.class);
        if (key != null) {
            final StringBuilder sb = new StringBuilder();
            if (StringUtils.isNotBlank(key.group())) {
                sb.append(key.group());
            }

            if (StringUtils.isNotBlank(key.subGroup())) {
                if (sb.length() > 0) {
                    sb.append(key.separator());
                }
                sb.append(key.subGroup());
            }

            if (sb.length() > 0) {
                sb.append(key.separator());
            }

            return sb.append(name).toString().toLowerCase();
        }

        final String result = name.toLowerCase();

        synchronized (CACHE_KEYS) {
            if (!CACHE_KEYS.containsKey(property)) {
                CACHE_KEYS.put(property, result);
            }
        }

        return result;
    }

    public static Object nullAsOf(final Converter converter, final Property property) {
        synchronized (CACHE_NULL_AS) {
            if (CACHE_NULL_AS.containsKey(property)) {
                return CACHE_NULL_AS.get(property);
            }
        }

        final String nullAs;
        if (property instanceof Wrapper) {
            nullAs = ((Wrapper) property).nullAs();
        } else {
            final Class clazz = property.getClass();
            Value value = null;
            if (clazz.isEnum()) {
                try {
                    final String name = ((Enum) property).name();
                    value = clazz.getField(name).getAnnotation(Value.class);
                } catch (final NoSuchFieldException ignore) {
                }
            } else {
                value = (Value) clazz.getAnnotation(Value.class);
            }
            nullAs = value == null || StringUtils.isBlank(value.nullAs()) ? null : value.nullAs();
        }

        final Object result = nullAs == null ? null : converter.asOf(Caches.typeOf(property), nullAs);
        synchronized (CACHE_NULL_AS) {
            if (!CACHE_NULL_AS.containsKey(property)) {
                CACHE_NULL_AS.put(property, result);
            }
        }

        return result;
    }

    public static Levels levelOf(final Property property) {
        synchronized (CACHE_LEVELS) {
            if (CACHE_LEVELS.containsKey(property)) {
                return CACHE_LEVELS.get(property);
            }
        }

        Levels level = null;
        if (property instanceof Wrapper) {
            level = ((Wrapper) property).levels();
        } else {
            final Class clazz = property.getClass();
            if (clazz.isEnum()) {
                try {
                    final String name = ((Enum) property).name();
                    level = clazz.getField(name).getAnnotation(Levels.class);
                } catch (final NoSuchFieldException ignore) {
                }
            } else {
                level = (Levels) clazz.getAnnotation(Levels.class);
            }
        }

        level = level == null ? DEFAULT_LEVEL : level;

        synchronized (CACHE_LEVELS) {
            if (!CACHE_LEVELS.containsKey(property)) {
                CACHE_LEVELS.put(property, level);
            }
        }

        return level;
    }

    public static Property.Type typeOf(final Property property) {
        synchronized (CACHE_TYPE) {
            if (CACHE_TYPE.containsKey(property)) {
                return CACHE_TYPE.get(property);
            }
        }

        Property.Type type;
        if (property instanceof Wrapper) {
            type = ((Wrapper) property).type();
        } else {
            final Class clazz = property.getClass();
            Value value = null;
            if (clazz.isEnum()) {
                try {
                    final String name = ((Enum) property).name();
                    value = clazz.getField(name).getAnnotation(Value.class);
                } catch (final NoSuchFieldException ignore) {
                }
            } else {
                value = (Value) clazz.getAnnotation(Value.class);
            }
            type = value == null ? null : value.type();
        }

        type = type == null ? DEFAULT_TYPE : type;

        synchronized (CACHE_TYPE) {
            if (!CACHE_TYPE.containsKey(property)) {
                CACHE_TYPE.put(property, type);
            }
        }

        return type;
    }

    public static Annotation[] restrictionsOf(final Property property) {
        synchronized (CACHE_RESTRICTIONS) {
            if (CACHE_RESTRICTIONS.containsKey(property)) {
                return CACHE_RESTRICTIONS.get(property);
            }
        }

        Annotation[] restrictions = null;
        if (property instanceof Wrapper) {
            restrictions = ((Wrapper) property).restrictions();
        } else {
            final Class clazz = property.getClass();
            if (clazz.isEnum()) {
                try {
                    final String name = ((Enum) property).name();
                    restrictions = filterRestrictions(clazz.getField(name).getAnnotations());
                } catch (final NoSuchFieldException ignore) {
                }
            } else {
                restrictions = filterRestrictions(clazz.getAnnotations());
            }
        }

        restrictions = restrictions == null ? DEFAULT_RESTRICTIONS : restrictions;

        synchronized (CACHE_RESTRICTIONS) {
            if (!CACHE_RESTRICTIONS.containsKey(property)) {
                CACHE_RESTRICTIONS.put(property, restrictions);
            }
        }

        return restrictions;
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
