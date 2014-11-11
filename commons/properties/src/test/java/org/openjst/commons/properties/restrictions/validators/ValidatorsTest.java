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

package org.openjst.commons.properties.restrictions.validators;

import org.openjst.commons.properties.Caches;
import org.openjst.commons.properties.Property;
import org.openjst.commons.properties.annotations.Value;
import org.openjst.commons.properties.exceptions.PropertyValidationException;
import org.openjst.commons.properties.restrictions.List;
import org.openjst.commons.properties.restrictions.Number;
import org.openjst.commons.properties.restrictions.Pattern;
import org.openjst.commons.properties.restrictions.Validator;
import org.openjst.commons.properties.storages.Storage;
import org.openjst.commons.properties.storages.StorageBuilder;
import org.openjst.commons.properties.values.ValuesBuilder;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static org.openjst.commons.properties.restrictions.validators.ValidatorsTest.TestProperties.*;

/**
 * @author Sergey Grachev
 */
public final class ValidatorsTest {

    @Test(groups = "unit")
    public void testTypesConversion() {
        testTypesConversion(NUMBER_MIN_MAX);
    }

    public static void testTypesConversion(final Property NUMBER_MIN_MAX) {
        final Property.Mutable mutable = ValuesBuilder.newMutable(NUMBER_MIN_MAX, "1");

        try {
            ValuesBuilder.newMutable(NUMBER_MIN_MAX, null);
            fail();
        } catch (final PropertyValidationException e) {
            assertThat(e.getValue()).isEqualTo(null);
            assertThat(e.getRestriction()).isInstanceOf(Number.Min.class);
            assertThat(((Number.Min) e.getRestriction()).value()).isEqualTo(1);
        }

        try {
            mutable.set("0");
            fail();
        } catch (final PropertyValidationException e) {
            assertThat(e.getValue()).isEqualTo(0);
            assertThat(e.getRestriction()).isInstanceOf(Number.Min.class);
            assertThat(((Number.Min) e.getRestriction()).value()).isEqualTo(1);
        }

        mutable.set("1");

        try {
            mutable.set("11");
            fail();
        } catch (final PropertyValidationException e) {
            assertThat(e.getValue()).isEqualTo(11);
            assertThat(e.getRestriction()).isInstanceOf(Number.Max.class);
            assertThat(((Number.Max) e.getRestriction()).value()).isEqualTo(10);
        }
    }

    @Test(groups = "unit")
    public void testNumberMinMax() {
        final Validator v = Validators.newStandard();

        try {
            v.validate(NUMBER_MIN_MAX, null);
            fail();
        } catch (final PropertyValidationException e) {
            assertThat(e.getValue()).isEqualTo(null);
            assertThat(e.getRestriction()).isInstanceOf(Number.Min.class);
            assertThat(((Number.Min) e.getRestriction()).value()).isEqualTo(1);
        }

        try {
            v.validate(NUMBER_MIN_MAX, 0);
            fail();
        } catch (final PropertyValidationException e) {
            assertThat(e.getValue()).isEqualTo(0);
            assertThat(e.getRestriction()).isInstanceOf(Number.Min.class);
            assertThat(((Number.Min) e.getRestriction()).value()).isEqualTo(1);
        }

        v.validate(NUMBER_MIN_MAX, 1);
        v.validate(NUMBER_MIN_MAX, 2);
        v.validate(NUMBER_MIN_MAX, 10);

        try {
            v.validate(NUMBER_MIN_MAX, 11);
            fail();
        } catch (final PropertyValidationException e) {
            assertThat(e.getValue()).isEqualTo(11);
            assertThat(e.getRestriction()).isInstanceOf(Number.Max.class);
            assertThat(((Number.Max) e.getRestriction()).value()).isEqualTo(10);
        }
    }

