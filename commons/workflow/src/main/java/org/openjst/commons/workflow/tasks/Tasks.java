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

/**
 * @author Sergey Grachev
 */
public enum Tasks {
    START,
    END,
    NAMED;

    public static Task newStart() {
        return new TaskStart();
    }

    public static Task end() {
        return new TaskEnd();
    }

    public static Task newNamed(final String name) {
        return new TaskNamed(name);
    }
}
