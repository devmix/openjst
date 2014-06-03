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

package org.openjst.commons.rpc.objects;

import org.openjst.commons.conversion.utils.DateTimeUtils;
import org.openjst.commons.rpc.exceptions.RPCException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
final class DefaultMethod {
    public static final Object[] EMPTY_PARAMETERS = new Object[0];

    private final Method method;

    public DefaultMethod(final DefaultContext context, final Method method) {
        this.method = method;
    }

    public Object invoke(final Object consumer, final Object[] parameters) throws RPCException {
        try {
            return method.invoke(consumer, cast(parameters));
        } catch (Exception e) {
            throw RPCException.newNested(e);
        }
    }

    private Object[] cast(final Object[] parameters) throws RPCException {
        final Class[] methodParameterTypes = method.getParameterTypes();

        if (methodParameterTypes.length != parameters.length) {
            throw RPCException.newWithMessage("Amount parameters isn't equals to method parameters");
        }

        if (parameters.length == 0) {
            return EMPTY_PARAMETERS;
        }

        final Object[] cast = new Object[parameters.length];
        for (int i = 0, parametersLength = parameters.length; i < parametersLength; i++) {
            final Object o = parameters[i];
            if (o != null) {
                final Class parameterClass = o.getClass();
                final Class methodParameterClass = methodParameterTypes[i];
                if (Map.class.isAssignableFrom(parameterClass) &&
                        !Map.class.isAssignableFrom(methodParameterClass) && Object.class.isAssignableFrom(methodParameterClass)) {
                    try {
                        //noinspection unchecked
                        cast[i] = createObject(methodParameterClass, (Map<String, Object>) o);
                    } catch (Exception e) {
                        throw RPCException.newNested(e);
                    }
                    continue;
                }
            }
            cast[i] = o;
        }

        return cast;
    }

    private Object createObject(final Class clazz, final Map<String, Object> rpcObject) throws IllegalAccessException, InstantiationException, NoSuchFieldException, RPCException, ParseException {
        final Object obj = clazz.newInstance();
        for (final Map.Entry<String, Object> entry : rpcObject.entrySet()) {
            final Field field = clazz.getDeclaredField(entry.getKey());
            if ((field.getModifiers() & (Modifier.STATIC | Modifier.FINAL | Modifier.NATIVE | Modifier.TRANSIENT)) != 0) {
                continue;
            }

            final boolean isAccessible = field.isAccessible();
            if (!isAccessible) {
                field.setAccessible(true);
            }

            try {
                final Object value = entry.getValue();
                final Class fieldClass = field.getType();
                if (value == null) {
                    if (fieldClass.isPrimitive()) {
                        throw RPCException.newWithMessage("Illegal value for field '" + entry.getKey() + "', primitive can't be to NULL");
                    }
                    field.set(obj, null);
                    continue;
                }

                if (value instanceof Map) {

                    if (!Map.class.isAssignableFrom(fieldClass)) {
                        //noinspection unchecked
                        field.set(obj, createObject(fieldClass, (Map<String, Object>) value));
                    } else {
                        field.set(obj, value);
                    }
                    continue;

                } else if (value instanceof Object[]) {

                    field.set(obj, value);
                    continue;

                } else if (value instanceof Date) {

                    field.set(obj, value);
                    continue;

                } else if (value instanceof byte[]) {

                    field.set(obj, value);
                    continue;

                }

                final String valueAsString = value.toString();
                if (fieldClass == Byte.class || fieldClass == byte.class) {
                    field.set(obj, Byte.parseByte(valueAsString));
                } else if (fieldClass == Short.class || fieldClass == short.class) {
                    field.set(obj, Short.parseShort(valueAsString));
                } else if (fieldClass == Integer.class || fieldClass == int.class) {
                    field.set(obj, Integer.parseInt(valueAsString));
                } else if (fieldClass == Long.class || fieldClass == long.class) {
                    field.set(obj, Long.parseLong(valueAsString));
                } else if (fieldClass == Float.class || fieldClass == float.class) {
                    field.set(obj, Float.parseFloat(valueAsString));
                } else if (fieldClass == Double.class || fieldClass == double.class) {
                    field.set(obj, Double.parseDouble(valueAsString));
                } else if (fieldClass == Boolean.class || fieldClass == boolean.class) {
                    field.set(obj, Boolean.parseBoolean(valueAsString));
                } else if (fieldClass == Date.class) {
                    field.set(obj, DateTimeUtils.parseTimestamp(valueAsString));
                } else if (fieldClass == String.class) {
                    field.set(obj, valueAsString);
                } else if (fieldClass == Character.class || fieldClass == char.class) {
                    if (valueAsString.length() > 1) {
                        throw RPCException.newWithMessage("Value of the character type has more that one symbol");
                    }
                    field.set(obj, valueAsString.charAt(0));
                } else {
                    throw RPCException.newWithMessage("Don't known how instantiate field " + entry.getKey());
                }
            } finally {
                if (!isAccessible) {
                    field.setAccessible(isAccessible);
                }
            }
        }
        return obj;
    }
}
