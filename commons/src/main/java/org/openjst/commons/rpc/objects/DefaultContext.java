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

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.rpc.RPCContext;
import org.openjst.commons.rpc.RPCParameters;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.commons.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
final class DefaultContext implements RPCContext {

    private final boolean useCache;
    private final DefaultCache cache = new DefaultCache();
    private final Map<String, Class> parameterClasses = new HashMap<String, Class>(0);
    private final Map<String, Class> handlerClasses = new HashMap<String, Class>(0);
    private final Map<String, Object> handlerInstances = new HashMap<String, Object>(0);

    @Nullable
    private Object newInstance(final Class clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public DefaultContext(final boolean useCache) {
        this.useCache = useCache;
    }

    public DefaultContext() {
        this(true);
    }

    public RPCContext registerParameter(final String name, final Class clazz) {
        parameterClasses.put(name, clazz);
        return this;
    }

    public RPCContext registerHandlerDefault(final Object handler) {
        handlerInstances.put(null, handler);
        return this;
    }

    public RPCContext registerHandler(final String alias, final Object handler) {
        if (alias == null) {
            throw new IllegalArgumentException("Alias name can't be null");
        }

        if (handler == null) {
            throw new IllegalArgumentException("Handler can't be null");
        }

        synchronized (handlerInstances) {
            handlerInstances.put(alias, handler);
        }

        return this;
    }

    public RPCContext registerHandler(final Class clazz, final boolean singleton) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class of handler can't be null");
        }

        if (singleton) {
            registerHandler(clazz.getSimpleName(), newInstance(clazz));
        } else {
            synchronized (handlerClasses) {
                handlerClasses.put(clazz.getSimpleName(), clazz);
            }
        }

        return this;
    }

    public Object invoke(final String object, final String methodName, final RPCParameters parameters) throws RPCException {
        final Object target = getHandler(object);
        if (target == null) {
            throw new IllegalArgumentException("Not found object '" + object + "'");
        }
        if (methodName == null) {
            throw new IllegalStateException("Method '" + methodName + "' not defined");
        }
        if (parameters == null) {
            throw new IllegalStateException("Parameters not defined");
        }

        final DefaultMethod method = getMethod(methodName, parameters, target);

        return method.invoke(target, parameters.getValues());
    }

    private DefaultMethod getMethod(final String methodName, final RPCParameters parameters, final Object target) throws RPCException {
        final Class clazz = target.getClass();

        DefaultCache.CacheKey cacheKey = null;
        if (useCache) {
            cacheKey = new DefaultCache.CacheKey(clazz, methodName, parameters.getTypes());
            final DefaultMethod rpcMethod = cache.get(cacheKey);
            if (rpcMethod != null) {
                return rpcMethod;
            }
        }

        final Method[] methods = clazz.getMethods();
        final Object[] values = parameters.getValues();

        for (final Method method : methods) {
            if (checkMethod(method, methodName, values)) {
                final DefaultMethod rpcMethod = new DefaultMethod(this, method);

                if (useCache) {
                    cache.put(cacheKey, rpcMethod);
                }

                return rpcMethod;
            }
        }

        throw RPCException.newWithMessage("RPC method not found. name:{" + methodName + "} parameterTypes:{" + parameters + "}");
    }

    private boolean checkMethod(final Method method, final String methodName, final Object[] values) throws RPCException {
        final Class[] actualParameters = method.getParameterTypes();
        if (actualParameters.length != values.length || !method.getName().equals(methodName)) {
            return false;
        }

        for (int i = 0, length = actualParameters.length; i < length; i++) {
            final Class argClass = actualParameters[i];
            final Object paramObj = values[i];

            if (paramObj == null) {
                if (argClass.isPrimitive()) {
                    return false;
                }
                continue;
            }

            final Class paramClass = paramObj.getClass();
            if (!ReflectionUtils.classesEquals(paramClass, argClass)) {
                if (Object.class.isAssignableFrom(argClass) && Map.class.isAssignableFrom(paramClass)) {
                    // convert Map to Object
                    continue;
                }
                return false;
            }
        }

        return true;
    }

    @Nullable
    public Object getHandler(final Class clazz) {
        if (clazz == null) {
            return null;
        }

        return getHandler(clazz.getSimpleName());
    }

    @Nullable
    public Object getHandler(final String alias) {
        synchronized (handlerClasses) {
            if (handlerClasses.containsKey(alias)) {
                return getHandler(handlerClasses.get(alias));
            }
        }

        synchronized (handlerInstances) {
            return handlerInstances.get(alias);
        }
    }
}
