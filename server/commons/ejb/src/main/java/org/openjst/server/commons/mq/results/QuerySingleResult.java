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

package org.openjst.server.commons.mq.results;

import org.openjst.server.commons.mq.IMapping;

/**
 * @author Sergey Grachev
 */
public final class QuerySingleResult<M> extends AbstractResult<M> {

    private final M value;

    QuerySingleResult(final Result.Status status) {
        this(status, null, null);
    }

    QuerySingleResult(final Result.Status status, final Object errorData) {
        this(status, errorData, null);
    }

    QuerySingleResult(final Result.Status status, final Object errorData, final M value) {
        this.status = status;
        this.errorData = errorData;
        this.value = value;
    }

    public M getValue() {
        return value;
    }

    public static final class Builder<M> {

        private Result.Status status = Result.Status.OK;
        private M value;
        private Object error;

        private Builder() {
        }

        public static <M> Builder<M> newInstance() {
            return new Builder<M>();
        }

        public Builder<M> value(final M value) {
            this.value = value;
            return this;
        }

        public Builder<M> status(final Result.Status status) {
            this.status = status;
            return this;
        }

        public Builder<M> error(final Object error) {
            this.error = error;
            return this;
        }

        public <F> Builder<M> convert(final F e, final IMapping<F, M> mapping) {
            this.value = mapping.map(e);
            return this;
        }

        public QuerySingleResult<M> build() {
            return new QuerySingleResult<M>(status, error, value);
        }
    }
}
