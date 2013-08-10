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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.infinispan.Cache;
import org.openjst.commons.dto.properties.GroupedProperties;
import org.openjst.commons.dto.properties.PropertiesFactory;
import org.openjst.commons.i18n.Language;
import org.openjst.server.commons.services.PreferencesManager;
import org.openjst.server.commons.web.utils.WebAssetsAggregator;
import org.openjst.server.mobile.I18n;
import org.openjst.server.mobile.Preferences;
import org.openjst.server.mobile.cdi.beans.MobileSession;
import org.openjst.server.mobile.mq.model.UIConfigModel;
import org.openjst.server.mobile.services.CacheService;
import org.openjst.server.mobile.web.rest.Assets;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import static org.openjst.server.mobile.web.UIAssets.*;

/**
 * @author Sergey Grachev
 */
public class AssetsImpl implements Assets {

    private static final String CACHE_COMMON_JS = "common.js";
    private static final Comparator<String> RESOURCE_COMPARATOR = new Comparator<String>() {
        @SuppressWarnings("ComparatorMethodParameterNotUsed")
        @Override
        public int compare(final String o1, final String o2) {
            return o1.indexOf("lib/") == 0 ? -1 : 0;
        }
    };

    @EJB
    private CacheService cacheService;

    @EJB
    private PreferencesManager preferences;

    @Inject
    private MobileSession session;

    public static String extJs(final String fileName, final boolean debug) {
        return (debug ? fileName : fileName + "-min") + ".js";
    }

    @Override
    public Response coreJs(final ServletContext servletContext, final String cacheControl, final String pragma) {
        final Cache<String, String> cache = cacheService.getWebUI();
        final boolean debug = preferences.getBoolean(Preferences.UI.SCRIPTS_DEBUG);
        final boolean useCache = preferences.getBoolean(Preferences.UI.SCRIPTS_CACHE);

        final String commonJs;
        if (!useCache || !cache.containsKey(CACHE_COMMON_JS)) {
            commonJs = WebAssetsAggregator.newInstance(servletContext)
                    .addResource('/' + PATH_LIB + extJs("yui/build/yui/yui", debug))
                    .addResource('/' + PATH_LIB + extJs("jquery/jquery-1.8.3", debug))
                    .addResource('/' + PATH_UI + "manifest.js")
                    .addResource('/' + PATH_CORE + extJs("core-all", debug))
                    .aggregate();
            cache.put(CACHE_COMMON_JS, commonJs);
        } else {
            commonJs = cache.get(CACHE_COMMON_JS);
        }

        return Response.ok().entity(commonJs).build();
    }

    @Override
    public Response yuiFrameworkCombo(final UriInfo uriInfo, final ServletContext servletContext, final String cacheControl, final String pragma) {
        final Set<String> resourcesSet = uriInfo.getQueryParameters().keySet();
        final String[] resources = resourcesSet.toArray(new String[resourcesSet.size()]);
        final String cacheKey = Arrays.toString(resources);
        return getAggregatedResources(servletContext, cacheControl, pragma, cacheKey, '/' + PATH_LIB + "yui/build/",
                resources
        );
    }

    @Override
    public Response yuiUiCombo(final UriInfo uriInfo, final ServletContext servletContext, final String cacheControl, final String pragma) {
        final Set<String> resourcesSet = uriInfo.getQueryParameters().keySet();
        final String[] resources = resourcesSet.toArray(new String[resourcesSet.size()]);
        final String cacheKey = Arrays.toString(resources);
        return getAggregatedResources(servletContext, cacheControl, pragma, cacheKey, '/' + PATH_UI,
                resources
        );
    }

    @Override
    public Response configJs(final HttpServletRequest request, final ServletContext servletContext) {
        final String config;
        try {
            config = getConfigJson();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getLocalizedMessage()).build();
        }
        return Response.ok().entity("(function(){OJST={CONFIG:" + config + ",core:{}}})();").build();
    }

    private String getConfigJson() throws JsonProcessingException {
        // TODO split localization keys into groups by '.', ex. i18n: {ui:{label:{'sing-in':'Sing in','password':'Password'}}}

        final Language language = I18n.get(this.session.getLocale());

        final GroupedProperties<String, String, Object> properties = PropertiesFactory.newHashMapGroupedProperties();
        properties.ensureGroup("ui").ensureGroup("scripts")
                .set("debug", preferences.get(Preferences.UI.SCRIPTS_DEBUG))
                .set("combine", true);

        final UIConfigModel application = new UIConfigModel(this.session.getUser(), language.getAll(), properties);

        return new ObjectMapper().writer().writeValueAsString(application);
    }

//    private boolean isRequireInvalidate(final String cacheControl, final String pragma) {
//        return (!StringUtils.isBlank(cacheControl) && cacheControl.toLowerCase().contains("no-cache"))
//                || (!StringUtils.isBlank(pragma) && pragma.toLowerCase().contains("no-cache"));
//    }

    private Response getAggregatedResources(final ServletContext servletContext, final String cacheControl, final String pragma,
                                            final String cacheKey, final String root, final String... resources) {

        final Cache<String, String> cache = cacheService.getWebUI();
//        if (isRequireInvalidate(cacheControl, pragma) || !cache.containsKey(cacheKey)) {
        final boolean isCss = resources.length > 0 && resources[0].toLowerCase().endsWith(".css");
        final String content;
        if (!preferences.getBoolean(Preferences.UI.SCRIPTS_CACHE) || !cache.containsKey(cacheKey)) {
            // hack, YUI does not sort correctly modules
            Arrays.sort(resources, 0, resources.length, RESOURCE_COMPARATOR);
            content = WebAssetsAggregator.newInstance(servletContext, root).addResources(resources)
                    .aggregate(isCss ? "\n" : ";\n");
            cache.put(cacheKey, content);
        } else {
            content = cache.get(cacheKey);
        }
        return Response.ok(content).header("Content-Type", isCss ? "text/css" : "text/javascript").build();
//        }
//        return Response.notModified().build();
    }
}
