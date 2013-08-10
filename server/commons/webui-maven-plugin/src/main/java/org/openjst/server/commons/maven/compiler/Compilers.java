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

package org.openjst.server.commons.maven.compiler;

import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.ContextFactory;
import org.openjst.server.commons.maven.manifest.jaxb.CompilersType;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class Compilers {

    private final Map<CompilerType, Compiler> map = new LinkedHashMap<CompilerType, Compiler>();
    private final Log log;
    private final ContextFactory ctx;

    public Compilers(final Log log) {
        this.log = log;
        this.ctx = ContextFactory.getGlobal();
    }

    public Compilers initialize(final CompilersType compilers) {
        if (compilers == null) {
            return this;
        }

        for (final org.openjst.server.commons.maven.manifest.jaxb.CompilerType item : compilers.getCompiler()) {
            final String type = item.getType();
            if (item.isSkip()) {
                if (log.isDebugEnabled()) {
                    log.debug("\tCompiler " + type + " skipped");
                }
                continue;
            }

            final CompilerType compilerType = CompilerType.valueOf(type.toUpperCase());

            map.put(compilerType, compilerType.newInstance(ctx, null));

            if (log.isDebugEnabled()) {
                log.debug(String.format("\tCompressor '%s' created", type));
            }
        }

        return this;
    }

    public Collection<Compiler> getList() {
        return map.values();
    }
}
