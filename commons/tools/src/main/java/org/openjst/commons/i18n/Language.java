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

import java.util.*;

/**
 * @author Sergey Grachev
 */
public class Language {

    private String bundleName;
    private ResourceBundle bundle;
    private Class relativeToClass = null;

    public Language(final Locale locale, final String bundleName, final Class relativeToClass) {
        this.bundleName = bundleName;
        this.relativeToClass = relativeToClass;
        load(locale);
    }

    public Language(final String bundleName) {
        this.bundleName = bundleName;
        this.relativeToClass = Language.class;
        load(Locale.getDefault());
    }

    private String makeUniqueBundleName() {
        return String.format("<<%s>>%s", getClassLocation(this.relativeToClass).hashCode(), this.bundleName);
    }

    private void load(final Locale locale) {
        this.bundle = ResourceBundle.getBundle(makeUniqueBundleName(), locale, new LanguageBundleControl(this.relativeToClass));
        if (this.bundle == null) {
            throw new IllegalArgumentException("Language bundle could not found");
        }
    }

    public String getString(final String key) {
        return bundle.getString(key);
    }

    public static String getClassLocation(final Class clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().toExternalForm();
    }

    public Map<String, String> getAll() {
        final Map<String, String> result = new HashMap<String, String>();
        final Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            result.put(key, bundle.getString(key));
        }
        return result;
    }
}
