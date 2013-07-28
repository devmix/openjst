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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.openjst.server.commons.maven.frameworks.Framework;
import org.openjst.server.commons.maven.frameworks.Module;
import org.openjst.server.commons.maven.manifest.jaxb.FrameworkCoreGroupType;
import org.openjst.server.commons.maven.manifest.jaxb.FrameworkGroupType;
import org.openjst.server.commons.maven.manifest.jaxb.FrameworkGroupsType;
import org.openjst.server.commons.maven.manifest.jaxb.FrameworkType;
import org.openjst.server.commons.maven.resources.Resource;
import org.openjst.server.commons.maven.resources.ResourceType;
import org.openjst.server.commons.maven.utils.ManifestUtils;
import org.openjst.server.commons.maven.utils.Patterns;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author Sergey Grachev
 */
public final class YUI implements Framework {

    public static final String TYPE_JS = "js";
    public static final String TYPE_CSS = "css";
    private final String globalNamespace;
    private final YUIConfig core;
    private final Map<YUIGroup, Set<String>> groupToNamespaces = new LinkedHashMap<YUIGroup, Set<String>>(2);
    private final List<Module> modules = new LinkedList<Module>();

    public YUI(final String globalNamespace, final FrameworkType frameworkConfig) {
        this.globalNamespace = globalNamespace;

        final FrameworkCoreGroupType coreConfig = frameworkConfig.getCore();

        core = new YUIConfig(coreConfig.getComboServiceUrl(), coreConfig.isDebug(), coreConfig.isCombine(), null);

        final FrameworkGroupsType groupsConfig = frameworkConfig.getGroups();
        if (groupsConfig != null) {
            for (final FrameworkGroupType groupConfig : groupsConfig.getGroup()) {
                final YUIGroup group = core.addGroup(
                        groupConfig.getName(), groupConfig.getComboServiceUrl(), groupConfig.isCombine(), null);

                groupToNamespaces.put(group,
                        new HashSet<String>(groupConfig.getNamespaces().getNamespace()));
            }
        }
    }

    @Override
    public boolean addResources(final ResourceType type, final String namespace, final String root, final Set<? extends Resource> resources) throws IOException {
        final YUIGroup group = findGroup(namespace);
        if (group == null) {
            return false;
        }

        for (final Resource resource : resources) {
            final String name = resource.getRelativePath();
            final String moduleName = ManifestUtils.makeModuleName(name);
            final String path = root + File.separator
                    + (resource.isCompressed() ? resource.getCompressedRelativePath() : resource.getRelativePath());

            final YUIModule module;
            switch (type) {
                case JAVA_SCRIPT:
                    module = group.addJsModule(makeGlobalModuleName(namespace, moduleName), path)
                            .addRequires(findDependencies(resource.getContent()));
                    break;

                case CSS:
                    module = group.addCssModule(makeGlobalModuleName(namespace, moduleName), path);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown resource type " + type);
            }

            modules.add(module);
        }

        return true;
    }

    @Override
    public boolean addLibraryResource(final ResourceType type, final String namespace, final String name, final String root, final Resource resource) {
        final YUIGroup group = findGroup(namespace);
        if (group == null) {
            return false;
        }

        final String path = root + File.separator
                + (resource.isCompressed() ? resource.getCompressedRelativePath() : resource.getRelativePath());

        final YUIModule module;
        switch (type) {
            case JAVA_SCRIPT:
                module = group.addJsModule(makeGlobalModuleName(namespace, name), path);
                break;

            case CSS:
                module = group.addCssModule(makeGlobalModuleName(namespace, name), path);
                break;

            default:
                throw new IllegalArgumentException("Unknown resource type " + type);
        }

        modules.add(module);

        return true;
    }

    @Override
    public String getConfigAsJson(final boolean formatted) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectWriter writer = formatted ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer();
        return writer.writeValueAsString(core);
    }

    @Override
    public List<? extends Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    private String makeGlobalModuleName(final String namespace, final String moduleName) {
        return globalNamespace + '.' + namespace + '.' + moduleName;
    }

    @Nullable
    private YUIGroup findGroup(final String namespace) {
        for (final Map.Entry<YUIGroup, Set<String>> entry : groupToNamespaces.entrySet()) {
            if (entry.getValue().contains(namespace)) {
                return entry.getKey();
            }
        }
        return null;
    }

    static Set<YUIDependency> findDependencies(final String content) {
        if (!StringUtils.isBlank(content)) {
            final Matcher matcher = Patterns.MODULE_REQUIRES.matcher(content);
            if (matcher.matches()) {
                final Set<YUIDependency> dependencies = new LinkedHashSet<YUIDependency>();
                final String dependenciesJs = matcher.group(1).replaceAll("\\s", "").trim();
                for (final String part : Patterns.MODULE_REQUIRES_SPLITTER.split(dependenciesJs)) {
                    final String dependency = StringUtils.trim(part);
                    final boolean isObject = dependency.length() > 0
                            && dependency.charAt(0) != '\'' && dependency.charAt(0) != '"';
                    dependencies.add(new YUIDependency(dependency, isObject));
                }
                return dependencies;
            }
        }
        return Collections.emptySet();
    }

    @SuppressWarnings("UnusedDeclaration")
    public static final class FrameworkConfig {

        @JsonProperty("config")
        private YUIConfig config;

        public YUIConfig getConfig() {
            return config;
        }

        public void setConfig(final YUIConfig config) {
            this.config = config;
        }

        @Override
        public String toString() {
            return "YUI{" +
                    "config=" + config +
                    '}';
        }
    }
}
