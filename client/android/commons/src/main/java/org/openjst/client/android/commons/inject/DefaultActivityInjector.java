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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import org.openjst.client.android.commons.inject.annotations.android.AMenu;
import org.openjst.client.android.commons.inject.annotations.android.AOnMenuItemSelected;
import org.openjst.commons.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public class DefaultActivityInjector extends DefaultInjector implements ActivityInjector {

    protected final Activity activity;
    protected final Map<Integer, String> mapOnOptionsItemSelected = new LinkedHashMap<Integer, String>();
    protected int activityMenu = -1;

    protected final Class<?> targetClass;

    public DefaultActivityInjector(final Activity activity) {
        this.activity = activity;
        this.targetClass = activity.getClass();
    }

    @Override
    public void onVisitClass(final Object target, final Class<?> clazz) {
        super.onVisitClass(target, clazz);

        InjectTools.injectAndroidLayout(targetClass, activity);
        InjectTools.injectAndroidPreferences(targetClass, activity);

        if (targetClass.isAnnotationPresent(AMenu.class)) {
            final AMenu annotation = targetClass.getAnnotation(AMenu.class);
            if (annotation.value() > -1) {
                activityMenu = annotation.value();
            }
        }
    }

    @Override
    public void onVisitField(final Object target, final Class<?> targetClass, final Field field) {
        super.onVisitField(target, targetClass, field);
        InjectTools.injectAndroidView(field, activity);
    }

    @Override
    public void onVisitMethod(final Object target, final Class<?> targetClass, final Method method) {
        super.onVisitMethod(target, targetClass, method);

        if (method.isAnnotationPresent(AOnMenuItemSelected.class)) {
            final AOnMenuItemSelected annotation = method.getAnnotation(AOnMenuItemSelected.class);
            if (annotation.value() > -1) {
                mapOnOptionsItemSelected.put(annotation.value(), method.getName());
            }
        }
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        if (activityMenu == -1) {
            return false;
        }
        activity.getMenuInflater().inflate(activityMenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        if (!mapOnOptionsItemSelected.containsKey(item.getItemId())) {
            return false;
        }

        final String methodName = mapOnOptionsItemSelected.get(item.getItemId());
        try {
            final Method method = targetClass.getDeclaredMethod(methodName, MenuItem.class);
            return (Boolean) ReflectionUtils.forceInvokeMethod(activity, method, item);
        } catch (final NoSuchMethodException e) {
            Log.e(this.getClass().getSimpleName(), "onOptionsItemSelected", e);
        }

        return false;
    }
}
