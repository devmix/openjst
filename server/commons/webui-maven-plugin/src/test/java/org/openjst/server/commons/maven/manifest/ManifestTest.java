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

import org.openjst.server.commons.maven.AbstractMojoTest;
import org.openjst.server.commons.maven.utils.ManifestUtils;
import org.testng.annotations.Test;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class ManifestTest extends AbstractMojoTest {

    public static final String PROJECT_NAMESPACE = "org.test";

    @Test(groups = "unit")
    public void testMakeModule() {
        assertThat(ManifestUtils.makeModuleName("first-second"))
                .isEqualTo("FirstSecond");

        assertThat(ManifestUtils.makeModuleName("pages/main.js"))
                .isEqualTo("pages.Main");

        assertThat(ManifestUtils.makeModuleName("first-second/first-second-third"))
                .isEqualTo("firstSecond.FirstSecondThird");

        assertThat(ManifestUtils.makeModuleName("first-second-third/first-second-third/first-second-third"))
                .isEqualTo("firstSecondThird.firstSecondThird.FirstSecondThird");
    }

    @SuppressWarnings("unchecked")
    @Test(groups = "unit")
    public void testOJST() {
        final Parameters ojst = new Parameters(PROJECT_NAMESPACE);
        ojst.addModuleName("pages.SingIn");
        ojst.addModuleName("pages.Main");
        ojst.addModuleName("Manifest");
        ojst.addModuleName("widgets.NavBar");
        ojst.addModuleName("Application");
        ojst.addModuleName("rest.AbstractRestService");
        ojst.addModuleName("views.Settings");
        ojst.addModuleName("views.Login");
        ojst.addModuleName("views.group.View1");
        ojst.addModuleName("views.group.View2");

        final Map<String, Object> modules = ojst.getModules();
        assertThat(modules).isNotNull();
        assertThat(modules).hasSize(6);
        assertThat(modules.get("Manifest")).isEqualTo(PROJECT_NAMESPACE + ".Manifest");
        assertThat(modules.get("Application")).isEqualTo(PROJECT_NAMESPACE + ".Application");

        final Map<String, Object> pages = (Map<String, Object>) modules.get("pages");
        assertThat(pages).isNotNull();
        assertThat(pages).hasSize(2);
        assertThat(pages.get("SingIn")).isEqualTo(PROJECT_NAMESPACE + ".pages.SingIn");
        assertThat(pages.get("Main")).isEqualTo(PROJECT_NAMESPACE + ".pages.Main");

        final Map<String, Object> widgets = (Map<String, Object>) modules.get("widgets");
        assertThat(widgets).isNotNull();
        assertThat(widgets).hasSize(1);
        assertThat(widgets.get("NavBar")).isEqualTo(PROJECT_NAMESPACE + ".widgets.NavBar");

        final Map<String, Object> rest = (Map<String, Object>) modules.get("rest");
        assertThat(rest).isNotNull();
        assertThat(rest).hasSize(1);
        assertThat(rest.get("AbstractRestService")).isEqualTo(PROJECT_NAMESPACE + ".rest.AbstractRestService");

        final Map<String, Object> views = (Map<String, Object>) modules.get("views");
        assertThat(views).isNotNull();
        assertThat(views).hasSize(3);
        assertThat(views.get("Settings")).isEqualTo(PROJECT_NAMESPACE + ".views.Settings");
        assertThat(views.get("Login")).isEqualTo(PROJECT_NAMESPACE + ".views.Login");
        final Map<String, Object> group = (Map<String, Object>) views.get("group");
        assertThat(group).isNotNull();
        assertThat(group).hasSize(2);
        assertThat(group.get("View1")).isEqualTo(PROJECT_NAMESPACE + ".views.group.View1");
        assertThat(group.get("View2")).isEqualTo(PROJECT_NAMESPACE + ".views.group.View2");
    }
}
