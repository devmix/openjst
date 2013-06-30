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

package org.openjst.commons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class ReflectionUtils {

    private static final Map<Class, Class> PRIMITIVE_TO_OBJECT = new HashMap<Class, Class>();
    private static final Map<Class, Class> OBJECT_TO_PRIMITIVE = new HashMap<Class, Class>();

    static {
        PRIMITIVE_TO_OBJECT.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_OBJECT.put(char.class, Character.class);
        PRIMITIVE_TO_OBJECT.put(byte.class, Byte.class);
        PRIMITIVE_TO_OBJECT.put(short.class, Short.class);
        PRIMITIVE_TO_OBJECT.put(int.class, Integer.class);
        PRIMITIVE_TO_OBJECT.put(long.class, Long.class);
        PRIMITIVE_TO_OBJECT.put(float.class, Float.class);
        PRIMITIVE_TO_OBJECT.put(double.class, Double.class);

        for (final Map.Entry<Class, Class> entry : PRIMITIVE_TO_OBJECT.entrySet()) {
            OBJECT_TO_PRIMITIVE.put(entry.getValue(), entry.getKey());
        }
    }

    private ReflectionUtils() {
    }

    public static boolean classesEquals(Class fromClass, final Class toClass) {

        // autoboxing
        if (fromClass.isPrimitive() && !toClass.isPrimitive()) {
            fromClass = PRIMITIVE_TO_OBJECT.get(fromClass);
        } else if (toClass.isPrimitive() && !fromClass.isPrimitive()) {
            fromClass = OBJECT_TO_PRIMITIVE.get(fromClass);
        }

        if (fromClass == null) {
            return false;
        } else if (fromClass.equals(toClass)) {
            return true;
        }

        if (fromClass.isPrimitive()) {
            if (!toClass.isPrimitive() || double.class == fromClass || boolean.class == fromClass) {
                return false;
            }

            if (byte.class == fromClass) {
                return short.class == toClass || int.class == toClass || long.class == toClass
                        || float.class == toClass || double.class == toClass;
            }
            if (short.class == fromClass) {
                return int.class == toClass || long.class == toClass
                        || float.class == toClass || double.class == toClass;
            }

            if (int.class == fromClass) {
                return long.class == toClass || float.class == toClass || double.class == toClass;
            }

            if (long.class == fromClass) {
                return float.class == toClass || double.class == toClass;
            }

            if (float.class == fromClass) {
                return double.class == toClass;
            }

            if (char.class == fromClass) {
                return int.class == toClass || long.class == toClass
                        || float.class == toClass || double.class == toClass;
            }

        } else {
            //noinspection unchecked
            return toClass.isAssignableFrom(fromClass);
        }

        return false;
    }

    public static void forceSetFieldValue(final Object target, final Field field, final Object value) {
        final boolean accessible = field.isAccessible();
        if (!accessible) {
            field.setAccessible(true);
        }
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            if (!accessible) {
                field.setAccessible(false);
            }
        }
    }

    public static Object forceInvokeMethod(final Object target, final Method method, final Object... args) {
        final boolean accessible = method.isAccessible();
        if (!accessible) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!accessible) {
                method.setAccessible(false);
            }
        }
    }

    public static boolean isModifiableField(final Field field) {
        return (field.getModifiers() & (Modifier.STATIC | Modifier.FINAL | Modifier.NATIVE | Modifier.TRANSIENT)) == 0;
    }
}
