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

package org.openjst.commons.i18n;

/**
 * @author Sergey Grachev
 */
public final class ExpressionEvaluateException extends RuntimeException {

    private static final long serialVersionUID = 7993014547945082134L;

    private final String key;

    public ExpressionEvaluateException(final String msg, final String key) {
        super(msg);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
