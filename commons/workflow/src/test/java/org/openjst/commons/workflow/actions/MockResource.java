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

package org.openjst.commons.workflow.actions;

/**
 * @author Sergey Grachev
 */
public final class MockResource implements Resource {

    private static final long serialVersionUID = -7788376309317819379L;

    private final int key;

    public MockResource(final int key) {
        this.key = key;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MockResource that = (MockResource) o;
        return key == that.key;

    }

    @Override
    public int hashCode() {
        return key;
    }

    @Override
    public String toString() {
        return String.valueOf(key);
    }
}
