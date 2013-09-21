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

package org.openjst.server.mobile.web;

import org.openjst.server.commons.model.types.MobileClientOS;
import org.openjst.server.commons.web.qualifiers.UIEnum;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class UIResources {

    public static final String PATH_UI = "ui/";
    public static final String PATH_LIB = "ui/lib/";
    public static final String PATH_CORE = "ui/core/";

    @Produces
    @ApplicationScoped
    @UIEnum
    public static final List<Class> UI_ENUMS = Arrays.asList(
            (Class) MobileClientOS.class
    );

    public static String jspDefault() {
        return "<title>OpenJST</title>\n"
                +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                +
                "<!--[if lt IE 9]>\n" +
                "<script src='" + PATH_LIB + "html5shim/html5shiv-3.6.2-min.js' type='text/javascript'></script>\n" +
                "<![endif]-->\n"
                +
                "<link href=\"ui/assets/ico/favicon.png\" rel=\"shortcut icon\">\n";
    }

    public static String jsDefault() {
        return "<script src='ui-api/assets/config.js' type='text/javascript'></script>\n"
                + "<script src='ui-api/assets/core.js' type='text/javascript'></script>\n";
    }
}
