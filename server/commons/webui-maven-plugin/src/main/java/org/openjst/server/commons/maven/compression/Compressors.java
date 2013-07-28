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

package org.openjst.server.commons.maven.compression;

import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.ContextFactory;
import org.openjst.server.commons.maven.manifest.jaxb.CompressorType;
import org.openjst.server.commons.maven.manifest.jaxb.CompressorsType;
import org.openjst.server.commons.maven.resources.ResourceType;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class Compressors {

    private final List<Compressor> js = new LinkedList<Compressor>();
    private final List<Compressor> css = new LinkedList<Compressor>();
    private final Log log;
    private final ContextFactory ctx;

    public Compressors(final Log log) {
        this.log = log;
        this.ctx = ContextFactory.getGlobal();
    }

    public Compressors initialize(final CompressorsType compressors) {
        if (compressors == null) {
            return this;
        }

        log.info("");
        log.info("Initialize compressors...");

        for (final CompressorType item : compressors.getCompressor()) {
            final String type = item.getType();
            if (item.isSkip()) {
                log.info("\tCompressor " + type + " skipped");
            }

            final ResourceType resourceType = ResourceType.of(item.getFor());
            final Compressor compressor = resourceType.createCompressor(type, ctx, null);

            switch (resourceType) {
                case JAVA_SCRIPT:
                    js.add(compressor);
                    break;
                case CSS:
                    css.add(compressor);
                    break;
            }

            if (log.isDebugEnabled()) {
                log.debug(String.format("\tCompressor '%s' created", type));
            }
        }

        return this;
    }

    public List<Compressor> forJavaScript() {
        return js;
    }

    public List<Compressor> forCss() {
        return css;
    }
}
