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

package org.openjst.server.commons.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.openjst.server.commons.maven.compression.Compressors;
import org.openjst.server.commons.maven.manifest.Manifest;
import org.openjst.server.commons.maven.manifest.jaxb.ManifestType;
import org.openjst.server.commons.maven.resources.ManifestResources;
import org.openjst.server.commons.maven.utils.ManifestUtils;
import org.openjst.server.commons.maven.validation.Validators;

import java.io.File;

/**
 * Lifecycle: validate, compress, generate manifest
 *
 * @author Sergey Grachev
 */
@Mojo(name = "compile",
        defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
        threadSafe = true)
public class CompileMojo extends AbstractMojo {

    @Parameter(property = "manifest", required = true, defaultValue = "${basedir}/src/main/webapp/ui/manifest.xml")
    private File manifestFile;

    @SuppressWarnings("UnusedDeclaration")
    public CompileMojo() {
    }

    public CompileMojo(final File manifestFile) {
        this.manifestFile = manifestFile;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!manifestFile.exists()) {
            getLog().warn(String.format("Manifest config not exists '%s'...", manifestFile));
            return;
        }

        getLog().info(String.format("Parse manifest '%s'...", manifestFile));

        final ManifestType manifestConfig;
        try {
            manifestConfig = ManifestUtils.parse(manifestFile);
        } catch (Exception e) {
            throw new MojoExecutionException("Error while read manifest", e);
        }

        sleep();

        final Compressors compressors;
        try {
            compressors = new Compressors(getLog()).initialize(manifestConfig.getCompressors());
        } catch (Exception e) {
            throw new MojoExecutionException("Error while initialize compressors", e);
        }

        sleep();

        final Validators validators;
        try {
            validators = new Validators(getLog()).initialize(manifestConfig.getValidators());
        } catch (Exception e) {
            throw new MojoExecutionException("Error while initialize validators", e);
        }

        sleep();

        final ManifestResources resources = new ManifestResources(getLog(), manifestFile.getParent());
        try {
            resources.scan(manifestConfig.getResources());
        } catch (Exception e) {
            throw new MojoExecutionException("Error while scan resources", e);
        }

        sleep();

        try {
            if (!resources.validateResourcesOfBootstrap(validators)) {
                throw new MojoExecutionException("Has errors in resources of bootstrap");
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error while validate resources of bootstrap", e);
        }

        sleep();

        try {
            if (!resources.validateResourcesOfModules(validators)) {
                throw new MojoExecutionException("Has errors in resources of modules");
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error while validate resources of modules", e);
        }

        sleep();

        try {
            if (!resources.validateResourcesOfLibraries(validators)) {
                throw new MojoExecutionException("Has invalid resources of libraries");
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error while validate resources of libraries", e);
        }

        sleep();

        try {
            resources.compressResourcesOfBootstrap(compressors);
        } catch (Exception e) {
            throw new MojoExecutionException("Error while compressing resources of bootstrap", e);
        }

        sleep();

        try {
            resources.compressResourcesOfModules(compressors);
        } catch (Exception e) {
            throw new MojoExecutionException("Error while compressing resources of modules", e);
        }

        sleep();

        try {
            resources.compressResourcesOfLibraries(compressors);
        } catch (Exception e) {
            throw new MojoExecutionException("Error while compressing resources of libraries", e);
        }

        sleep();

        final Manifest manifest = new Manifest(getLog(), manifestFile.getParent(), resources);
        try {
            manifest.create(manifestConfig);
        } catch (Exception e) {
            throw new MojoExecutionException("Error while generate manifest", e);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException ignore) {
        }
    }
}
