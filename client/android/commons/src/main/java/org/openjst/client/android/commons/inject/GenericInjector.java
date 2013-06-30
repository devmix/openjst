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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Sergey Grachev
 */
public class GenericInjector implements Injector {

    public GenericInjector() {
    }

    public void apply(final Object target) {
        final Class<?> targetClass = target.getClass();
        onVisitClass(target, targetClass);

        final Field[] fields = targetClass.getDeclaredFields();
        for (final Field field : fields) {
            onVisitField(target, targetClass, field);
        }

        final Method[] methods = targetClass.getDeclaredMethods();
        for (final Method method : methods) {
            onVisitMethod(target, targetClass, method);
        }
    }

    public void onVisitField(final Object target, final Class<?> targetClass, final Field field) {
        Inject.injectAndroidSystemService(field, target);
        Inject.injectManager(field, target);
    }

    public void onVisitMethod(final Object target, final Class<?> targetClass, final Method method) {
        Inject.injectAndroidService(method, target);
    }

    public void onVisitClass(final Object target, final Class<?> targetClass) {
        // override
    }
}
