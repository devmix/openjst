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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.logging.Log;
import org.openjst.server.commons.maven.frameworks.Dependency;
import org.openjst.server.commons.maven.frameworks.Framework;
import org.openjst.server.commons.maven.frameworks.Frameworks;
import org.openjst.server.commons.maven.frameworks.Module;
import org.openjst.server.commons.maven.manifest.jaxb.FrameworkType;
import org.openjst.server.commons.maven.manifest.jaxb.ManifestType;
import org.openjst.server.commons.maven.manifest.jaxb.ParametersType;
import org.openjst.server.commons.maven.resources.*;
import org.openjst.server.commons.maven.utils.ManifestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public final class Manifest {

    private final Log log;
    private final ManifestResources resources;
    private final File manifestFile;

    public Manifest(final Log log, final String baseDir, final ManifestResources resources) {
        this.log = log;
        this.resources = resources;
        this.manifestFile = new File(baseDir, "manifest.js");
    }

    public void create(final ManifestType manifest) throws IOException {
        log.info("");
        log.info(String.format("Generate manifest '%s'...", manifestFile));

        final Framework framework = createFramework(manifest);

        addModules(framework);
        addLibraries(framework);

        final Parameters parameters = createParameters(manifest.getParameters(), framework);

        printModules(framework);

        final String parametersJson = new ObjectMapper().writer().writeValueAsString(parameters);
        final String frameworkJson = framework.getConfigAsJson(false);

        String template = IOUtils.toString(this.getClass().getResourceAsStream("/template.js"));
        template = template
                .replace("_NS_", parameters.getNamespace())
                .replace("_MANIFEST_", parametersJson)
                .replace("_FRAMEWORK_", frameworkJson);

        if (log.isDebugEnabled()) {
            log.debug("Parameters JSON: " + parametersJson);
            log.debug("Framework JSON: " + frameworkJson);
            log.debug("Manifest: " + manifest);
        }

        FileUtils.writeStringToFile(manifestFile, template);
    }

    private Parameters createParameters(final ParametersType parametersConfig, final Framework framework) {
        final String globalNamespace = parametersConfig.getNamespace();

        final Parameters parameters = new Parameters(
                globalNamespace, parametersConfig.getProject(), parametersConfig.getVersion());

        for (final Module module : framework.getModules()) {
            final String relativeNamespace = module.getName().substring(globalNamespace.length() + 1);
            parameters.addModuleName(relativeNamespace);
        }

        return parameters;
    }

    private void addLibraries(final Framework framework) throws IOException {
        final Libraries libraries = resources.getLibraries();
        if (libraries.isEmpty()) {
            return;
        }

        final String librariesNamespace = libraries.getNamespace();
        for (final LibraryResources libraryResources : libraries.getResources()) {
            if (libraryResources.isEmpty()) {
                continue;
            }

            final String root = ManifestUtils.relativePath(libraries.getBaseDir(), libraryResources.getRoot());

            if (libraryResources.getJoinSourcesAs() != null) {
                framework.addLibraryResource(ResourceType.JAVA_SCRIPT,
                        librariesNamespace, libraryResources.getName(), root, libraryResources.getJoinSourcesAs());
            }

            if (libraryResources.getJoinStylesAs() != null) {
                framework.addLibraryResource(ResourceType.CSS,
                        librariesNamespace, libraryResources.getName(), root, libraryResources.getJoinStylesAs());
            }
        }
    }

    private void addModules(final Framework framework) throws IOException {
        final ModuleResources moduleResources = resources.getModuleResources();
        if (moduleResources.isEmpty()) {
            return;
        }

        framework.addResources(ResourceType.JAVA_SCRIPT, moduleResources.getNamespace(),
                moduleResources.getRelativeSourcesRoot(), moduleResources.getResources());

        framework.addResources(ResourceType.CSS, moduleResources.getNamespace(),
                moduleResources.getRelativeStylesRoot(), moduleResources.getStyleResources());
    }

    private void printModules(final Framework framework) {
        log.info("  Modules:");
        for (final Module module : framework.getModules()) {
            log.info(String.format("    %s (%s)", module.getName(), module.getPath()));
            final Set<? extends Dependency> dependencies = module.getDependencies();
            if (dependencies != null) {
                final StringBuilder sb = new StringBuilder();
                for (final Dependency dependency : dependencies) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(dependency.getName());
                }
                log.info("      " + sb);
            }
        }
    }

    private Framework createFramework(final ManifestType manifest) {
        final FrameworkType manifestFramework = manifest.getFramework();
        final Frameworks type = Frameworks.valueOf(manifestFramework.getType().toUpperCase());
        return type.newInstance(manifest.getParameters().getNamespace(), manifestFramework);
    }
}
