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

import android.app.Application;
import org.openjst.client.android.commons.inject.annotations.Singleton;
import org.openjst.client.android.commons.inject.annotations.android.ApplicationConfig;

import static org.openjst.client.android.commons.GlobalContext.*;

/**
 * @author Sergey Grachev
 */
public class DefaultApplicationInjector extends DefaultInjector {

    protected final Application application;

    public DefaultApplicationInjector(final Application application) {
        this.application = application;
    }

    @Override
    public void onVisitClass(final Object target, final Class<?> targetClass) {
        if (targetClass.isAnnotationPresent(ApplicationConfig.class)) {
            final ApplicationConfig annotation = targetClass.getAnnotation(ApplicationConfig.class);
            registerImplementations(annotation.implementations());
        }
        super.onVisitClass(target, targetClass);
    }

    public void registerImplementations(final Class<?>[] implementations) {
        if (implementations.length == 0) {
            return;
        }

        for (final Class<?> oClass : implementations) {
            final Class<?>[] interfaces = oClass.getInterfaces();
            if (interfaces.length > 0) {
                for (final Class<?> iClass : interfaces) {
                    implementations().put(iClass, oClass);
                }
                interfaces().put(oClass, interfaces);
            }
        }

        for (final Class<?> oClass : implementations) {
            final boolean isLazySingleton = oClass.isAnnotationPresent(Singleton.class)
                    && oClass.getAnnotation(Singleton.class).lazy();
            final Class<?>[] interfaces = interfaces().get(oClass);
            if (!isLazySingleton) {
                if (interfaces.length > 0) {
                    apply(lookup(interfaces[0]));
                } else {
                    apply(lookup(oClass));
                }
            }
        }
    }
}
