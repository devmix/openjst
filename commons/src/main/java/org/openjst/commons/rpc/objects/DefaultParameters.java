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

import org.openjst.commons.rpc.RPCParameters;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Sergey Grachev
 */
final class DefaultParameters implements RPCParameters {

    private final Object lock = new Object();
    private Class[] classes;
    private Object[] values;
    private List<Object> objects;

    public DefaultParameters() {
    }

    @Nullable
    public Class[] getTypes() {
        synchronized (lock) {
            if (objects == null || objects.isEmpty()) {
                return null;
            }

            if (classes == null) {
                final Object[] localValues = getValues();
                if (localValues == null) {
                    return null;
                }

                classes = new Class[localValues.length];
                for (int i = 0; i < localValues.length; i++) {
                    final Object object = localValues[i];
                    classes[i] = object == null ? null : object.getClass();
                }
            }

            return classes;
        }
    }

    @Nullable
    public Object[] getValues() {
        synchronized (lock) {
            if (objects == null || objects.isEmpty()) {
                return null;
            }

            if (values == null) {
                values = objects.toArray();
            }

            return values;
        }
    }

    public RPCParameters add(final Object value) {
        synchronized (lock) {
            reset();
            checkObjectsList();
            objects.add(value);
        }
        return this;
    }

    public RPCParameters addAll(final Object[] values) {
        synchronized (lock) {
            reset();
            checkObjectsList();
            Collections.addAll(objects, values);
        }
        return this;
    }

    private void checkObjectsList() {
        if (objects == null) {
            objects = new ArrayList<Object>(1);
        }
    }

    private void reset() {
        // reset
        classes = null;
        values = null;
    }

    public boolean isEmpty() {
        synchronized (lock) {
            return objects == null || objects.isEmpty();
        }
    }

    @Override
    public String toString() {
        return "DefaultParameters{" +
                "classes=" + (classes == null ? null : Arrays.asList(classes)) +
                ", values=" + (values == null ? null : Arrays.asList(values)) +
                ", objects=" + objects +
                '}';
    }
}
