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

package org.openjst.server.mobile.web.rest;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.cache.Cache;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author Sergey Grachev
 */
@Path("/ui/assets")
public interface Assets {

    @GZIP
    @GET
    @Path("/common.css")
    @Cache(maxAge = 315360000, noStore = false, mustRevalidate = true)
    @Produces("text/css")
    Response commonCss(@Context ServletContext servletContext,
                       @HeaderParam("Cache-Control") String cacheControl, @HeaderParam("Pragma") String pragma);

    @GZIP
    @GET
    @Path("/common.js")
    @Cache(maxAge = 315360000, noStore = false, mustRevalidate = true)
    @Produces("text/javascript")
    Response commonJs(@Context ServletContext servletContext,
                      @HeaderParam("Cache-Control") String cacheControl, @HeaderParam("Pragma") String pragma);

    @GZIP
    @GET
    @Path("/application.js")
    @Cache(maxAge = 315360000, noStore = false, mustRevalidate = true)
    @Produces("text/javascript")
    Response applicationJs(@Context HttpServletRequest request, @Context ServletContext servletContext);

    @GZIP
    @GET
    @Path("/yui-combo")
    @Cache(maxAge = 315360000, noStore = false, mustRevalidate = true)
    Response yuiFrameworkCombo(@Context UriInfo uriInfo, @Context ServletContext servletContext,
                               @HeaderParam("Cache-Control") String cacheControl, @HeaderParam("Pragma") String pragma);

    @GZIP
    @GET
    @Path("/ui-combo")
    @Cache(maxAge = 315360000, noStore = false, mustRevalidate = true)
    Response yuiUiCombo(@Context UriInfo uriInfo, @Context ServletContext servletContext,
                        @HeaderParam("Cache-Control") String cacheControl, @HeaderParam("Pragma") String pragma);
}
