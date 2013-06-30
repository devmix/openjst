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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.mozilla.javascript.ContextFactory;
import org.openjst.server.commons.maven.build.Compressor;
import org.openjst.server.commons.maven.build.Validator;
import org.openjst.server.commons.maven.build.css.compress.CssCompressorType;
import org.openjst.server.commons.maven.build.js.compress.JsCompressorType;
import org.openjst.server.commons.maven.build.js.validate.JsValidatorType;
import org.openjst.server.commons.maven.utils.OptionGroup;
import org.openjst.server.commons.maven.utils.Patterns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.openjst.server.commons.maven.utils.Constants.*;

/**
 * Goal which touches a timestamp file.
 *
 * @goal build
 * @phase process-resources
 * @threadSafe
 */
@SuppressWarnings("JavaDoc")
public final class BuildMojo extends AbstractMojo {

    public static final int PERCENTS_MAX = 100;

    /**
     * @parameter expression="${encoding}" default-value="UTF-8"
     */
    private String encoding;

    /**
     * Directory of input files
     *
     * @parameter expression="${basedir}/src/main/webapp"
     * @required
     */
    private File sourcePath;

    /**
     * The directory where the webapp is built.
     *
     * @parameter expression="${project.build.directory}/${project.build.finalName}"
     * @required
     */
    private File targetPath;

    /**
     * List of additional excludes
     *
     * @parameter
     */
    private String[] excludes;

    /**
     * List of additional includes
     *
     * @parameter
     */
    private String[] includes;

    /**
     * Whether to skip execution.
     *
     * @parameter default-value="false"
     */
    private boolean skipCompress;

    /**
     * Whether to skip execution.
     *
     * @parameter default-value="false"
     */
    private boolean skipValidate;

    /**
     * @parameter default-value="true"
     */
    private boolean allowNotValid;

    /**
     * Whether to skip execution.
     *
     * @parameter
     */
    private Map<String, String> configJsCompressor;

    /**
     * Whether to skip execution.
     *
     * @parameter
     */
    private Map<String, String> configCssCompressor;

    private final JsCompressorType jsCompressorType = JsCompressorType.UGLIFYJS;
    private final CssCompressorType cssCompressorType = CssCompressorType.YUI;
    private final JsValidatorType jsValidatorType = JsValidatorType.JSLINT;

    @SuppressWarnings("UnusedDeclaration")
    public BuildMojo() {
    }

    public BuildMojo(final String encoding, final File sourcePath, final File targetPath, final boolean allowNotValid,
                     final String[] excludes, final String[] includes, final boolean skipCompress, final boolean skipValidate,
                     final Map<String, String> configJsCompressor, final Map<String, String> configCssCompressor) {
        this.encoding = encoding;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.allowNotValid = allowNotValid;
        this.excludes = excludes;
        this.includes = includes;
        this.skipCompress = skipCompress;
        this.skipValidate = skipValidate;
        this.configJsCompressor = configJsCompressor;
        this.configCssCompressor = configCssCompressor;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(sourcePath);
        scanner.addDefaultExcludes();
        scanner.setExcludes(excludes);
        scanner.setIncludes(includes);
        scanner.scan();

        final ContextFactory ctx = ContextFactory.getGlobal();
        final Set<String> jsList = new TreeSet<String>();
        final Set<String> cssList = new TreeSet<String>();

        scan(scanner, jsList, cssList);

        if (!skipValidate) {
            validate(scanner.getBasedir(), jsList);
        }

        getLog().info("");

        if (!skipCompress) {
            compress(JAVA_SCRIPT, MIN_JS, scanner.getBasedir(), jsList, createJsCompressor(ctx));

            getLog().info("");

            compress(CASCADING_STYLE_SHEETS, MIN_CSS, scanner.getBasedir(), cssList, createCssCompressor(ctx));
        } else {
            // YUI doesn't use a mask for css files, so you need to create a min version of the css files in any way
            rename(CASCADING_STYLE_SHEETS, MIN_CSS, scanner.getBasedir(), cssList);
        }
    }

    private void rename(final String type, final String extension, final File base, final Set<String> files) throws MojoExecutionException {
        getLog().info(String.format(MSG_DO_RENAME, type, base));
        getLog().info(LINE_SEPARATOR);

        for (final String name : files) {
            final String outputName = Patterns.EXT_REPLACE.matcher(name).replaceAll(extension);
            final File inFile = new File(base, name);
            final File outFile = new File(targetPath, outputName);
            if (outFile.exists() && !outFile.delete()) {
                throw new MojoExecutionException(String.format(ERROR_CANT_DELETE_OLD_FILE, outFile));
            }
            if (!inFile.renameTo(outFile)) {
                throw new MojoExecutionException(String.format(ERROR_CANT_RENAME_FILE_TO, inFile, outFile));
            }

            getLog().info(String.format("\t %s -> %s", name, outputName));
        }
    }

