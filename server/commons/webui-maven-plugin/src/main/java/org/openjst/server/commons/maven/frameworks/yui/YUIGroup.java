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

package org.openjst.server.commons.maven.frameworks.yui;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("UnusedDeclaration")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
final class YUIGroup {

    private final boolean combine;
    private final String comboBase;
    private final String filter;
    private final String root;
    private final Map<String, YUIModule> modules = new TreeMap<String, YUIModule>();

    public YUIGroup(final String comboBase, final boolean combine, final String filter) {
        this.combine = combine;
        this.comboBase = comboBase;
        this.filter = filter;
        root = "";
    }

    public YUIModule addJsModule(final String name, final String path) {
        final YUIModule module = new YUIModule(name, path, YUI.TYPE_JS);
        modules.put(name, module);
        return module;
    }

    public YUIModule addCssModule(final String name, final String path) {
        final String fullName = name + "Css";

        final YUIModule jsModule = modules.get(name);
        if (jsModule != null && jsModule.isJavaScript()) {
            jsModule.addRequires(new YUIDependency(fullName, true));
        }

        final YUIModule module = new YUIModule(fullName, path, YUI.TYPE_CSS);

        modules.put(fullName, module);

        return module;
    }

    public boolean isCombine() {
        return combine;
    }

    public String getFilter() {
        return filter;
    }

    public String getComboBase() {
        return comboBase;
    }

    public Map<String, YUIModule> getModules() {
        return modules;
    }

    public String getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return "YUIGroup{" +
                "combine=" + combine +
                ", comboBase='" + comboBase + '\'' +
                ", filter='" + filter + '\'' +
                ", root='" + root + '\'' +
                ", modules=" + modules +
                '}';
    }
}
