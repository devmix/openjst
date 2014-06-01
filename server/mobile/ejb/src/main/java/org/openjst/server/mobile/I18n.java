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

package org.openjst.server.mobile;

import org.openjst.commons.cache.CacheOnDemand;
import org.openjst.commons.cache.Caches;
import org.openjst.commons.i18n.Language;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class I18n {

    private static final String RESOURCE_PATH = "i18n/language";
    private static final Locale DEFAULT_LOCALE = new Locale("en");
    private static final Language DEFAULT_LANGUAGE = new Language(DEFAULT_LOCALE, RESOURCE_PATH, I18n.class);
    private static final CacheOnDemand<String, Locale, Void> LOCALES_CACHE = Caches.newCacheOnDemandLRU(new CacheOnDemand.Listener<String, Locale, Void>() {
        @Nullable
        @Override
        public Locale onFetch(final String code, @Nullable final Void userData) {
            return new Locale(code);
        }
    }, 8, false);
    private static final CacheOnDemand<Locale, Language, Void> LANGUAGES_CACHE = Caches.newCacheOnDemandLRU(new CacheOnDemand.Listener<Locale, Language, Void>() {
        @Nullable
        @Override
        public Language onFetch(final Locale locale, @Nullable final Void userData) {
            return new Language(locale, RESOURCE_PATH, I18n.class);
        }
    }, 8, false);

    private static Locale defaultLocale = DEFAULT_LOCALE;

    private I18n() {
    }

    public static Language get() {
        return get(defaultLocale);
    }

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    public static void setDefaultLocale(@Nullable final String code, final boolean updateSystem) {
        defaultLocale = code == null ? DEFAULT_LOCALE : LOCALES_CACHE.get(code);
        if (updateSystem) {
            Locale.setDefault(defaultLocale);
        }
    }

    public static Locale getLocale(@Nullable final String code) {
        return code == null ? DEFAULT_LOCALE : LOCALES_CACHE.get(code);
    }

    public static Language get(@Nullable final Locale locale) {
        return locale == null ? DEFAULT_LANGUAGE : LANGUAGES_CACHE.get(locale);
    }

    @Nullable
    public static String getString(final String key) {
        return get().getString(key);
    }

    public static Map<String, String> getAll(final String locale) {
        return get(getLocale(locale)).getAll();
    }
}
