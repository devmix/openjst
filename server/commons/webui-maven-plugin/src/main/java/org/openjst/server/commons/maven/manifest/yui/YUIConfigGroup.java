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

package org.openjst.server.commons.maven.manifest.yui;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openjst.server.commons.maven.manifest.YUI;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("UnusedDeclaration")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public final class YUIConfigGroup {

    private final boolean combine;
    private final String comboBase;
    private final String filter;
    private final String root = "";
    private final Map<String, YUIConfigGroupModule> modules = new TreeMap<String, YUIConfigGroupModule>();

    public YUIConfigGroup(final String comboBase, final boolean combine, final String filter) {
        this.combine = combine;
        this.comboBase = comboBase;
        this.filter = filter;
    }

    public YUIConfigGroupModule addJsModule(final String name, final String path) {
        final YUIConfigGroupModule module = new YUIConfigGroupModule(name, path, YUI.TYPE_JS);
        modules.put(name, module);
        return module;
    }

    public YUIConfigGroupModule addCssModule(final String projectNamespace, final String projectManifestObject, final String name, final String path) {
        final String fullName = projectNamespace + '.' + name + "Css";

        final YUIConfigGroupModule jsModule = modules.get(projectNamespace + '.' + name);
        if (jsModule != null && jsModule.isJavaScript()) {
            jsModule.addRequires(new YUIConfigGroupModule.Dependency(fullName, true));
        }

        final YUIConfigGroupModule module = new YUIConfigGroupModule(fullName, path, YUI.TYPE_CSS);

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

    public Map<String, YUIConfigGroupModule> getModules() {
        return modules;
    }

    @Override
    public String toString() {
        return "YUIConfigGroup{" +
                "combine=" + combine +
                ", comboBase='" + comboBase + '\'' +
                ", filter='" + filter + '\'' +
                ", modules=" + modules +
                '}';
    }

    public String getRoot() {
        return root;
    }
}
