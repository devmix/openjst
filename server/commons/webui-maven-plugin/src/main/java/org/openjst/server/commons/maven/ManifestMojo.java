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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.DirectoryScanner;
import org.openjst.server.commons.maven.manifest.OpenJST;
import org.openjst.server.commons.maven.manifest.YUI;
import org.openjst.server.commons.maven.manifest.yui.YUIConfig;
import org.openjst.server.commons.maven.manifest.yui.YUIConfigGroup;
import org.openjst.server.commons.maven.manifest.yui.YUIConfigGroupModule;
import org.openjst.server.commons.maven.utils.Patterns;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openjst.server.commons.maven.utils.Constants.*;

/**
 * Goal which touches a timestamp file.
 *
 * @goal manifest
 * @phase process-resources
 * @threadSafe
 */
@SuppressWarnings("JavaDoc")
public final class ManifestMojo extends AbstractMojo {

    /**
     * Project namespace
     *
     * @parameter expression="${project.groupId}"
     * @required
     */
    private String projectNamespace;

    /**
     * Project version
     *
     * @parameter expression="${project.version}"
     * @required
     */
    private String projectVersion;

    /**
     * Project name
     *
     * @parameter expression="${project.name}"
     * @required
     */
    private String projectName;

    /**
     * Project manifest object
     *
     * @parameter
     * @required
     */
    private String projectManifestObject;

    /**
     * Base URL for a dynamic combo handler. This will be used to make combo-handled module requests if combine is set to `true.
     *
     * @parameter default-value="rest/ui/assets/ui-combo?"
     * @required
     */
    private String comboServiceUrl;

    /**
     * See {@link #comboServiceUrl}
     *
     * @parameter default-value="rest/ui/assets/yui-combo?"
     * @required
     */
    private String frameworkComboServiceUrl;

    /**
     * Directory with JS
     *
     * @parameter expression="${basedir}/src/main/webapp/ui/js"
     * @required
     */
    private File resourcesPathJs;

    /**
     * Directory with CSS
     *
     * @parameter expression="${basedir}/src/main/webapp/ui/css"
     * @required
     */
    private File resourcesPathCss;

    /**
     * Manifest file name
     *
     * @parameter expression="${basedir}/src/main/webapp/ui/js/manifest.js"
     * @required
     */
    private File manifestFile;

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
     * If true, Y.log() messages will be written to the browser's debug console when available.
     *
     * @parameter default-value="false"
     * @required
     */
    private boolean debug;

    /**
     * If true, YUI will use a combo handler to load multiple modules in as few requests as possible.
     *
     * @parameter default-value="true"
     * @required
     */
    private boolean combine;

    /**
     * Filter to apply to module urls. This filter will modify the default path for all modules.
     * <p/>
     * The default path for the YUI library is the minified version of the files (e.g., event-min.js).
     * The filter property can be a predefined filter or a custom filter. The valid predefined filters are:
     * <ul>
     * <li>debug: Loads debug versions of modules (e.g., event-debug.js).</li>
     * <li>raw: Loads raw, non-minified versions of modules without debug logging (e.g., event.js).</li>
     * </ul>
     *
     * @parameter default-value="raw"
     */
    private String filter;

    /**
     * See {@link #filter}
     *
     * @parameter default-value="raw"
     */
    private String frameworkFilter;

    @SuppressWarnings("UnusedDeclaration")
    public ManifestMojo() {
    }

