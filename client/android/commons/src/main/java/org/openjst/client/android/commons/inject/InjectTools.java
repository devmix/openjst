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

import android.app.Activity;
import android.app.Service;
import android.preference.PreferenceActivity;
import org.openjst.client.android.commons.GlobalContext;
import org.openjst.client.android.commons.inject.annotations.Inject;
import org.openjst.client.android.commons.inject.annotations.android.*;
import org.openjst.client.android.commons.services.LookupServiceFuture;
import org.openjst.commons.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.openjst.commons.utils.ReflectionUtils.forceSetFieldValue;

/**
 * @author Sergey Grachev
 */
public final class InjectTools {

    private static final Map<Class<? extends Injector>, Injector> INJECTORS = new HashMap<Class<? extends Injector>, Injector>();

    private InjectTools() {
    }

    public static <T> void apply(final T target, final Injector injector) {
        injector.apply(target);
        injector.finish();
    }

    public static <T> void apply(final T target, final Class<? extends Injector> injectorClass) {
        final Injector injector;
        if (INJECTORS.containsKey(injectorClass)) {
            injector = INJECTORS.get(injectorClass);
        } else {
            try {
                INJECTORS.put(injectorClass, injector = injectorClass.newInstance());
            } catch (final Exception e) {
                throw new IllegalArgumentException("Can't create injector " + injectorClass.getName());
            }
        }
        apply(target, injector);
    }

    public static <T> void apply(final T target) {
        apply(target, new DefaultInjector());
    }

    public static void injectAndroidSystemService(final Field field, final Object target) {
        if (field.isAnnotationPresent(ASystemService.class)) {
            final ASystemService annotation = field.getAnnotation(ASystemService.class);
            if (!"".equals(annotation.value())) {
                final Object service = GlobalContext.application().getSystemService(annotation.value());
                forceSetFieldValue(target, field, service);
            }
        }
    }

    public static void injectBean(final Field field, final Object target, final DefaultInjector injector) {
        if (field.isAnnotationPresent(Inject.class)) {
            final Object bean = GlobalContext.lookup(field.getType());
            forceSetFieldValue(target, field, bean);
            injector.apply(bean);
        }
    }

    public static void injectAndroidView(final Field field, final Activity target) {
        if (field.isAnnotationPresent(AView.class)) {
            final AView annotation = field.getAnnotation(AView.class);
            if (annotation.value() > -1) {
                final Object view = target.findViewById(annotation.value());
                forceSetFieldValue(target, field, view);
            }
        }
    }

    public static void injectAndroidLayout(final Class<?> targetClass, final Activity target) {
        if (targetClass.isAnnotationPresent(ALayout.class)) {
            final ALayout annotation = targetClass.getAnnotation(ALayout.class);
            if (annotation.value() > -1) {
                target.setContentView(annotation.value());
            }
        }
    }

    public static void injectAndroidPreferences(final Class<?> targetClass, final Activity target) {
        if (PreferenceActivity.class.isAssignableFrom(targetClass) && targetClass.isAnnotationPresent(APreferences.class)) {
            final APreferences annotation = targetClass.getAnnotation(APreferences.class);
            if (annotation.value() > -1) {
                ((PreferenceActivity) target).addPreferencesFromResource(annotation.value());
            }
        }
    }

    public static void injectAndroidService(final Method method, final Object target) {
        if (!method.isAnnotationPresent(AService.class)) {
            return;
        }
        final AService annotation = method.getAnnotation(AService.class);
        @SuppressWarnings("unchecked")
        final Class<Service> serviceClass = (Class<Service>) annotation.value();
        GlobalContext.lookupService(serviceClass, new LookupServiceFuture<Service>() {
            public void onBind(final Service service) {
                ReflectionUtils.forceInvokeMethod(target, method, service);
            }
        });
    }
}
