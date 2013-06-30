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

package org.openjst.server.commons.maven.utils;

import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class PatternsTest {

    @Test(groups = "unit")
    public void testFile() {
        assertThat(Patterns.CSS_MODULE_FILE_NAME.matcher("file1.js").matches()).isTrue();
        assertThat(Patterns.CSS_MODULE_FILE_NAME.matcher("/folder/file1.js").matches()).isTrue();
        assertThat(Patterns.CSS_MODULE_FILE_NAME.matcher("folder/file1.js").matches()).isTrue();
        assertThat(Patterns.CSS_MODULE_FILE_NAME.matcher("fol_der/file1.js").matches()).isFalse();
        assertThat(Patterns.CSS_MODULE_FILE_NAME.matcher("fol-der/file1.js").matches()).isTrue();
        assertThat(Patterns.CSS_MODULE_FILE_NAME.matcher("fol-der/fi-le1.js").matches()).isTrue();
        assertThat(Patterns.CSS_MODULE_FILE_NAME.matcher("fol-der/fi-le-1.js").matches()).isTrue();
        assertThat(Patterns.CSS_MODULE_FILE_NAME.matcher("fol-der/fi-le-1-.js").matches()).isFalse();
        assertThat(Patterns.CSS_MODULE_FILE_NAME.matcher("fol-der/-fi-le-1.js").matches()).isFalse();
        assertThat(Patterns.CSS_MODULE_FILE_NAME.matcher("-fol-der/fi-le-1.js").matches()).isFalse();
        assertThat(Patterns.CSS_MODULE_FILE_NAME.matcher("/folder/folder2/file1.js").matches()).isTrue();
    }
}
