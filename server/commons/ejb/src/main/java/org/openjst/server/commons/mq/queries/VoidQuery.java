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

package org.openjst.server.commons.mq.queries;


import org.openjst.server.commons.mq.IFilter;
import org.openjst.server.commons.mq.IOrder;
import org.openjst.server.commons.mq.ISearch;

/**
 * @author Sergey Grachev
 */
public final class VoidQuery {

    private VoidQuery() {
    }

    public static enum Filter implements IFilter {
    }

    public static enum Order implements IOrder {
    }

    public static final class Search implements ISearch {
        private Search() {
        }

        @Override
        public String getText() {
            return null;
        }
    }
}
