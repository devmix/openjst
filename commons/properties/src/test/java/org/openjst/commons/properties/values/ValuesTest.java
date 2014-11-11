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

package org.openjst.commons.properties.values;

import org.fest.assertions.Assertions;
import org.joda.time.LocalTime;
import org.openjst.commons.properties.Caches;
import org.openjst.commons.properties.Property;
import org.openjst.commons.properties.annotations.Key;
import org.openjst.commons.properties.annotations.Value;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static org.openjst.commons.properties.Property.Immutable;
import static org.openjst.commons.properties.Property.Values;
import static org.openjst.commons.properties.values.ValuesBuilder.newImmutable;
import static org.openjst.commons.properties.values.ValuesBuilder.nullValue;
import static org.openjst.commons.properties.values.ValuesTest.TestProperties.*;

/**
 * @author Sergey Grachev
 */
public final class ValuesTest {

    @Test(groups = "unit")
    public void testNulls() {
        assertThat(ValuesBuilder.newValues().get(NULL)).isEqualTo(nullValue());
        assertThat(ValuesBuilder.newValues(true).get(NULL_WITH_DEFAULT).get()).isEqualTo("test");
    }

    @Test(groups = "unit")
    public void testKeys() {
        Assertions.assertThat(BOOLEAN.key()).isEqualTo("g1.sg1.boolean");
        Assertions.assertThat(BYTE.key()).isEqualTo("g1.sg1.byte");
    }

    @Test(groups = "unit")
    public void testValues() {
        final Values values = ValuesBuilder.newValues()
                .put(NULL, "1")
                .put(BOOLEAN, true)
                .put(BYTE, 11)
                .put(SHORT, 22)
                .put(INT, 33)
                .put(LONG, 44)
                .put(FLOAT, 55)
                .put(DOUBLE, 66)
                .put(CHAR, 77)
                .put(STRING, "88")
                .put(TIME, new LocalTime(99));

        assertThat(values.get(NULL).get()).isEqualTo("1");
        values.clear(NULL);
        assertThat(values.get(NULL)).isEqualTo(nullValue());

        assertThat(values.get(BOOLEAN).asBoolean()).isEqualTo(true);
        assertThat(values.get(BYTE).asByte()).isEqualTo((byte) 11);
        assertThat(values.get(SHORT).asShort()).isEqualTo((short) 22);
        assertThat(values.get(INT).asInt()).isEqualTo(33);
        assertThat(values.get(LONG).asLong()).isEqualTo(44);
        assertThat(values.get(FLOAT).asFloat()).isEqualTo(55);
        assertThat(values.get(DOUBLE).asDouble()).isEqualTo(66);
        assertThat(values.get(CHAR).asChar()).isEqualTo((char) 77);
        assertThat(values.get(STRING).asString()).isEqualTo("88");
        assertThat(values.get(TIME).asTime()).isEqualTo(new LocalTime(99));
    }

    @Test(groups = "unit")
    public void testBoolean() {
        final Immutable value = newImmutable(BOOLEAN, true);

        assertThat(value.asBoolean()).isTrue();
        assertThat(value.asByte()).isEqualTo((byte) 1);
        assertThat(value.asShort()).isEqualTo((short) 1);
        assertThat(value.asInt()).isEqualTo(1);
        assertThat(value.asLong()).isEqualTo(1);
        assertThat(value.asFloat()).isEqualTo(1);
        assertThat(value.asDouble()).isEqualTo(1);
        assertThat(value.asChar()).isEqualTo('Y');
        assertThat(value.asString()).isEqualTo("true");
        try {
            value.asTime();
            fail();
        } catch (final Exception ignore) {
        }
    }

    @Test(groups = "unit")
    public void testByte() {
        final Immutable value = newImmutable(BYTE, Byte.MAX_VALUE);

        assertThat(value.asBoolean()).isTrue();
        assertThat(value.asByte()).isEqualTo(Byte.MAX_VALUE);
        assertThat(value.asShort()).isEqualTo((short) Byte.MAX_VALUE);
        assertThat(value.asInt()).isEqualTo(Byte.MAX_VALUE);
        assertThat(value.asLong()).isEqualTo(Byte.MAX_VALUE);
        assertThat(value.asFloat()).isEqualTo(Byte.MAX_VALUE);
        assertThat(value.asDouble()).isEqualTo(Byte.MAX_VALUE);
        assertThat(value.asChar()).isEqualTo((char) Byte.MAX_VALUE);
        assertThat(value.asString()).isEqualTo(java.lang.String.valueOf(Byte.MAX_VALUE));
        assertThat(value.asTime()).isEqualTo(LocalTime.fromMillisOfDay(Byte.MAX_VALUE));
    }

