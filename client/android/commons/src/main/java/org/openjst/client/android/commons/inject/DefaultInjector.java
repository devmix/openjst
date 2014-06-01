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

package org.openjst.client.android.commons.inject;

import org.openjst.client.android.commons.GlobalContext;
import org.openjst.client.android.commons.events.annotations.OnConnectionEvent;
import org.openjst.client.android.commons.inject.annotations.OnCreate;
import org.openjst.client.android.commons.inject.annotations.Singleton;
import org.openjst.commons.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public class DefaultInjector implements Injector {

    protected static final Set<Class<?>> VISITED_SINGLETONS = new LinkedHashSet<Class<?>>();
    protected final Set<Class<?>> visitedBeans = new LinkedHashSet<Class<?>>();

    @Override
    public void apply(final Object target) {
        final Class<?> targetClass = target.getClass();

        final boolean isSingleton = targetClass.isAnnotationPresent(Singleton.class);
        if (isSingleton) {
            if (VISITED_SINGLETONS.contains(targetClass)) {
                return;
            }
        } else {
            if (visitedBeans.contains(targetClass)) {
                final StringBuilder sb = new StringBuilder("Cyclic reference {" + target + "}:\n");
                for (final Class c : visitedBeans) {
                    sb.append("\t").append(c.getName()).append("\n");
                }
                sb.append("\t").append(targetClass.getName());
                throw new IllegalArgumentException(sb.toString());
            }
        }

        if (isSingleton) {
            VISITED_SINGLETONS.add(targetClass);
        } else {
            visitedBeans.add(targetClass);
        }

        onVisitClass(target, targetClass);

        final Field[] fields = targetClass.getDeclaredFields();
        for (final Field field : fields) {
            onVisitField(target, targetClass, field);
        }

        final Method[] methods = targetClass.getDeclaredMethods();
        for (final Method method : methods) {
            onVisitMethod(target, targetClass, method);
        }

        if (!isSingleton) {
            visitedBeans.remove(targetClass);
        }
    }

    @Override
    public void onVisitField(final Object target, final Class<?> targetClass, final Field field) {
        InjectTools.injectAndroidSystemService(field, target);
        InjectTools.injectBean(field, target, this);
    }

    @Override
    public void onVisitMethod(final Object target, final Class<?> targetClass, final Method method) {
        InjectTools.injectAndroidService(method, target);
        if (method.isAnnotationPresent(OnCreate.class)) {
            ReflectionUtils.forceInvokeMethod(target, method);
        }
    }

    @Override
    public void onVisitClass(final Object target, final Class<?> targetClass) {
        // override
    }

    @Override
    public void onEnableEvents(final Object target, final Class<?> targetClass, final Method method) {
        if (method.isAnnotationPresent(OnConnectionEvent.class)) {
            GlobalContext.addEventListener(OnConnectionEvent.class, target, method.getName());
        }
    }

    @Override
    public void enableEvents(final Object target) {
        final Class<?> targetClass = target.getClass();
        for (final Method method : targetClass.getDeclaredMethods()) {
            onEnableEvents(target, targetClass, method);
        }
    }

    @Override
    public void disableEvents(final Object target) {
        GlobalContext.removeEventsListener(target);
    }

    @Override
    public void finish() {
        visitedBeans.clear();
    }
}
