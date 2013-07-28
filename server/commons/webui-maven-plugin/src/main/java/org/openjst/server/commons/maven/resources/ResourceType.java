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

package org.openjst.server.commons.maven.resources;

import org.mozilla.javascript.ContextFactory;
import org.openjst.server.commons.maven.compression.Compressor;
import org.openjst.server.commons.maven.compression.CssCompressors;
import org.openjst.server.commons.maven.compression.JsCompressors;
import org.openjst.server.commons.maven.utils.OptionGroup;
import org.openjst.server.commons.maven.validation.JsValidators;
import org.openjst.server.commons.maven.validation.Validator;

/**
 * @author Sergey Grachev
 */
public enum ResourceType {

    JAVA_SCRIPT {
        @Override
        public Compressor createCompressor(final String type, final ContextFactory ctx, final OptionGroup options) {
            return JsCompressors.valueOf(type.toUpperCase()).newInstance(ctx, null);
        }

        @Override
        public Validator createValidator(final String type, final ContextFactory ctx, final OptionGroup options) {
            return JsValidators.valueOf(type.toUpperCase()).newInstance();
        }
    },

    CSS {
        @Override
        public Compressor createCompressor(final String type, final ContextFactory ctx, final OptionGroup options) {
            return CssCompressors.valueOf(type.toUpperCase()).newInstance(ctx, null);
        }

        @Override
        public Validator createValidator(final String type, final ContextFactory ctx, final OptionGroup options) {
            throw new UnsupportedOperationException();
        }
    };

    public static ResourceType of(final String value) {
        if ("javascript".equalsIgnoreCase(value)) {
            return JAVA_SCRIPT;
        } else if ("css".equalsIgnoreCase(value)) {
            return CSS;
        }
        throw new IllegalArgumentException("Unknown source type " + value);
    }

    public abstract Compressor createCompressor(String type, ContextFactory ctx, OptionGroup options);

    public abstract Validator createValidator(String type, ContextFactory ctx, OptionGroup options);
}
