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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author Sergey Grachev
 */
public final class LanguageBundle extends ResourceBundle {

    private final Properties properties;

    public LanguageBundle(final BufferedInputStream stream) throws IOException {
        properties = new Properties();
        properties.load(stream);
    }

    @Override
    protected Object handleGetObject(final String key) {
        synchronized (properties) {
            final Object o = properties.get(key);
            if (o != null && o instanceof String && ((String) o).contains("${")) {
                final String v = evaluate((String) o);
                properties.setProperty(key, v);
            }
        }
        return properties.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return new Enumeration<String>() {
            private final Enumeration<Object> keys = properties.keys();

            @Override
            public boolean hasMoreElements() {
                return keys.hasMoreElements();
            }

            @Override
            public String nextElement() {
                return keys.nextElement().toString();
            }
        };
    }

    private String evaluate(final String expr) {
        final StringBuilder value = new StringBuilder();
        int curIdx = 0;
        int varStart;
        while ((varStart = expr.indexOf("${", curIdx)) != -1) {
            if (varStart == 0 || (varStart > 0 && expr.charAt(varStart - 1) != '\\')) {
                final int varEnd = expr.indexOf("}", varStart);
                if (varEnd != -1 && varEnd > varStart + 2) {
                    final String k = expr.substring(varStart + 2, varEnd);
                    final String v = getString(k);
                    value.append(expr.substring(curIdx, varStart));
                    if (v != null) {
                        value.append(v);
                    } else {
                        final String msg = String.format("no value for \"%s\" in \"%s\"", k, expr);
                        throw new ExpressionEvaluateException(msg, k);
                    }
                    curIdx = varEnd + 1;  // skip trailing "}"
                } else {
                    final String msg = String.format("no closing brace in \"%s\"", expr);
                    throw new ExpressionEvaluateException(msg, "<not found>");
                }
            } else {  // we've found an escaped variable - "\${"
                value.append(expr.substring(curIdx, varStart - 1));
                value.append("${");
                curIdx = varStart + 2; // skip past "${"
            }
        }
        value.append(expr.substring(curIdx));
        return value.toString();
    }
}