    private void compress(final String type, final String extension, final File base, final Set<String> files,
                          final Compressor compressor) throws MojoExecutionException {

        getLog().info(String.format(MSG_DO_COMPRESS, type, base));
        getLog().info(LINE_SEPARATOR);

        for (final String name : files) {
            final File inFile = new File(base, name);

            final String original = readFile(inFile);
            final String compressed;
            try {
                compressed = compressor.compress(original);
            } catch (IOException e) {
                throw new MojoExecutionException(String.format(ERROR_CANT_COMPRESS_FILE, inFile));
            }

            final String outputName = Patterns.EXT_REPLACE.matcher(name).replaceAll(extension);
            final File outFile = new File(targetPath, outputName);
            final File outDirectory = new File(outFile.getParent());
            if (!outDirectory.exists() && !outDirectory.mkdirs()) {
                throw new MojoExecutionException(String.format(ERROR_CANT_CREATE_DIRECTORY, outDirectory));
            }

            OutputStreamWriter out = null;
            try {
                out = new OutputStreamWriter(new FileOutputStream(outFile, false), encoding);
                out.write(compressed);
            } catch (Exception e) {
                throw new MojoExecutionException(String.format(ERROR_CANT_WRITE_FILE, outFile));
            } finally {
                IOUtils.closeQuietly(out);
            }

            printResult(name, outputName, original.length(), compressed.length());
        }
    }

    private void validate(final File base, final Set<String> files) throws MojoExecutionException {
        getLog().info(String.format(MSG_DO_VALIDATE, base));
        getLog().info(LINE_SEPARATOR);

        boolean hasErrors = false;
        final Validator jsValidator = createJsValidator();
        for (final String name : files) {
            final File inFile = new File(base, name);

            final String original = readFile(inFile);

            final List<String> issues = jsValidator.validate(original);
            if (!issues.isEmpty()) {
                printIssues(name, issues);
                hasErrors = true;
            }
        }

        if (hasErrors && !allowNotValid) {
            throw new MojoExecutionException(ERROR_STOPPING_BECAUSE_THERE_IS_AN_ERROR_AND_CAN_T_BE_IGNORED);
        }
    }

    private String readFile(final File inFile) throws MojoExecutionException {
        final String original;
        try {
            original = FileUtils.readFileToString(inFile, "UTF-8");
        } catch (IOException e) {
            throw new MojoExecutionException(String.format(ERROR_CANT_READ_FILE, inFile.toString()));
        }
        return original;
    }

    private Compressor createCssCompressor(final ContextFactory factory) throws MojoExecutionException {
        try {
            return skipCompress ? null : cssCompressorType.newInstance(factory, OptionGroup.parse(configCssCompressor));
        } catch (IOException e) {
            throw new MojoExecutionException(ERROR_CANT_CREATE_CSS_COMPRESSOR, e);
        }
    }

    private void printResult(final String name, final String outputName, final int original, final int compressed) {
        final String stat = String.format("  %6.2f%%", original > 0 ? (double) PERCENTS_MAX / original * compressed : PERCENTS_MAX);
        getLog().info(stat + " " + name + " > " + outputName);
    }

    private void printIssues(final String name, final List<String> issues) {
        getLog().error(name + ":");
        for (final String issue : issues) {
            getLog().error('\t' + issue);
        }
    }

    private Validator createJsValidator() throws MojoExecutionException {
        try {
            return skipValidate ? null : jsValidatorType.newInstance();
        } catch (IOException e) {
            throw new MojoExecutionException(ERROR_CANT_CREATE_VALIDATOR, e);
        }
    }

    private Compressor createJsCompressor(final ContextFactory factory) throws MojoExecutionException {
        try {
            return skipCompress ? null : jsCompressorType.newInstance(factory, OptionGroup.parse(configJsCompressor));
        } catch (IOException e) {
            throw new MojoExecutionException(ERROR_CANT_CREATE_COMPRESSOR, e);
        }
    }

    private static void scan(final DirectoryScanner scanner, final Set<String> jsList, final Set<String> cssList) {
        for (final String name : scanner.getIncludedFiles()) {
            final File inFile = new File(scanner.getBasedir(), name);
            if (Patterns.JS.matcher(inFile.getName()).matches()) {
                jsList.add(name);
            } else if (Patterns.CSS.matcher(inFile.getName()).matches()) {
                cssList.add(name);
            }
        }
    }
}
