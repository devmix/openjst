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

package org.openjst.commons.dto.tuples;

import org.jetbrains.annotations.Nullable;

/**
 * @author Sergey Grachev
 */
final class GenericTriple<F, S, T> implements Triple<F, S, T> {
    private final F first;
    private final S second;
    private final T third;

    public GenericTriple(@Nullable final F first, @Nullable final S second, @Nullable final T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public F first() {
        return first;
    }

    @Override
    public S second() {
        return second;
    }

    @Override
    public T third() {
        return third;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GenericTriple that = (GenericTriple) o;
        return !(first != null ? !first.equals(that.first) : that.first != null)
                && !(second != null ? !second.equals(that.second) : that.second != null)
                && !(third != null ? !third.equals(that.third) : that.third != null);
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        result = 31 * result + (third != null ? third.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}
