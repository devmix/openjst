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

package org.openjst.server.mobile.web.rest.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.infinispan.Cache;
import org.openjst.commons.i18n.Language;
import org.openjst.server.commons.services.PreferencesManager;
import org.openjst.server.commons.web.utils.WebAssetsAggregator;
import org.openjst.server.mobile.I18n;
import org.openjst.server.mobile.Preferences;
import org.openjst.server.mobile.cdi.beans.MobileSession;
import org.openjst.server.mobile.mq.model.ApplicationModel;
import org.openjst.server.mobile.services.CacheService;
import org.openjst.server.mobile.web.UIAssets;
import org.openjst.server.mobile.web.rest.Assets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public class AssetsImpl implements Assets {

    private static final Logger LOG = LoggerFactory.getLogger(Assets.class);

    private static final int CACHE_COMMON_CSS = "common.css".hashCode();
    private static final int CACHE_COMMON_JS = "common.js".hashCode();

    @EJB
    private CacheService cacheService;

    @EJB
    private PreferencesManager preferences;

    @Inject
    private MobileSession session;

    @Override
    public Response commonCss(final ServletContext servletContext, final String cacheControl, final String pragma) {
        final Cache<Integer, String> cache = cacheService.getWebUI();

        final String content;
        if (!preferences.getBoolean(Preferences.UI.SCRIPTS_CACHE) || !cache.containsKey(CACHE_COMMON_CSS)) {
            content = WebAssetsAggregator.newInstance(servletContext)
                    .addResources("/" + UIAssets.PATH_LIB, UIAssets.CSS_COMMON)
//                    .addResources("/" + UIAssets.PATH_ASSETS_CSS, UIAssets.CSS_ASSETS)
                    .aggregate();
            cache.put(CACHE_COMMON_CSS, content);
        } else {
            content = cache.get(CACHE_COMMON_CSS);
        }

        return Response.ok(content).header("Content-Type", "text/css").build();
    }

    @Override
    public Response commonJs(final ServletContext servletContext, final String cacheControl, final String pragma) {
        return getAggregatedResources(servletContext, cacheControl, pragma, CACHE_COMMON_JS, "/" + UIAssets.PATH_LIB, UIAssets.JS_COMMON);
    }

    @Override
    public Response yuiFrameworkCombo(final UriInfo uriInfo, final ServletContext servletContext, final String cacheControl, final String pragma) {
        final Set<String> resourcesSet = uriInfo.getQueryParameters().keySet();
        final String[] resources = resourcesSet.toArray(new String[resourcesSet.size()]);
        final int cacheKey = Arrays.hashCode(resources);
        return getAggregatedResources(servletContext, cacheControl, pragma, cacheKey, "/" + UIAssets.PATH_LIB + "yui/build/",
                resources
        );
    }

    @Override
    public Response yuiUiCombo(final UriInfo uriInfo, final ServletContext servletContext, final String cacheControl, final String pragma) {
        final Set<String> resourcesSet = uriInfo.getQueryParameters().keySet();
        final String[] resources = resourcesSet.toArray(new String[resourcesSet.size()]);
        final int cacheKey = Arrays.hashCode(resources);
        return getAggregatedResources(servletContext, cacheControl, pragma, cacheKey, "/ui/",
                resources
        );
    }

    @Override
    public Response applicationJs(final HttpServletRequest request, final ServletContext servletContext) {
        final Language language = I18n.get(this.session.getLocale());

        // TODO split localization keys into groups by '.', ex. i18n: {ui:{label:{'sing-in':'Sing in','password':'Password'}}}
        // TODO user preferences

        final ApplicationModel application = new ApplicationModel(this.session.getSession(), language.getAll());

        final ObjectMapper mapper = new ObjectMapper();
        final ObjectWriter writer = mapper.writer();
        final String applicationJson;
        try {
            applicationJson = "\n(function(){\n\"use strict\";\nOJST.app=" + writer.writeValueAsString(application) + "}());\n";
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getLocalizedMessage()).build();
        }

        final String js = WebAssetsAggregator.newInstance(servletContext, "/ui/js/")
                .addResource("manifest.js")
                .addString(applicationJson)
                .addResource(preferences.getBoolean(Preferences.UI.SCRIPTS_DEBUG) ? "application-min.js" : "application.js")
                .aggregate();

        return Response.ok().entity(js).build();
    }

//    private boolean isRequireInvalidate(final String cacheControl, final String pragma) {
//        return (!StringUtils.isBlank(cacheControl) && cacheControl.toLowerCase().contains("no-cache"))
//                || (!StringUtils.isBlank(pragma) && pragma.toLowerCase().contains("no-cache"));
//    }

    private Response getAggregatedResources(final ServletContext servletContext, final String cacheControl, final String pragma,
                                            final int cacheKey, final String root, final String... resources) {

        final Cache<Integer, String> cache = cacheService.getWebUI();
//        if (isRequireInvalidate(cacheControl, pragma) || !cache.containsKey(cacheKey)) {
        final String content;
        if (!preferences.getBoolean(Preferences.UI.SCRIPTS_CACHE) || !cache.containsKey(cacheKey)) {
            content = WebAssetsAggregator.newInstance(servletContext, root).addResources(resources).aggregate();
            cache.put(cacheKey, content);
        } else {
            content = cache.get(cacheKey);
        }
        final boolean isCss = resources.length > 0 && resources[0].toLowerCase().endsWith(".css");
        return Response.ok(content).header("Content-Type", isCss ? "text/css" : "text/javascript").build();
//        }
//        return Response.notModified().build();
    }
}
