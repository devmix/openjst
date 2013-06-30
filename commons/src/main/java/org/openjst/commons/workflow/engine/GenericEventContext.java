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

package org.openjst.commons.workflow.engine;

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.workflow.UserData;
import org.openjst.commons.workflow.UserDataHashMap;

/**
 * @author Sergey Grachev
 */
final class GenericEventContext implements EventContext {

    private static final long serialVersionUID = -1162411148666909339L;

    private final String id;
    private final String name;
    private final UserData data;

    public GenericEventContext(final String id, final String name, @Nullable final UserData data) {
        this.id = id;
        this.name = name;
        this.data = data == null ? new UserDataHashMap() : data;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UserData getData() {
        return data;
    }

    @Override
    public String toString() {
        return "EventContext{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", data=" + data +
                '}';
    }
}
