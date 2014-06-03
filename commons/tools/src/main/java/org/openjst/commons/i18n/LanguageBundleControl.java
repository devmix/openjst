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

import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Sergey Grachev
 */
final class LanguageBundleControl extends ResourceBundle.Control {
    private final Class clazz;

    public LanguageBundleControl(final Class clazz) {
        this.clazz = clazz;
    }

    private String extractRealBundleName(final String fullBundleName) {
        return fullBundleName.startsWith("<<") ?
                fullBundleName.substring(fullBundleName.indexOf(">>") + 2).trim() : fullBundleName;
    }

    @Override
    public List<String> getFormats(final String baseName) {
        return FORMAT_PROPERTIES;
    }

    @Override
    public ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {

        if (baseName == null || locale == null || format == null || loader == null) {
            throw new NullPointerException();
        }

        ResourceBundle bundle = null;
        if (format.equals("java.properties")) {
            final String bundleName = toBundleName(extractRealBundleName(baseName), locale);
            final String resourceName = toResourceName(bundleName, "properties");
            final URL url = getRelativeResource(clazz, resourceName);
            InputStream stream = null;
            if (url != null) {
                if (reload) {
                    final URLConnection connection = url.openConnection();
                    if (connection != null) {
                        // Disable caches to get fresh data for reloading.
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                } else {
                    stream = url.openStream();
                }

                if (stream != null) {
                    final BufferedInputStream bis = new BufferedInputStream(stream);
                    bundle = new LanguageBundle(bis);
                    bis.close();
                }
            }
        }

        return bundle;
    }

    @Nullable
    public static URL getRelativeResource(final Class clazz, final String name) {
        final String currentLocation = getClassLocation(clazz);
        final java.util.Enumeration<URL> resources;
        try {
            resources = clazz.getClassLoader().getResources(name);
            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                final String urlStr = url.toString();
                if ((urlStr.indexOf("jar:") == 0 && urlStr.indexOf(currentLocation) == 4)
                        || urlStr.indexOf(currentLocation) == 0) {
                    return url;
                }
            }
        } catch (IOException e) {
            // ignore
        }
        return null;
    }

    public static String getClassLocation(final Class clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().toExternalForm();
    }
}
