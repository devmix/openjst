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

import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class YUITest {

    private static final String DEPENDENCIES = "}, OJST.VERSION, {requires: [\n" +
            "    'yui-module-1', org.test.modules.Class2,\n" +
            "    'yui-module-2', org.test.modules.module.Class, 'yui-module,3'\n" +
            "]});";

    @Test(groups = "unit")
    public void testDependenciesParser() {
        final Set<YUIDependency> dependencies = YUI.findDependencies(DEPENDENCIES);
        final Iterator<YUIDependency> it = dependencies.iterator();

        assertThat(dependencies).hasSize(5);
        assertDependency(it.next(), "'yui-module-1'", false);
        assertDependency(it.next(), "org.test.modules.Class2", true);
        assertDependency(it.next(), "'yui-module-2'", false);
        assertDependency(it.next(), "org.test.modules.module.Class", true);
        assertDependency(it.next(), "'yui-module,3'", false);
    }

    private void assertDependency(final YUIDependency next, final String name, final boolean isObject) {
        assertThat(next).isNotNull();
        assertThat(next.getName()).isEqualTo(name);
        assertThat(next.isObject()).isEqualTo(isObject);
    }
}
