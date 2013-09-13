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


import org.openjst.commons.dto.tuples.Pair;
import org.openjst.server.commons.mq.IMapping;
import org.openjst.server.commons.mq.ModelQuery;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class QueryListResult<M> extends AbstractResult<M> {

    private final List<M> list;
    private final int startIndex;
    private final long total;
    private final int pageSize;

    private QueryListResult(final List<M> list, final int startIndex, final long total, final Result.Status status, final int pageSize,
                            final Object errorData) {
        this.list = list;
        this.startIndex = startIndex;
        this.total = total;
        this.status = status;
        this.pageSize = pageSize;
        this.errorData = errorData;
    }

    public List<M> getList() {
        return list;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public long getTotal() {
        return total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public static final class Builder<M> {

        private final List<M> list = new LinkedList<M>();
        private final int startIndex;
        private final int pageSize;
        private long total;
        private Result.Status status = Result.Status.OK;

        private Builder(final int startIndex, final int pageSize) {
            this.startIndex = startIndex;
            this.pageSize = pageSize;
        }

        public Builder() {
            startIndex = 0;
            pageSize = ModelQuery.DEFAULT_PAGE_SIZE;
        }

        public static <M> Builder<M> newInstanceFor(final ModelQuery query) {
            return new Builder<M>(query.getStartIndex(), query.getPageSize());
        }

        public static <M> Builder<M> newInstance() {
            return new Builder<M>();
        }

        public Builder<M> add(final List<M> list) {
            this.list.addAll(list);
            return this;
        }

        public Builder<M> add(final M model) {
            this.list.add(model);
            return this;
        }

        public Builder<M> total(final long total) {
            this.total = total;
            return this;
        }

        public Builder<M> status(final Result.Status status) {
            this.status = status;
            return this;
        }

        public <F> Builder<M> convert(final F e, final IMapping<F, M> mapping) {
            add(mapping.map(e));
            return this;
        }

        public <F> Builder<M> convert(final List<F> list, final IMapping<F, M> mapping) {
            for (final F e : list) {
                add(mapping.map(e));
            }
            return this;
        }

        public <F> Builder<M> convert(final Pair<List<F>, Long> list, final IMapping<F, M> mapping) {
            convert(list.first(), mapping);
            total(list.second());
            return this;
        }

        public QueryListResult<M> build() {
            return new QueryListResult<M>(list, startIndex, total, status, pageSize, null);
        }
    }
}
