/*
 * Copyright (C) 2013-2014 OpenJST Project
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

package org.openjst.commons.properties.wrappers;

import org.openjst.commons.properties.Property;
import org.openjst.commons.properties.restrictions.Number;
import org.openjst.commons.properties.restrictions.validators.ValidatorsTest;
import org.openjst.commons.properties.storages.StorageTest;
import org.openjst.commons.properties.storages.annotations.Levels;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * @author Sergey Grachev
 */
public final class WrapperTest {

    // STORAGE

    @Test(groups = "unit")
    public void testStorageInheritance() {
        StorageTest.testInheritance(
                new TestWrapper("p1", Property.Type.INT, null, new Levels.Instance(0, 0xFF)));
    }

    @Test(groups = "unit")
    public void testStorageBatchGet() {
        StorageTest.testBatchGet(
                new TestWrapper("LEVEL_1_1", Property.Type.INT, null, new Levels.Instance(1, 0xFF)),
                new TestWrapper("LEVEL_1_2", Property.Type.INT, null, new Levels.Instance(1, 0xFF)),
                new TestWrapper("LEVEL_1_3", Property.Type.INT, null, new Levels.Instance(1, 0xFF)));
    }

    @Test(groups = "unit")
    public void testStorageBatchPut() {
        StorageTest.testBatchPut(
                new TestWrapper("LEVEL_1_1", Property.Type.INT, null, new Levels.Instance(1, 0xFF)),
                new TestWrapper("LEVEL_1_2", Property.Type.INT, null, new Levels.Instance(1, 0xFF)),
                new TestWrapper("LEVEL_1_3", Property.Type.INT, null, new Levels.Instance(1, 0xFF)));
    }

    @Test(groups = "unit")
    public void testStorageSubSet() {
        StorageTest.testSubSet(
                new TestWrapper("LEVEL_1_1", Property.Type.INT, null, new Levels.Instance(1, 0xFF)),
                new TestWrapper("LEVEL_1_2", Property.Type.INT, null, new Levels.Instance(1, 0xFF)),
                new TestWrapper("LEVEL_1_3", Property.Type.INT, null, new Levels.Instance(1, 0xFF)));
    }

    @Test(groups = "unit")
    public void testStorageDefaultValues() {
        StorageTest.testDefaultValues(
                new TestWrapper("LEVEL_1_1", Property.Type.INT, null, new Levels.Instance(1, 0xFF)),
                new TestWrapper("LEVEL_1_2", Property.Type.INT, "0", new Levels.Instance(1, 0xFF)));
    }

    // VALIDATORS

    @Test(groups = "unit")
    public void testValidatorsTypesConversion() {
        ValidatorsTest.testTypesConversion(new TestWrapper("NUMBER_MIN_MAX", Property.Type.INT, "1", null,
                new Annotation[]{new Number.Min.Instance(1), new Number.Max.Instance(10)}));
    }

    private static class TestWrapper extends WrapperAdapter {

        private final String nullAs;
        private final Type type;
        private final String key;
        private final Levels levels;
        private final Annotation[] restrictions;

        private TestWrapper(final String key, final Type type, @Nullable final String nullAs,
                            final Levels levels, final Annotation[] restrictions) {
            this.nullAs = nullAs;
            this.type = type;
            this.key = key;
            this.levels = levels;
            this.restrictions = restrictions;
        }

        private TestWrapper(final String key, final Type type, @Nullable final String nullAs,
                            final Levels levels) {
            this.nullAs = nullAs;
            this.type = type;
            this.key = key;
            this.levels = levels;
            this.restrictions = null;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public Type type() {
            return type;
        }

        @Nullable
        @Override
        public String nullAs() {
            return nullAs;
        }

        @Nullable
        @Override
        public Levels levels() {
            return levels;
        }

        @Nullable
        @Override
        public Annotation[] restrictions() {
            return restrictions;
        }
    }
}
