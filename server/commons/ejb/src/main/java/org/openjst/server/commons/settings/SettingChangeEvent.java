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

package org.openjst.server.commons.settings;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("UnusedDeclaration")
public final class SettingChangeEvent implements Serializable {

    private static final long serialVersionUID = 810092482987272964L;

    private final Map<Setting, Item> settings = new HashMap<Setting, Item>();

    public SettingChangeEvent(final Set<Item> settings) {
        for (final Item item : settings) {
            this.settings.put(item.getSetting(), item);
        }
    }

    public SettingChangeEvent(final Item item) {
        this.settings.put(item.getSetting(), item);
    }

    public static SettingChangeEvent newInstance(final Setting setting, final Object newValue, final Object oldValue) {
        return new SettingChangeEvent(new Item(setting, newValue, oldValue));
    }

    public static SettingChangeEvent newInstance(final Set<Item> settings) {
        return new SettingChangeEvent(settings);
    }

    public static Item newItem(final Setting setting, final Object newValue, final Object oldValue) {
        return new Item(setting, newValue, oldValue);
    }

    public boolean containsAny(final Set<Setting> search) {
        final Set<Setting> exists = settings.keySet();
        for (final Setting setting : search) {
            if (exists.contains(setting)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "SettingChangeEvent{" +
                "settings=" + settings +
                '}';
    }

    public static final class Item {

        private final Setting setting;
        private final Object newValue;
        private final Object oldValue;

        public Item(final Setting setting, @Nullable final Object newValue, @Nullable final Object oldValue) {
            this.setting = setting;
            this.newValue = newValue;
            this.oldValue = oldValue;
        }

        public Setting getSetting() {
            return setting;
        }

        @Nullable
        public Object getNewValue() {
            return newValue;
        }

        @Nullable
        public Object getOldValue() {
            return oldValue;
        }
    }
}
