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

package org.openjst.commons.workflow.tasks;

import org.jetbrains.annotations.Nullable;

/**
 * @author Sergey Grachev
 */
final class TaskNamed implements Task {

    private static final long serialVersionUID = 4402517713441542736L;

    private final String name;

    TaskNamed(final String name) {
        this.name = name;
    }

    @Override
    public Tasks getType() {
        return Tasks.NAMED;
    }

    @Nullable
    @Override
    public String getName() {
        return name;
    }
}
