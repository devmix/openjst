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
import android.content.Context;
import android.os.Handler;
import org.openjst.client.android.commons.events.annotations.OnConnectionEvent;
import org.openjst.client.android.commons.inject.annotations.Singleton;
import org.openjst.client.android.commons.services.LookupServiceFuture;
import org.openjst.client.android.commons.services.ServicesBinder;
import org.openjst.commons.utils.ReflectionUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Sergey Grachev
 */
public final class GlobalContext {

    private static final Map<Class<?>, Class<?>> IMPLEMENTATIONS = new HashMap<Class<?>, Class<?>>();
    private static final Map<Object, Handler> HANDLERS = new WeakHashMap<Object, Handler>();
    private static final Map<Class<?>, Class<?>[]> INTERFACES = new HashMap<Class<?>, Class<?>[]>();
    private static final Map<Class<?>, Object> SINGLETONS = new HashMap<Class<?>, Object>();
    private static final Map<Class, Service> SERVICES = new HashMap<Class, Service>();
    private static final ServicesBinder SERVICES_BINDER = new ServicesBinder();

    /**
     * [annotation class => [{target object => method name}]]
     */
    private static final Map<Class, Map<WeakReference<Object>, String>> EVENT_LISTENERS = new HashMap<Class, Map<WeakReference<Object>, String>>();

    private static WeakReference<Application> application;
    private static WeakReference<Service> service;

    private GlobalContext() {
    }

    public static Application application() {
        return application.get();
    }

    public static Handler handler() {
        final Application a = application.get();
        return HANDLERS.containsKey(a) ? HANDLERS.get(a) : HANDLERS.get(service.get());
    }

    public static Context context() {
        final Application a = application.get();
        return a != null ? a : service.get();
    }

    public static void registerApplication(final Application application) {
        GlobalContext.application = new WeakReference<Application>(application);
        HANDLERS.put(application, new Handler(application.getMainLooper()));
    }

    public static void unregisterApplication() {
        HANDLERS.remove(application.get());
        application = null;
    }

    public static void registerService(final Service service) {
        GlobalContext.service = new WeakReference<Service>(service);
        HANDLERS.put(service, new Handler());
    }

    public static void unregisterService() {
        HANDLERS.remove(service.get());
        service = null;
    }

    public static Map<Class<?>, Class<?>> implementations() {
        return IMPLEMENTATIONS;
    }

    public static Map<Class<?>, Class<?>[]> interfaces() {
        return INTERFACES;
    }

    @SuppressWarnings("unchecked")
    public static <T> T lookup(final Class<T> beanClass) {
        synchronized (SINGLETONS) {
            // find by bean class
            if (SINGLETONS.containsKey(beanClass)) {
                return (T) SINGLETONS.get(beanClass);
            }

            // find by any interface of bean
            for (final Class iClass : beanClass.getInterfaces()) {
                if (SINGLETONS.containsKey(iClass)) {
                    return (T) SINGLETONS.get(iClass);
                }
            }
            // try create new instance of bean
            return (T) createBean(beanClass);
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
                            handler().post(new Runnable() {
                                public void run() {
                                    ReflectionUtils.forceInvokeMethod(target, method, args);
                                }
                            });
                            break;
                        }
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addEvents(final Object target, final Method method) {
        if (method.isAnnotationPresent(OnConnectionEvent.class)) {
            addEventListener(OnConnectionEvent.class, target, method.getName());
        }
    }

//    public static void addEvents(final Object target) {
//        for (final Method method : target.getClass().getDeclaredMethods()) {
//            addEvents(target, method);
//        }
//    }

    public static void removeEventsListener(final Object target) {
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
        handler().post(runnable);
    }

    private static Object createBean(final Class<?> beanClass) {
        final Class<?> oClass = IMPLEMENTATIONS.containsKey(beanClass) ? IMPLEMENTATIONS.get(beanClass) : beanClass;
        final Class<?>[] iClass = INTERFACES.containsKey(oClass) ? INTERFACES.get(oClass) : new Class[]{beanClass};
        final boolean isSingleton = oClass.isAnnotationPresent(Singleton.class);
        Object bean;
        try {
            try {
                final Constructor constructor = oClass.getConstructor(Context.class);
                bean = constructor.newInstance(context());
            } catch (final Exception e) {
                bean = oClass.newInstance();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        if (isSingleton) {
            for (final Class<?> i : iClass) {
                SINGLETONS.put(i, bean);
            }
        }

        return bean;
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
