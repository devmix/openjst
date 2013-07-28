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

import org.mozilla.javascript.ContextFactory;
import org.openjst.server.commons.maven.utils.OptionGroup;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("UnusedDeclaration")
public enum CssCompressors {
    YUI {
        @Override
        public Compressor newInstance(final ContextFactory factory, final OptionGroup config) {
            return new org.openjst.server.commons.maven.compression.YUI(factory, config);
        }
    };

    public abstract Compressor newInstance(ContextFactory ctxFactory, OptionGroup config);
}
