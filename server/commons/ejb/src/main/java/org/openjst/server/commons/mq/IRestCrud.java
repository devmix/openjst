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

import org.jboss.resteasy.annotations.Form;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Sergey Grachev
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IRestCrud<M, LP> {

    @GET
    @Path("/")
    QueryResult<M> list(@Form LP parameters);

    @POST
    @Path("/")
    QueryResult<M> create(M model);

    @GET
    @Path("/{id}")
    QueryResult<M> read(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    QueryResult<M> update(@PathParam("id") Long id, M model);

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    QueryResult<M> delete(@PathParam("id") Long id);
}
