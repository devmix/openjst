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

import java.io.Serializable;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("UnusedDeclaration")
public final class SettingChangeEvent implements Serializable {

    private static final long serialVersionUID = 810092482987272964L;

    private final String name;
    private final Object newValue;
    private final Object oldValue;

    private SettingChangeEvent(final String name, final Object newValue, final Object oldValue) {
        this.name = name;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public static SettingChangeEvent newInstance(final String name, final Object newValue, final Object oldValue) {
        return new SettingChangeEvent(name, newValue, oldValue);
    }

    public String getName() {
        return name;
    }

    public Object getNewValue() {
        return newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    @Override
    public String toString() {
        return "SettingChangeEvent{" +
                "name='" + name + '\'' +
                ", newValue=" + newValue +
                ", oldValue=" + oldValue +
                '}';
    }
}
