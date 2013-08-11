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

/**
 * @author Sergey Grachev
 */
public final class Result {

    private static final QuerySingleResult INTERNAL_SERVER_ERROR = new QuerySingleResult(Status.INTERNAL_SERVER_ERROR);
    private static final QuerySingleResult NOT_EXISTS = new QuerySingleResult(Status.NOT_EXISTS);
    private static final QuerySingleResult NOT_UNIQUE = new QuerySingleResult(Status.NOT_UNIQUE);
    private static final QuerySingleResult OK = new QuerySingleResult(Status.OK);

    private Result() {
    }

    @SuppressWarnings("unchecked")
    public static <M> QuerySingleResult<M> notExists() {
        return (QuerySingleResult<M>) NOT_EXISTS;
    }

    @SuppressWarnings("unchecked")
    public static <M> QuerySingleResult<M> notUnique() {
        return (QuerySingleResult<M>) NOT_UNIQUE;
    }

    @SuppressWarnings("unchecked")
    public static <M> QuerySingleResult<M> ok() {
        return (QuerySingleResult<M>) OK;
    }

    @SuppressWarnings("unchecked")
    public static <M> QuerySingleResult<M> internalServerError() {
        return (QuerySingleResult<M>) INTERNAL_SERVER_ERROR;
    }

    public static <M> QuerySingleResult notUnique(final String[] fields) {
        return new QuerySingleResult(Status.NOT_UNIQUE, fields);
    }

    // TODO errors
    // TODO messages
    public static enum Status {
        OK,
        NOT_UNIQUE,
        NOT_EXISTS,
        INTERNAL_SERVER_ERROR
    }
}
