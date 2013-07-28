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

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.mozilla.javascript.*;
import org.openjst.server.commons.maven.utils.OptionGroup;

import java.io.IOException;

/**
 * @author Sergey Grachev
 */
final class UglifyJS implements Compressor {

    public static final int DEFAULT_INDENT_LEVEL = 4;
    public static final String[] SCRIPTS = new String[]{
            "commons.js", "consolidator.js", "parse-js.js", "process.js", "squeeze-more.js", "uglify-js.js"};

    private final ContextFactory factory;
    private final OptionGroup options;
    private Context ctx = null;
    private ScriptableObject scope = null;
    private Function fnUglify = null;

    public UglifyJS(final ContextFactory factory, @Nullable final OptionGroup config) {
        this.factory = factory;
        this.options = defaultOptions().merge(config);
    }

    @Override
    public OptionGroup defaultOptions() {
        final OptionGroup result = new OptionGroup()
                .add("squeeze", true) // false
                .add("strict_semicolons", false) // false
                .add("lift_vars", false) // false
                .add("max_line_length", 32 * 1024); // 6000

        result.addGroup("mangle_options")
                .add("mangle", true)
                .add("toplevel", false) // true
                .add("defines", null)
                .add("except", null)
                .add("no_functions", false); // true

        result.addGroup("squeeze_options")
                .add("make_seqs", true)
                .add("dead_code", true)
                .add("no_warnings", false)
                .add("keep_comps", true)
                .add("unsafe", false);

        result.addGroup("gen_options")
                .add("indent_start", 0)
                .add("indent_level", DEFAULT_INDENT_LEVEL)
                .add("quote_keys", false)
                .add("space_colon", false)
                .add("beautify", false)
                .add("ascii_only", false)
                .add("inline_script", false);

        return result;
    }

    @Override
    public String compress(final String data) throws IOException {
        try {
            if (ctx == null) {
                ctx = Context.enter();
            } else {
                factory.enterContext(ctx);
            }

            if (scope == null) {
                initializeScope();
            }

            return (String) factory.call(new ContextAction() {
                @Override
                public Object run(final Context cx) {
                    return fnUglify.call(ctx, scope, scope, new Object[]{data, options.toJSObject(ctx, scope)});
                }
            });
        } finally {
            Context.exit();
        }
    }

    private void initializeScope() throws IOException {
        scope = ctx.initStandardObjects();
        for (final String script : SCRIPTS) {
            ctx.evaluateString(scope, load(script), script, 1, null);
        }
        fnUglify = (Function) scope.get("uglify");
    }

    private String load(final String name) throws IOException {
        return IOUtils.toString(Compressor.class.getClassLoader().getResourceAsStream("js/uglifyjs/" + name));
    }
}
