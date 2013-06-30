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

/**
 * @author Sergey Grachev
 */
public final class UIAssets {

    public static final String PATH_LIB = "ui/lib/";
    public static final String PATH_ASSETS_CSS = "ui/assets/css/";

    public static final String[] CSS_COMMON = new String[]{
            "select2/select2.css"
    };
    public static final String[] CSS_ASSETS = new String[]{

    };
    public static final String[] JS_COMMON = new String[]{
            "jquery/jquery-1.8.3.min.js",
            "select2/select2.min.js",
            "yui/build/yui/yui.js",
            "bootstrap/js/bootstrap.min.js"
    };

    private UIAssets() {
    }

    public static String jspDefault(final String pageName) {
        return "<link href='rest/ui/assets/common.css' rel='stylesheet'>\n"
                +
                "<link href='" + PATH_LIB + "bootstrap/css/bootstrap.min.css' rel='stylesheet'>\n"
                +
                "<link href='" + PATH_ASSETS_CSS + "pages/" + pageName + ".css' rel='stylesheet'>\n"
                +
                "<link href='" + PATH_LIB + "bootstrap/css/bootstrap-responsive.min.css' rel='stylesheet'>\n"
                +
                "<!--[if lt IE 9]>\n" +
                "<script src='" + PATH_LIB + "html5shim/html5.js' type='text/javascript'></script>\n" +
                "<![endif]-->\n"
                +
                "<link href=\"ui/assets/ico/favicon.png\" rel=\"shortcut icon\">\n";
    }

    public static String jsDefault() {
        return "<script src='rest/ui/assets/common.js' type='text/javascript'></script>\n"
                + "<script src='rest/ui/assets/application.js' type='text/javascript'></script>\n";
    }
}