    @Test(groups = "unit")
    public void testRange() {
        final Validator v = Validators.newStandard();

        v.validate(NUMBER_RANGE, 1);
        v.validate(NUMBER_RANGE, 5);

        try {
            v.validate(NUMBER_RANGE, 6);
            fail();
        } catch (final PropertyValidationException e) {
            assertThat(e.getValue()).isEqualTo(6);
            assertThat(e.getRestriction()).isInstanceOf(Number.Range.class);
        }

        v.validate(NUMBER_RANGE, 8);
        v.validate(NUMBER_RANGE, 9);
        v.validate(NUMBER_RANGE, 10);

        try {
            v.validate(NUMBER_RANGE, 11);
            fail();
        } catch (final PropertyValidationException e) {
            assertThat(e.getValue()).isEqualTo(11);
            assertThat(e.getRestriction()).isInstanceOf(Number.Range.class);
        }

        v.validate(NUMBER_RANGE, 20);
        v.validate(NUMBER_RANGE, 21);
        v.validate(NUMBER_RANGE, 22);
    }

    @Test(groups = "unit")
    public void testPattern() {
        final Validator v = Validators.newStandard();

        v.validate(PATTERN_STRING, "1");
        v.validate(PATTERN_STRING, "10");

        try {
            v.validate(PATTERN_STRING, "100");
            fail();
        } catch (final PropertyValidationException e) {
            assertThat(e.getValue()).isEqualTo("100");
            assertThat(e.getRestriction()).isInstanceOf(Pattern.String.class);
        }
    }

    @Test(groups = "unit")
    public void testList() {
        final Validator v = Validators.newStandard();

        // strings

        v.validate(LIST_STRING, "1");
        v.validate(LIST_STRING, "2");

        try {
            v.validate(LIST_STRING, "3");
            fail();
        } catch (final PropertyValidationException e) {
            assertThat(e.getValue()).isEqualTo("3");
            assertThat(e.getRestriction()).isInstanceOf(List.String.class);
        }

        // numbers

        v.validate(LIST_NUMBER, 1);
        v.validate(LIST_NUMBER, 2);

        try {
            v.validate(LIST_NUMBER, 3);
            fail();
        } catch (final PropertyValidationException e) {
            assertThat(e.getValue()).isEqualTo(3);
            assertThat(e.getRestriction()).isInstanceOf(List.Number.class);
        }

        // enums

        v.validate(LIST_ENUM, "E1");
    }

    @Test(groups = "unit")
    public void testValidation() {
        final TestPersistence persistence = new TestPersistence();
        final Storage storage = StorageBuilder.newMemory(persistence);

        try {
            storage.put(NUMBER_MIN_MAX, 0);
            fail();
        } catch (final PropertyValidationException ignore) {
        }

        storage.put(NUMBER_MIN_MAX, 1);
        storage.put(NUMBER_MIN_MAX, 10);

        final Map<Property, Object> data = new LinkedHashMap<Property, Object>();
        data.put(NUMBER_MIN_MAX, 1);
        data.put(NUMBER_MIN_MAX, 2);
        storage.putAll(data, 0);

        data.put(NUMBER_MIN_MAX, 11);

        try {
            storage.putAll(data, 0);
            fail();
        } catch (final PropertyValidationException ignore) {
        }
    }

    static enum TestProperties implements Property {

        @Number.Min(1) @Number.Max(10) @Value(type = Type.INT, nullAs = "1")
        NUMBER_MIN_MAX,

        @Number.Range({1, 5, 8, 10, 20}) @Value(type = Type.INT, nullAs = "1")
        NUMBER_RANGE,

        @Pattern.String("\\d{1,2}") @Value(nullAs = "1")
        PATTERN_STRING,

        @List.String({"1", "2"}) @Value(nullAs = "1")
        LIST_STRING,

        @List.Number({1, 2}) @Value(type = Type.INT, nullAs = "1")
        LIST_NUMBER,

        @List.Enum(EnumList.class) @Value(nullAs = "E1")
        LIST_ENUM
        //
        ;

        private final String key = Caches.keyOf(this);

        @Override
        public String key() {
            return key;
        }
    }

    private static enum EnumList {
        E1, E2
    }

    private static class TestPersistence implements Storage.Persistence {
        @Nullable
        @Override
        public Object get(final int level, final Property property) {
            return null;
        }

        @Override
        public Map<Property, Object> get(final int level, final Set<Property> properties) {
            return Collections.emptyMap();
        }

        @Override
        public void put(final int level, @Nullable final Object value) {
        }

        @Override
        public void put(final int level, final Map<Property, Object> values) {
        }
    }
}