    @Test(groups = "unit")
    public void testShort() {
        final Immutable value = newImmutable(SHORT, Short.MAX_VALUE);

        assertThat(value.asBoolean()).isTrue();
        assertThat(value.asByte()).isEqualTo((byte) Short.MAX_VALUE);
        assertThat(value.asShort()).isEqualTo(Short.MAX_VALUE);
        assertThat(value.asInt()).isEqualTo(Short.MAX_VALUE);
        assertThat(value.asLong()).isEqualTo(Short.MAX_VALUE);
        assertThat(value.asFloat()).isEqualTo(Short.MAX_VALUE);
        assertThat(value.asDouble()).isEqualTo(Short.MAX_VALUE);
        assertThat(value.asChar()).isEqualTo((char) Short.MAX_VALUE);
        assertThat(value.asString()).isEqualTo(java.lang.String.valueOf(Short.MAX_VALUE));
        assertThat(value.asTime()).isEqualTo(LocalTime.fromMillisOfDay(Short.MAX_VALUE));
    }

    @Test(groups = "unit")
    public void testInt() {
        final Immutable value = newImmutable(INT, Integer.MAX_VALUE);

        assertThat(value.asBoolean()).isTrue();
        assertThat(value.asByte()).isEqualTo((byte) Integer.MAX_VALUE);
        assertThat(value.asShort()).isEqualTo((short) Integer.MAX_VALUE);
        assertThat(value.asInt()).isEqualTo(Integer.MAX_VALUE);
        assertThat(value.asLong()).isEqualTo(Integer.MAX_VALUE);
        assertThat(value.asFloat()).isEqualTo(Integer.MAX_VALUE);
        assertThat(value.asDouble()).isEqualTo(Integer.MAX_VALUE);
        assertThat(value.asChar()).isEqualTo((char) Integer.MAX_VALUE);
        assertThat(value.asString()).isEqualTo(java.lang.String.valueOf(Integer.MAX_VALUE));
        assertThat(value.asTime()).isEqualTo(LocalTime.fromMillisOfDay(Integer.MAX_VALUE));
    }

    @Test(groups = "unit")
    public void testLong() {
        final Immutable value = newImmutable(LONG, Long.MAX_VALUE);

        assertThat(value.asBoolean()).isTrue();
        assertThat(value.asByte()).isEqualTo((byte) Long.MAX_VALUE);
        assertThat(value.asShort()).isEqualTo((short) Long.MAX_VALUE);
        assertThat(value.asInt()).isEqualTo((int) Long.MAX_VALUE);
        assertThat(value.asLong()).isEqualTo(Long.MAX_VALUE);
        assertThat(value.asFloat()).isEqualTo(Long.MAX_VALUE);
        assertThat(value.asDouble()).isEqualTo(Long.MAX_VALUE);
        assertThat(value.asChar()).isEqualTo((char) Long.MAX_VALUE);
        assertThat(value.asString()).isEqualTo(java.lang.String.valueOf(Long.MAX_VALUE));
        assertThat(value.asTime()).isEqualTo(LocalTime.fromMillisOfDay(Long.MAX_VALUE));
    }

    @Test(groups = "unit")
    public void testFloat() {
        final Immutable value = newImmutable(FLOAT, Float.MAX_VALUE);

        assertThat(value.asBoolean()).isTrue();
        assertThat(value.asByte()).isEqualTo((byte) Float.MAX_VALUE);
        assertThat(value.asShort()).isEqualTo((short) Float.MAX_VALUE);
        assertThat(value.asInt()).isEqualTo((int) Float.MAX_VALUE);
        assertThat(value.asLong()).isEqualTo((long) Float.MAX_VALUE);
        assertThat(value.asFloat()).isEqualTo(Float.MAX_VALUE);
        assertThat(value.asDouble()).isEqualTo(Float.MAX_VALUE);
        assertThat(value.asChar()).isEqualTo((char) Float.MAX_VALUE);
        assertThat(value.asString()).isEqualTo(java.lang.String.valueOf(Float.MAX_VALUE));
        assertThat(value.asTime()).isEqualTo(LocalTime.fromMillisOfDay((long) Float.MAX_VALUE));
    }