    public ManifestMojo(final File resourcesPathJs, final File resourcesPathCss, final File manifestFile,
                        final String projectNamespace, final String projectVersion, final String projectName,
                        final String projectManifestObject,
                        final String comboServiceUrl, final String frameworkComboServiceUrl,
                        final String[] excludes, final String[] includes,
                        final boolean debug, final boolean combine, final String filter, final String frameworkFilter) {
        this.resourcesPathJs = resourcesPathJs;
        this.resourcesPathCss = resourcesPathCss;
        this.projectName = projectName;
        this.projectNamespace = projectNamespace;
        this.projectVersion = projectVersion;
        this.projectManifestObject = projectManifestObject;
        this.comboServiceUrl = comboServiceUrl;
        this.frameworkComboServiceUrl = frameworkComboServiceUrl;
        this.manifestFile = manifestFile;
        this.excludes = excludes;
        this.includes = includes;
        this.debug = debug;
        this.combine = combine;
        this.filter = filter;
        this.frameworkFilter = frameworkFilter;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final OpenJST ojst = new OpenJST();
        ojst.setNamespace(projectNamespace);
        ojst.setVersion(projectVersion);
        ojst.setProjectName(projectName);

        final YUI yui = new YUI();

        final YUIConfig yuiConfig = new YUIConfig(frameworkComboServiceUrl, debug, combine, frameworkFilter);
        yui.setConfig(yuiConfig);

        final YUIConfigGroup group = yuiConfig.addGroup("openjst", comboServiceUrl, combine, filter);

        scanJsFiles(ojst, group);
        getLog().info("");
        scanCssFiles(ojst, group);

        // generate manifest

        String manifest;
        try {
            manifest = IOUtils.toString(this.getClass().getResourceAsStream("/manifest-bootstrap.js"));
            final ObjectMapper mapper = new ObjectMapper();
            final ObjectWriter writer = debug ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer();

            manifest = manifest
                    .replace("_PROJECT_MANIFEST_OBJECT_", projectManifestObject)
                    .replace("_PROJECT_MANIFEST_META_", writer.writeValueAsString(ojst))
                    .replace("_PROJECT_MANIFEST_YUI_", writer.writeValueAsString(yui));

            if (debug) {
                getLog().info(manifest);
            }
        } catch (Exception e) {
            throw new MojoExecutionException(ERROR_CANT_BUILD_MANIFEST, e);
        }

        // write manifest

        try {
            final OutputStream os = new FileOutputStream(manifestFile);
            try {
                IOUtils.write(manifest, os);
            } catch (IOException e) {
                throw new MojoExecutionException(ERROR_CANT_BUILD_MANIFEST, e);
            } finally {
                IOUtils.closeQuietly(os);
            }
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(ERROR_CANT_BUILD_MANIFEST, e);
        }
    }

    private Map<String, Resource> scanFiles(final String typeName, final File resourcesPath, final Pattern typeValidator, final Pattern moduleValidator,
                                            final String type, final String minExt)
            throws MojoExecutionException {

        getLog().info(String.format(MSG_DO_SEARCH_MODULES, typeName, resourcesPath));
        getLog().info(LINE_SEPARATOR);

        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(resourcesPath);
        scanner.addDefaultExcludes();
        scanner.setExcludes(excludes);
        scanner.setIncludes(includes);
        scanner.scan();

        final Map<String, Resource> result = new TreeMap<String, Resource>();
        for (final String name : scanner.getIncludedFiles()) {
            final boolean isSupported = typeValidator.matcher(name).matches();
            if (!isSupported) {
                getLog().info(String.format(MSG_SKIP_UNSUPPORTED_RESOURCE, name));
                continue;
            }

            if (!moduleValidator.matcher(name).matches()) {
                getLog().error(String.format(ERROR_INCORRECT_MODULE_NAME, name));
                throw new MojoExecutionException(ERROR_CANT_BUILD_MANIFEST);
            }

            final String moduleName = makeModuleName(name);
            final String resourceName = new File(type, name.replaceAll(Patterns.STRING_EXT_REPLACE, minExt)).toString();

            result.put(moduleName, new Resource(name, resourceName));
        }

        return result;
    }

