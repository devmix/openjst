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

package org.openjst.server.commons.web.utils;

import org.apache.commons.io.IOUtils;
import org.openjst.commons.dto.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class WebAssetsAggregator {

    private static final Logger LOG = LoggerFactory.getLogger(WebAssetsAggregator.class);

    private final ServletContext servletContext;
    private final String defaultRoot;
    private final Map<String, Type> resources = new LinkedHashMap<String, Type>();

    private WebAssetsAggregator(final ServletContext servletContext, final String defaultRoot) {
        this.servletContext = servletContext;
        this.defaultRoot = defaultRoot;
    }

    private WebAssetsAggregator(final ServletContext servletContext) {
        this(servletContext, Constants.stringsEmpty());
    }

    public static WebAssetsAggregator newInstance(final ServletContext servletContext) {
        return new WebAssetsAggregator(servletContext);
    }

    public static WebAssetsAggregator newInstance(final ServletContext servletContext, final String root) {
        return new WebAssetsAggregator(servletContext, root);
    }

    public WebAssetsAggregator addResource(final String root, final String resource) {
        resources.put(root + resource, Type.FILE);
        return this;
    }

    public WebAssetsAggregator addResource(final String resource) {
        return addResource(defaultRoot, resource);
    }

    public WebAssetsAggregator addResources(final String... resources) {
        for (final String resource : resources) {
            addResource(defaultRoot, resource);
        }
        return this;
    }

    public WebAssetsAggregator addString(final String value) {
        resources.put(value, Type.STRING);
        return this;
    }

    public String aggregate() {
        return aggregate("\n");
    }

    public String aggregate(final String endOfResourceMark) {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, Type> resource : resources.entrySet()) {
            final String content = resource.getKey();
            switch (resource.getValue()) {
                case FILE: {
                    final InputStream is = servletContext.getResourceAsStream(content);
                    if (is != null) {
                        try {
                            sb.append(IOUtils.toString(is)).append(endOfResourceMark);
                        } catch (IOException e) {
                            LOG.error("Can't read resource", e);
                        } finally {
                            try {
                                is.close();
                            } catch (IOException ignore) {
                            }
                        }
                    } else {
                        LOG.error("Resource '{}' not found", defaultRoot + content);
                    }
                    break;
                }
                case STRING: {
                    sb.append(content).append("\n");
                }
            }
        }
        return sb.toString();
    }

    private static enum Type {
        FILE, STRING
    }
}
