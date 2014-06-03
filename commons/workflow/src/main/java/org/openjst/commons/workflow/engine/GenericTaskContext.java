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

import org.openjst.commons.workflow.tasks.Tasks;

import javax.annotation.Nullable;

/**
 * @author Sergey Grachev
 */
final class GenericTaskContext implements TaskContext {

    private static final long serialVersionUID = -6756364544176702248L;

    private final String id;
    private final Tasks type;
    private final String name;

    public GenericTaskContext(final String id, final Tasks type, final String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Tasks getType() {
        return type;
    }

    @Nullable
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TaskContext{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
