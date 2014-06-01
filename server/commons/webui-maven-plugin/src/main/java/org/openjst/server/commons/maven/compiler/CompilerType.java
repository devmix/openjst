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

import org.mozilla.javascript.ContextFactory;
import org.openjst.server.commons.maven.utils.OptionGroup;

import javax.annotation.Nullable;
import java.io.File;

/**
 * @author Sergey Grachev
 */
public enum CompilerType {
    LESS {
        @Override
        public Compiler newInstance(final ContextFactory ctxFactory, final OptionGroup config) {
            return new LessCompiler();
        }
    };

    public abstract Compiler newInstance(ContextFactory ctxFactory, OptionGroup config);

    @Nullable
    public CompilerType typeOf(final File file) {
        if (file.toString().toLowerCase().lastIndexOf(".less") != -1) {
            return LESS;
        }
        return null;
    }
}
