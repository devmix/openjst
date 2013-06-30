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

package org.openjst.server.commons.utils;

import org.jetbrains.annotations.Nullable;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Sergey Grachev
 */
public final class EjbLocator {

    public static final String GLOBAL_SERVER_MOBILE_MOBILE_EJB = "global/server-mobile/mobile-ejb/";
    public static final String GLOBAL_SERVER_MOBILE_COMMONS_EJB = "global/server-mobile/commons-ejb/";

    private EjbLocator() {
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T lookupGlobal(final Class clazz) {
        final String prefix = getGlobalPrefix(clazz);

        final InitialContext ctx;
        try {
            ctx = new InitialContext();
        } catch (NamingException e) {
            return null;
        }

        final String name = clazz.getSimpleName();
        try {
            return (T) ctx.lookup(prefix + name);
        } catch (Exception ignore) {
        }

        return null;
    }

    private static String getGlobalPrefix(final Class clazz) {
        final String fullName = clazz.getName();
        final String prefix;
        if (fullName.startsWith("org.openjst.server.commons")) {
            prefix = GLOBAL_SERVER_MOBILE_COMMONS_EJB;
        } else {
            prefix = GLOBAL_SERVER_MOBILE_MOBILE_EJB;
        }
        return prefix;
    }
}
