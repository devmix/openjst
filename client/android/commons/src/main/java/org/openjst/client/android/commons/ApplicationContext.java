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

package org.openjst.client.android.commons;

import android.app.Application;
import android.app.Service;
import android.os.Handler;
import org.openjst.client.android.commons.events.annotations.OnConnectionEvent;
import org.openjst.client.android.commons.inject.Inject;
import org.openjst.client.android.commons.inject.annotations.JSTApplicationContext;
import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.openjst.client.android.commons.services.LookupServiceFuture;
import org.openjst.client.android.commons.services.ServicesBinder;
import org.openjst.commons.utils.ReflectionUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class ApplicationContext {

    private static final Map<Class<?>, Object> MANAGERS = new HashMap<Class<?>, Object>();
    private static final Map<Class, Service> SERVICES = new HashMap<Class, Service>();
    private static final ServicesBinder SERVICES_BINDER = new ServicesBinder();

    /**
     * [annotation class => [{target object => method name}]]
     */
    private static final Map<Class, Map<WeakReference<Object>, String>> EVENT_LISTENERS = new HashMap<Class, Map<WeakReference<Object>, String>>();

    private static Application application;
    private static Handler handler;

    private ApplicationContext() {
    }

    public static void init(final Application application) {
        ApplicationContext.application = application;
        ApplicationContext.handler = new Handler(application.getMainLooper());
        applyApplicationContextAnnotation(application);
    }

    /**
     * For test
     */
    public static <T> void applyApplicationContextAnnotation(final T target) {
        final Class<?> applicationClass = target.getClass();
        if (applicationClass.isAnnotationPresent(JSTApplicationContext.class)) {
            final JSTApplicationContext annotation = applicationClass.getAnnotation(JSTApplicationContext.class);
            // create managers
            initializeManagers(annotation.managers());
            // inject dependencies into current object
            Inject.apply(target, annotation.injector());
            // inject dependencies into new managers
            for (final Object manager : ApplicationContext.MANAGERS.values()) {
                Inject.apply(manager, annotation.injector());
            }
        }
    }

    public static Application getApplication() {
        return application;
    }

    @SuppressWarnings("unchecked")
    public static <T> T lookup(final Class<T> managerClass) {
        synchronized (MANAGERS) {
            return (T) MANAGERS.get(managerClass);
        }
    }

    private static void initializeManagers(final Class<?>... list) {
        for (final Class<?> managerClass : list) {
            if (!managerClass.isAnnotationPresent(JSTInject.class)) {
                throw new IllegalArgumentException("Instance of '" + managerClass + "' not annotated as JSTInject");
            }

            final JSTInject annotation = managerClass.getAnnotation(JSTInject.class);
            final Class bindManagerClass = annotation.value();
            if (bindManagerClass == Void.class) {
                throw new IllegalArgumentException("Unknown class of manager '" + managerClass + "'");
            }

            try {
                Object manager;
                try {
                    final Constructor constructor = managerClass.getConstructor(Application.class);
                    manager = constructor.newInstance(application);
                } catch (Exception e) {
                    manager = managerClass.newInstance();
                }
                addManager(bindManagerClass, manager);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void addManager(final Class clazz, final Object instance) {
        synchronized (MANAGERS) {
            MANAGERS.put(clazz, instance);
        }
    }

    public static <T extends Service> void lookupService(final Class<T> type, final LookupServiceFuture<T> future) {
        synchronized (SERVICES) {
            @SuppressWarnings("unchecked")
            final T service = (T) SERVICES.get(type);
            if (service != null) {
                future.onBind(service);
                return;
            }
        }
        SERVICES_BINDER.bind(type, future);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Service> T lookupService(final Class<T> type) {
        synchronized (SERVICES) {
            return (T) SERVICES.get(type);
        }
    }

    public static void addAndroidService(final Class serviceClass, final Service serviceInstance) {
        synchronized (SERVICES) {
            SERVICES.put(serviceClass, serviceInstance);
        }
    }

    public static void removeAndroidService(final Class serviceClass) {
        synchronized (SERVICES) {
            SERVICES.remove(serviceClass);
        }
    }

    public static <T> void addEventListener(final Class annotationClass, final T target, final String methodName) {
        synchronized (EVENT_LISTENERS) {
            Map<WeakReference<Object>, String> listeners = EVENT_LISTENERS.get(annotationClass);
            if (listeners == null) {
                listeners = new HashMap<WeakReference<Object>, String>(1);
                EVENT_LISTENERS.put(annotationClass, listeners);
            }
            listeners.put(new WeakReferenceListener(target.getClass().getName().hashCode(), target), methodName);
        }
    }

    public static void fireEvent(final Class annotationClass, final Object... args) {
        synchronized (EVENT_LISTENERS) {
            final Map<WeakReference<Object>, String> listeners = EVENT_LISTENERS.get(annotationClass);
            if (listeners == null) {
                return;
            }

            final Iterator<Map.Entry<WeakReference<Object>, String>> iterator = listeners.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<WeakReference<Object>, String> listener = iterator.next();
                final Object target = listener.getKey().get();
                if (target == null) {
                    iterator.remove();
                    continue;
                }

                try {
                    final String methodName = listener.getValue();
                    for (final Method method : target.getClass().getDeclaredMethods()) {
                        if (method.getName().equals(methodName)) {
                            handler.post(new Runnable() {
                                public void run() {
                                    ReflectionUtils.forceInvokeMethod(target, method, args);
                                }
                            });
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addEvents(final Object target, final Method method) {
        if (method.isAnnotationPresent(OnConnectionEvent.class)) {
            ApplicationContext.addEventListener(OnConnectionEvent.class, target, method.getName());
        }
    }

    public static void addEvents(final Object target) {
        for (final Method method : target.getClass().getDeclaredMethods()) {
            addEvents(target, method);
        }
    }

    public static void removeEventListener(final Object target) {
        synchronized (EVENT_LISTENERS) {
            final int id = target.getClass().getName().hashCode();
            for (final Map<WeakReference<Object>, String> listeners : EVENT_LISTENERS.values()) {
                final Iterator<Map.Entry<WeakReference<Object>, String>> iterator = listeners.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Map.Entry<WeakReference<Object>, String> listener = iterator.next();
                    if (listener.getKey().hashCode() == id) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    public static void post(final Runnable runnable) {
        handler.post(runnable);
    }

    private static final class WeakReferenceListener extends WeakReference<Object> {

        private final int id;

        public WeakReferenceListener(final int id, final Object reference) {
            super(reference);
            this.id = id;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final WeakReferenceListener that = (WeakReferenceListener) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}
