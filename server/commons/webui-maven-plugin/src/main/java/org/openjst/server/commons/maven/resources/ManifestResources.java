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

package org.openjst.server.commons.maven.resources;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.openjst.server.commons.maven.compression.Compressor;
import org.openjst.server.commons.maven.compression.Compressors;
import org.openjst.server.commons.maven.manifest.jaxb.ResourcesType;
import org.openjst.server.commons.maven.validation.Validator;
import org.openjst.server.commons.maven.validation.Validators;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public final class ManifestResources {

    private final File baseDir;
    private final BootstrapResources bootstrapResources;
    private final ModuleResources moduleResources;
    private final Libraries libraries;
    private final Log log;

    public ManifestResources(final Log log, final String baseDir) {
        this.log = log;
        this.baseDir = new File(baseDir);
        this.bootstrapResources = new BootstrapResources(this.baseDir);
        this.libraries = new Libraries(this.baseDir);
        this.moduleResources = new ModuleResources(this.baseDir);
    }

    public void scan(final ResourcesType resources) throws IOException {
        if (resources.getCore() != null) {
            bootstrapResources.scan(resources.getCore());
        }
        if (resources.getModules() != null) {
            moduleResources.scan(resources.getModules());
        }
        if (resources.getLibraries() != null) {
            libraries.scan(resources.getLibraries());
        }
    }

    public Libraries getLibraries() {
        return libraries;
    }

    public ModuleResources getModuleResources() {
        return moduleResources;
    }

    public boolean validateResourcesOfBootstrap(final Validators validators) throws IOException {
        if (bootstrapResources.isEmpty()) {
            return true;
        }

        log.info("");
        log.info("Validate resources of bootstrap...");

        final Map<String, List<String>> issues = new LinkedHashMap<String, List<String>>();

        final boolean success
                = validate(validators, validators.forJavaScript(), bootstrapResources.getResources(), issues);

        printValidationResult(issues);

        return success;
    }

    public boolean validateResourcesOfModules(final Validators validators) throws IOException {
        if (moduleResources.isEmpty()) {
            return true;
        }

        log.info("");
        log.info("Validate resources of modules...");

        final Map<String, List<String>> issues = new LinkedHashMap<String, List<String>>();

        final boolean success = validate(validators, validators.forJavaScript(), moduleResources.getResources(), issues)
                || validate(validators, validators.forCss(), moduleResources.getStyleResources(), issues);

        printValidationResult(issues);

        return success;
    }

    public boolean validateResourcesOfLibraries(final Validators validators) throws IOException {
        if (libraries.isEmpty()) {
            return true;
        }

        log.info("");
        log.info("Validate resources of libraries...");

        boolean success = true;
        for (final LibraryResources libraryResources : libraries.getResources()) {
            if (libraryResources.isSkipValidate()) {
                continue;
            }

            log.info(String.format("\t%s...", libraryResources.getName()));

            final Map<String, List<String>> issues = new LinkedHashMap<String, List<String>>();

            success &= validate(validators, validators.forJavaScript(), libraryResources.getResources(), issues)
                    || validate(validators, validators.forCss(), libraryResources.getStyleResources(), issues);

            printValidationResult(issues);
        }

        return success;
    }

    public void compressResourcesOfBootstrap(final Compressors compressors) throws IOException {
        if (bootstrapResources.isEmpty()) {
            return;
        }

        log.info("");
        log.info("Compress resources of bootstrap...");

        compress(bootstrapResources.getResources(), bootstrapResources.getJoinAs(), compressors.forJavaScript());
    }

    public void compressResourcesOfModules(final Compressors compressors) throws IOException {
        if (moduleResources.isEmpty()) {
            return;
        }

        log.info("");
        log.info("Compress JavaScript resources of modules...");

        compress(moduleResources.getResources(), null, compressors.forJavaScript());

        log.info("");
        log.info("Compress CSS resources of modules...");

        compress(moduleResources.getStyleResources(), null, compressors.forCss());
    }

    public void compressResourcesOfLibraries(final Compressors compressors) throws IOException {
        if (libraries.isEmpty()) {
            return;
        }

        log.info("");
        log.info("Compress resources of libraries...");

        for (final LibraryResources library : libraries.getResources()) {
            log.info(String.format("\t%s...", library.getName()));
            compress(library.getResources(), library.getJoinSourcesAs(), compressors.forJavaScript());
            compress(library.getStyleResources(), library.getJoinStylesAs(), compressors.forCss());
        }
    }

    private void compress(final Set<? extends Resource> resources, @Nullable final Resource joinAs,
                          final List<Compressor> compressors) throws IOException {

        final boolean joinToSingleFile = joinAs != null;
        for (final Resource resource : resources) {
            if (joinToSingleFile) {
                final String content = resource.getContent();
                FileUtils.fileAppend(joinAs.getFile().getAbsolutePath(), content + "\n");
            }

            String content;
            if (resource.isCompressed()) {
                content = resource.getCompressedContent();
            } else {
                content = resource.getContent();
                for (final Compressor compressor : compressors) {
                    content = compressor.compress(content);
                }
            }

            if (joinToSingleFile) {
                FileUtils.fileAppend(joinAs.getCompressedFile().getAbsolutePath(), content + "\n");
            } else {
                resource.writeCompressed(content);
                printCompressResult(resource, resource.getCompressedFile().length(), resource.getFile().length());
            }
        }

        if (joinToSingleFile) {
            printCompressResult(joinAs, joinAs.getCompressedFile().length(), joinAs.getFile().length());
        }
    }

    private boolean validate(final Validators validators, final List<Validator> useValidators,
                             final Set<? extends Resource> resources, final Map<String, List<String>> errors) throws IOException {
        boolean success = true;

        for (final Validator validator : useValidators) {
            for (final Resource resource : resources) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("  Validate resource %s", resource.getFile()));
                }
                final List<String> result = validator.validate(resource.getContent());
                if (!result.isEmpty()) {
                    success &= validators.isAllowErrors(validator);
                    errors.put(resource.getRelativePath(), result);
                }
            }
        }

        return success;
    }

    private void printValidationResult(final Map<String, List<String>> issues) {
        for (final Map.Entry<String, List<String>> entry : issues.entrySet()) {
            log.error(String.format("  %s:", entry.getKey()));
            for (final String issue : entry.getValue()) {
                log.error("    " + issue);
            }
        }
    }

    private void printCompressResult(final Resource resource, final long compressedSize, final long originalSize) {
        final double percent = originalSize > 0 ? (double) 100 / originalSize * compressedSize : 100;
        log.info(String.format("  %6.2f%% %s > %s",
                percent, resource.getRelativePath(), resource.getCompressedRelativePath()));
    }

    @Override
    public String toString() {
        return "ResourcesScanner{" +
                "baseDir=" + baseDir +
                ", bootstrapResources=" + bootstrapResources +
                ", libraries=" + libraries +
                ", moduleResources=" + moduleResources +
                '}';
    }
}
