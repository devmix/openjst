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

package org.openjst.server.commons.maven.utils;

import org.jetbrains.annotations.Nullable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public class OptionGroup {

    public static final OptionGroup EMPTY = new OptionGroup() {

        @Override
        public OptionGroup add(final String name, final Object value) {
            return this;
        }

        @Override
        public OptionGroup addGroup(final String name) {
            return this;
        }

        @Override
        public OptionGroup merge(final OptionGroup group) {
            return this;
        }
    };

    private final Map<String, OptionGroup> groups = new HashMap<String, OptionGroup>();
    private final Map<String, Option> options = new HashMap<String, Option>();

    public static OptionGroup parse(final Map<String, String> config) {
        if (config == null) {
            return EMPTY;
        }
        final OptionGroup result = new OptionGroup();
        for (final String key : config.keySet()) {
            result.add(key, config.get(key));
        }
        return result;
    }

    public OptionGroup add(final String name, final Object value) {
        if (name.contains(".")) {
            OptionGroup current = this;
            final String[] groupNames = name.split("\\.");
            for (int i = 0; i < groupNames.length - 1; i++) {
                final String group = groupNames[i];
                if (!current.hasGroup(group)) {
                    current = current.addGroup(group);
                } else {
                    current = current.group(group);
                }
            }
            current.add(groupNames[groupNames.length - 2], value);
        } else {
            options.put(name, new Option(name, value));
        }
        return this;
    }

    public OptionGroup addGroup(final String name) {
        final OptionGroup group = new OptionGroup();
        groups.put(name, group);
        return group;
    }

    private OptionGroup group(final String name) {
        return groups.get(name);
    }

    private boolean hasGroup(final String group) {
        return groups.containsKey(group);
    }

    public OptionGroup merge(@Nullable final OptionGroup group) {
        if (group != null) {
            merge(group, this);
        }
        return this;
    }

    private void merge(final OptionGroup source, final OptionGroup target) {
        for (final Map.Entry<String, OptionGroup> entry : source.groups.entrySet()) {
            final String name = entry.getKey();
            if (target.groups.containsKey(name)) {
                merge(entry.getValue(), target.groups.get(name));
            } else {
                target.groups.put(name, entry.getValue());
            }
        }

        for (final Map.Entry<String, Option> entry : source.options.entrySet()) {
            target.options.put(entry.getKey(), entry.getValue());
        }
    }

    public Object toJSObject(final Context ctx, final ScriptableObject scope) {
        final Scriptable object = ctx.newObject(scope);
        for (final Map.Entry<String, OptionGroup> entry : groups.entrySet()) {
            object.put(entry.getKey(), object, entry.getValue().toJSObject(ctx, scope));
        }
        for (final Map.Entry<String, Option> entry : options.entrySet()) {
            object.put(entry.getKey(), object, entry.getValue().value());
        }
        return object;
    }

    @Override
    public String toString() {
        return "OptionGroup{" +
                "groups=" + groups +
                ", options=" + options +
                '}';
    }
}
