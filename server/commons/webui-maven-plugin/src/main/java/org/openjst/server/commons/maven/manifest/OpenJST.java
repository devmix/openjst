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

package org.openjst.server.commons.maven.manifest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("UnusedDeclaration")
@JsonPropertyOrder({"NAMESPACE", "VERSION", "PROJECT_NAME", "modules", "ui"})
public final class OpenJST {

    @JsonProperty("NAMESPACE")
    private String namespace;

    @JsonProperty("VERSION")
    private String version;

    @JsonProperty("PROJECT_NAME")
    private String projectName;

    @JsonProperty("modules")
    private final Map<String, Object> modules = new TreeMap<String, Object>();

    @JsonProperty("ui")
    private final Map<String, Object> ui = new TreeMap<String, Object>();

    public OpenJST() {
    }

    public OpenJST(final String namespace) {
        this.namespace = namespace;
    }

    @SuppressWarnings("unchecked")
    public void addModuleName(final String name) {
        final String[] parts = name.split("\\.");
        if (parts.length == 1) {
            modules.put(name, namespace + '.' + name);
        } else {
            Map<String, Object> module = modules;
            Map<String, Object> uiNode = ui;
            for (int i = 0, length = parts.length; i < length; i++) {
                final String part = parts[i];
                if (i + 1 < length) {
                    if (!module.containsKey(part)) {
                        module.put(part, module = new LinkedHashMap<String, Object>());
                        uiNode.put(part, uiNode = new LinkedHashMap<String, Object>());
                    } else {
                        module = (Map<String, Object>) module.get(part);
                        uiNode = (Map<String, Object>) uiNode.get(part);
                    }
                } else {
                    module.put(part, namespace + '.' + name);
                }
            }
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public Map<String, Object> getModules() {
        return modules;
    }

    public Map<String, Object> getUi() {
        return ui;
    }

    @Override
    public String toString() {
        return "OJST{" +
                "namespace='" + namespace + '\'' +
                ", version='" + version + '\'' +
                ", projectName='" + projectName + '\'' +
                ", modules=" + modules +
                ", ui=" + ui +
                '}';
    }
}
