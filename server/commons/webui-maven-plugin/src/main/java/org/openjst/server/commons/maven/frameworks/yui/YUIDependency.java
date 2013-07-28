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

package org.openjst.server.commons.maven.frameworks.yui;

import org.openjst.server.commons.maven.frameworks.Dependency;

/**
 * @author Sergey Grachev
 */
final class YUIDependency implements Dependency {
    private final boolean isObject;
    private final String name;

    public YUIDependency(final String name, final boolean object) {
        this.name = name;
        this.isObject = object;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isObject() {
        return isObject;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final YUIDependency that = (YUIDependency) o;

        return isObject == that.isObject && !(name != null ? !name.equals(that.name) : that.name != null);
    }

    @Override
    public int hashCode() {
        int result = (isObject ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "YUIDependency{" +
                "name='" + name + '\'' +
                '}';
    }
}