    @Test(groups = "unit")
    public void testDouble() {
        final Immutable value = newImmutable(DOUBLE, Double.MAX_VALUE);

        assertThat(value.asBoolean()).isTrue();
        assertThat(value.asByte()).isEqualTo((byte) Double.MAX_VALUE);
        assertThat(value.asShort()).isEqualTo((short) Double.MAX_VALUE);
        assertThat(value.asInt()).isEqualTo((int) Double.MAX_VALUE);
        assertThat(value.asLong()).isEqualTo((long) Double.MAX_VALUE);
        assertThat(value.asFloat()).isEqualTo((float) Double.MAX_VALUE);
        assertThat(value.asDouble()).isEqualTo(Double.MAX_VALUE);
        assertThat(value.asChar()).isEqualTo((char) Double.MAX_VALUE);
        assertThat(value.asString()).isEqualTo(java.lang.String.valueOf(Double.MAX_VALUE));
        assertThat(value.asTime()).isEqualTo(LocalTime.fromMillisOfDay((long) Double.MAX_VALUE));
    }

    @Test(groups = "unit")
    public void testChar() {
        final Immutable value = newImmutable(CHAR, 'Y');

        assertThat(value.asBoolean()).isTrue();
        assertThat(value.asByte()).isEqualTo((byte) 'Y');
        assertThat(value.asShort()).isEqualTo((short) 'Y');
        assertThat(value.asInt()).isEqualTo((int) 'Y');
        assertThat(value.asLong()).isEqualTo((long) 'Y');
        assertThat(value.asFloat()).isEqualTo((float) 'Y');
        assertThat(value.asDouble()).isEqualTo('Y');
        assertThat(value.asChar()).isEqualTo('Y');
        assertThat(value.asString()).isEqualTo(java.lang.String.valueOf('Y'));
        try {
            value.asTime();
            fail();
        } catch (final Exception ignore) {
        }
    }

    @Test(groups = "unit")
    public void testTime() {
        final Immutable value = newImmutable(TIME, new LocalTime(10, 11, 12, 13));

        assertThat(value.asInt()).isEqualTo(36672013);
        assertThat(value.asLong()).isEqualTo(36672013);
        assertThat(value.asFloat()).isEqualTo(36672013);
        assertThat(value.asDouble()).isEqualTo(36672013);
        assertThat(value.asString()).isEqualTo("10:11:12:013");
        try {
            value.asBoolean();
            fail();
        } catch (final Exception ignore) {
        }
        try {
            value.asByte();
            fail();
        } catch (final Exception ignore) {
        }
        try {
            value.asShort();
            fail();
        } catch (final Exception ignore) {
        }
        try {
            value.asChar();
            fail();
        } catch (final Exception ignore) {
        }
    }

    @Test(groups = "unit")
    public void testString() {
        assertThat(newImmutable(STRING, "true").asBoolean()).isTrue();
        assertThat(newImmutable(STRING, "1").asByte()).isEqualTo((byte) 1);
        assertThat(newImmutable(STRING, "2").asShort()).isEqualTo((short) 2);
        assertThat(newImmutable(STRING, "3").asInt()).isEqualTo(3);
        assertThat(newImmutable(STRING, "4").asLong()).isEqualTo((long) 4);
        assertThat(newImmutable(STRING, "5.5").asFloat()).isEqualTo((float) 5.5);
        assertThat(newImmutable(STRING, "6.6").asDouble()).isEqualTo(6.6);
        assertThat(newImmutable(STRING, "7").asChar()).isEqualTo('7');
        assertThat(newImmutable(STRING, "8").asString()).isEqualTo("8");
        assertThat(newImmutable(STRING, "NULL").asEnum(TestProperties.class)).isEqualTo(NULL);
    }

    @Key(group = "G1", subGroup = "sg1")
    static enum TestProperties implements Property {

        NULL,

        @Value(nullAs = "test")
        NULL_WITH_DEFAULT,

        @Value(type = Type.BOOLEAN, nullAs = "false")
        BOOLEAN,

        @Value(type = Type.BYTE, nullAs = "1")
        BYTE,

        @Value(type = Type.SHORT, nullAs = "2")
        SHORT,

        @Value(type = Type.INT, nullAs = "3")
        INT,

        @Value(type = Type.LONG, nullAs = "4")
        LONG,

        @Value(type = Type.FLOAT, nullAs = "5")
        FLOAT,

        @Value(type = Type.DOUBLE, nullAs = "6")
        DOUBLE,

        @Value(type = Type.CHAR, nullAs = "7")
        CHAR,

        @Value(nullAs = "8")
        STRING,

        @Value(type = Type.TIME, nullAs = "00:00:00:009")
        TIME,
        //
        ;

        private final String key = Caches.keyOf(this);

        @Override
        public String key() {
            return key;
        }
    }
}
