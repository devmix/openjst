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

import org.lesscss.LessException;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.dto.tuples.Tuples;
import org.openjst.server.commons.maven.utils.Patterns;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
final class LessCompiler implements Compiler {

    private final org.lesscss.LessCompiler compiler = new org.lesscss.LessCompiler();

    @Override
    public String compile(final String data) throws CompilerException {
        try {
            return compiler.compile(data);
        } catch (LessException e) {
            throw new CompilerException(e);
        }
    }

    @Override
    public Pair<Set<File>, List<String>> compile(final File file) {
        final List<String> issues = new LinkedList<String>();
        final Set<File> files = new HashSet<File>(1);
        try {
            final File compiledFile = makeNameOfCompiledFile(file);
            compiler.compile(file, compiledFile);
            files.add(compiledFile);
        } catch (Exception e) {
            issues.add(e.getMessage());
        }
        return Tuples.newPair(files, issues);
    }

    @Override
    public boolean isSupportedSource(final File file) {
        return file.toString().toLowerCase().lastIndexOf(".less") != -1;
    }

    private static File makeNameOfCompiledFile(final File file) {
        return new File(file.toString().replaceFirst(Patterns.REPLACE_EXTENSION, ".css"));
    }
}
