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

package org.openjst.commons.workflow.events;

import org.openjst.commons.workflow.UserData;

import javax.annotation.Nullable;

/**
 * @author Sergey Grachev
 */
final class EventNamed implements Event {

    private static final long serialVersionUID = 6387315961434279238L;

    private final String name;
    private final UserData data;

    EventNamed(final String name, @Nullable final UserData data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public UserData getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", data=" + data +
                '}';
    }
}
