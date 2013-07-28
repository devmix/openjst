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

package org.openjst.server.commons.maven.validation;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Grachev
 */
final class JSLint implements Validator {

    private final com.googlecode.jslint4java.JSLint jsLint = new JSLintBuilder().fromDefault();

    @Override
    public List<String> validate(final String data) {
        jsLint.addOption(Option.UNPARAM, Boolean.TRUE.toString());
        jsLint.addOption(Option.SLOPPY, Boolean.TRUE.toString());
        final List<String> result = new ArrayList<String>();
        final JSLintResult jsLintResult = jsLint.lint("#", data);
        for (final Issue issue : jsLintResult.getIssues()) {
            result.add(String.format("%d,%d: %s", issue.getLine(), issue.getCharacter(), issue.getReason()));
        }
        return result;
    }
}
