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

package org.openjst.server.commons.mq;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class ModelQuery<F extends IFilter, O extends IOrder, S extends ISearch> {

    public static final int DEFAULT_PAGE_SIZE = 15;

    private final Map<F, Object> filter;
    private final Map<O, IOrder.Type> order;
    private final int startIndex;
    private final int pageSize;
    private S search;

    private ModelQuery(final Map<F, Object> filter, final Map<O, IOrder.Type> order, final int startIndex, final int pageSize) {
        this.filter = filter;
        this.order = order;
        this.startIndex = startIndex;
        this.pageSize = pageSize;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public S getSearch() {
        return search;
    }

    @Nullable
    public Object getFilterValue(final F filter) {
        return this.filter.get(filter);
    }

    public Map<O, IOrder.Type> getOrder() {
        return order;
    }

    public static final class Builder<F extends IFilter, O extends IOrder, S extends ISearch> {

        private final Map<F, Object> filter = new HashMap<F, Object>(0);
        private final Map<O, IOrder.Type> sort = new HashMap<O, IOrder.Type>();
        private int startIndex = 0;
        private int pageSize = DEFAULT_PAGE_SIZE;

        private Builder() {
        }

        public static <F extends IFilter, O extends IOrder, S extends ISearch> Builder<F, O, S> newInstance() {
            return new Builder<F, O, S>();
        }

        public static <F extends IFilter, O extends IOrder, S extends ISearch> Builder<F, O, S> newInstance(final QueryListParams parameters) {
            return new Builder<F, O, S>().startIndex(parameters.startIndex).pageSize(parameters.pageSize);
        }

        public Builder<F, O, S> pageSize(final int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder<F, O, S> startIndex(final int startIndex) {
            this.startIndex = startIndex;
            return this;
        }

        public Builder<F, O, S> filterBy(final F parameter, final Object value) {
            filter.put(parameter, value);
            return this;
        }

        public Builder<F, O, S> orderBy(final O parameter, final IOrder.Type order) {
            sort.put(parameter, order);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <F extends IFilter, O extends IOrder, S extends ISearch> ModelQuery<F, O, S> build() {
            return new ModelQuery<F, O, S>((Map<F, Object>) filter, (Map<O, IOrder.Type>) sort, startIndex, pageSize);
        }
    }
}