    private void scanJsFiles(final OpenJST ojst, final YUIConfigGroup group) throws MojoExecutionException {
        final Map<String, Resource> files = scanFiles(JAVA_SCRIPT, resourcesPathJs,
                Patterns.JS, Patterns.JS_MODULE_FILE_NAME,
                YUI.TYPE_JS, MIN_JS);

        for (final Map.Entry<String, Resource> entry : files.entrySet()) {
            final String moduleName = entry.getKey();
            final String resourceName = entry.getValue().urlName;
            final String resourceRealName = entry.getValue().realName;

            getLog().info(String.format(MSG_MODULE, moduleName, resourceName));

            group.addJsModule(projectNamespace + '.' + moduleName, resourceName)
                    .addRequires(extractJsDependencies(resourceRealName));

            ojst.addModuleName(moduleName);
        }
    }

    private void scanCssFiles(final OpenJST ojst, final YUIConfigGroup group) throws MojoExecutionException {
        final Map<String, Resource> files = scanFiles(CASCADING_STYLE_SHEETS, resourcesPathCss,
                Patterns.CSS, Patterns.CSS_MODULE_FILE_NAME,
                YUI.TYPE_CSS, MIN_CSS);

        for (final Map.Entry<String, Resource> entry : files.entrySet()) {
            final String moduleName = entry.getKey();
            final String resourceName = entry.getValue().urlName;

            getLog().info(String.format("\t%s (%s)", moduleName, resourceName));

            group.addCssModule(projectNamespace, projectManifestObject, moduleName, resourceName);

            ojst.addModuleName(moduleName + "Css");
        }
    }

    private Set<YUIConfigGroupModule.Dependency> extractJsDependencies(final String resourceName) throws MojoExecutionException {
        final File file = new File(resourcesPathJs, resourceName);
        final String content;
        try {
            content = FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new MojoExecutionException(String.format(ERROR_CANT_GET_DEPENDENCIES_FROM, file));
        }

        if (!StringUtils.isBlank(content)) {
            final Matcher matcher = Patterns.MODULE_REQUIRES.matcher(content);
            if (matcher.matches()) {
                final Set<YUIConfigGroupModule.Dependency> dependencies = new LinkedHashSet<YUIConfigGroupModule.Dependency>();
                final String dependenciesJs = matcher.group(1).replaceAll("\\s", "").trim();
                final Scanner s = new Scanner(dependenciesJs);

                getLog().info(String.format(MSG_DEPENDENCIES, dependenciesJs));

                while (s.hasNext()) {
                    final StringBuilder token = new StringBuilder(s.next().trim());
                    if (token.length() == 0) {
                        continue;
                    }

                    if (token.charAt(token.length() - 1) == ',') {
                        token.deleteCharAt(token.length() - 1);
                    }

                    final char firstChar = token.charAt(0);
                    final boolean isObject = firstChar != '\'' && firstChar != '"';
                    if (!isObject) {
                        token.deleteCharAt(0);
                        if (token.length() > 0) {
                            token.deleteCharAt(token.length() - 1);
                        }
                    }

                    dependencies.add(new YUIConfigGroupModule.Dependency(token.toString(), isObject));
                }
                return dependencies;
            }
        }

        return Collections.emptySet();
    }

    public static String makeModuleName(final String name) {
        if (name.isEmpty()) {
            return name;
        }

        final StringBuilder sb = new StringBuilder(name.replaceAll(Patterns.STRING_EXT_REPLACE, "").replaceAll("/", "."));
        int i;
        while ((i = sb.lastIndexOf("-")) > -1) {
            sb.replace(i, i + 2, "" + Character.toUpperCase(sb.charAt(i + 1)));
        }

        final int lastDotIndex = sb.lastIndexOf(".") + 1;
        sb.replace(lastDotIndex, lastDotIndex + 1, "" + Character.toUpperCase(sb.charAt(lastDotIndex)));
        return sb.toString();
    }

    private static final class Resource {
        private final String realName;
        private final String urlName;

        public Resource(final String realName, final String urlName) {
            this.realName = realName;
            this.urlName = urlName;
        }
    }
}
