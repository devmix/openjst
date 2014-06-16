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

package org.openjst.commons.properties.storages;

import org.openjst.commons.properties.Property;
import org.openjst.commons.properties.annotations.Group;
import org.openjst.commons.properties.annotations.Levels;
import org.openjst.commons.properties.utils.PropertiesUtils;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.fest.assertions.Assertions.assertThat;
import static org.openjst.commons.properties.storages.PropertiesStorageTest.TestProperties.*;

/**
 * @author Sergey Grachev
 */
public final class PropertiesStorageTest {

    @Test(groups = "unit")
    public void testInheritance() {
        final PropertiesStorage storage = StorageBuilder.newMemory(new TestPersistence());

        assertThat(storage.get(LEVEL_0_1).asInt()).isEqualTo(0); // level 0
        storage.put(LEVEL_0_1, 1); // level 0
        assertThat(storage.get(LEVEL_0_1).asInt()).isEqualTo(1); // level 0

        storage.put(LEVEL_0_1, 2, 1);
        assertThat(storage.get(1, LEVEL_0_1).asInt()).isEqualTo(2);
        assertThat(storage.get(2, LEVEL_0_1).asInt()).isEqualTo(2);
        assertThat(storage.get(3, LEVEL_0_1).asInt()).isEqualTo(2);
        assertThat(storage.get(4, LEVEL_0_1).asInt()).isEqualTo(2);

        storage.put(LEVEL_0_1, 3, 3);
        assertThat(storage.get(2, LEVEL_0_1).asInt()).isEqualTo(2);
        assertThat(storage.get(3, LEVEL_0_1).asInt()).isEqualTo(3);
        assertThat(storage.get(4, LEVEL_0_1).asInt()).isEqualTo(3);

        storage.put(LEVEL_0_1, null, 3);
        assertThat(storage.get(4, LEVEL_0_1).asInt()).isEqualTo(2);
    }

    @Test(groups = "unit")
    public void testBatchGet() {
        final PropertiesStorage storage = StorageBuilder.newMemory(new TestPersistence() {
            @Override
            public Map<Property, Object> get(final int level, final Set<Property> properties) {
                super.get(level, properties);
                final Map<Property, Object> result = new HashMap<Property, Object>(properties.size());
                if (level == 2) {
                    for (final Property property : properties) {
                        if (LEVEL_1_1.equals(property)) {
                            result.put(property, 1);
                        } else if (LEVEL_1_2.equals(property)) {
                            result.put(property, 2);
                        } else if (LEVEL_1_3.equals(property)) {
                            result.put(property, 3);
                        }
                    }
                }
                return result;
            }
        });

        final Property.Values values = storage.get(2, LEVEL_1_1, LEVEL_1_2, LEVEL_1_3);

        assertThat(storage.get(1, LEVEL_1_1).asInt()).isEqualTo(0);
        assertThat(storage.get(1, LEVEL_1_2).asInt()).isEqualTo(0);
        assertThat(storage.get(1, LEVEL_1_3).asInt()).isEqualTo(0);

        assertThat(values.get(LEVEL_1_1).asInt()).isEqualTo(1);
        assertThat(values.get(LEVEL_1_2).asInt()).isEqualTo(2);
        assertThat(values.get(LEVEL_1_3).asInt()).isEqualTo(3);

        assertThat(storage.get(2, LEVEL_1_1).asInt()).isEqualTo(1);
        assertThat(storage.get(2, LEVEL_1_2).asInt()).isEqualTo(2);
        assertThat(storage.get(2, LEVEL_1_3).asInt()).isEqualTo(3);
    }

    @Test(groups = "unit")
    public void testBatchPut() {
        final PropertiesStorage storage = StorageBuilder.newMemory(new TestPersistence());
        final Map<Property, Object> data = new LinkedHashMap<Property, Object>();
        data.put(LEVEL_1_1, 1);
        data.put(LEVEL_1_2, 2);
        data.put(LEVEL_1_3, 3);
        storage.putAll(data, 2);

        assertThat(storage.get(1, LEVEL_1_1).asInt()).isEqualTo(0);
        assertThat(storage.get(1, LEVEL_1_2).asInt()).isEqualTo(0);
        assertThat(storage.get(1, LEVEL_1_3).asInt()).isEqualTo(0);

        assertThat(storage.get(2, LEVEL_1_1).asInt()).isEqualTo(1);
        assertThat(storage.get(2, LEVEL_1_2).asInt()).isEqualTo(2);
        assertThat(storage.get(2, LEVEL_1_3).asInt()).isEqualTo(3);
    }

    @Test(groups = "unit")
    public void testSubSet() {
        final TestPersistence persistence = new TestPersistence() {
            @Override
            public Map<Property, Object> get(final int level, final Set<Property> properties) {
                super.get(level, properties);
                final Map<Property, Object> result = new HashMap<Property, Object>(properties.size());
                for (final Property property : properties) {
                    result.put(property, 0);
                }
                return result;
            }
        };
        final PropertiesStorage storage = StorageBuilder.newMemory(persistence);

        final Property.Values values =
                storage.get(1, LEVEL_1_1, LEVEL_1_2);

        // check subset of newValues
        assertThat(values.get(LEVEL_1_1).asInt()).isEqualTo(0);
        values.put(LEVEL_1_1, 1);
        assertThat(values.get(LEVEL_1_1).asInt()).isEqualTo(1);

        // check storage
        assertThat(storage.get(1, LEVEL_1_1).asInt()).isEqualTo(1);
        values.put(LEVEL_1_1, 2);
        assertThat(storage.get(3, LEVEL_1_1).asInt()).isEqualTo(2);

        final Map<Property, Object> data = new LinkedHashMap<Property, Object>();
        data.put(LEVEL_1_1, 1);
        data.put(LEVEL_1_2, 2);
        data.put(LEVEL_1_3, 3);
        values.put(data);

        assertThat(values.get(LEVEL_1_1).asInt()).isEqualTo(1);
        assertThat(values.get(LEVEL_1_2).asInt()).isEqualTo(2);
        assertThat(values.get(LEVEL_1_3).asInt()).isEqualTo(0); // out of subset

        persistence.printCounters();
    }

    @Test(groups = "unit")
    public void testDefaultValues() {
        PropertiesStorage storage = StorageBuilder.newMemory(new TestPersistence());

        assertThat(storage.get(LEVEL_1_1).get()).isEqualTo(0);

        storage = StorageBuilder.newMemory(new TestPersistence());
        assertThat(storage.get(1, new Property[]{LEVEL_1_1}).get(LEVEL_1_1).get()).isEqualTo(0);
    }

    @Test(groups = "unit", enabled = false)
    public void testMT() throws InterruptedException {
        final TestPersistence persistence = new TestPersistence();
        final PropertiesStorage storage = StorageBuilder.newMemory(persistence);
        final Map<Property, Object> data = new LinkedHashMap<Property, Object>();
        data.put(LEVEL_1_1, 1);
        data.put(LEVEL_1_3, 3);

        final AtomicLong t = new AtomicLong(System.currentTimeMillis());
        final ExecutorService service = Executors.newFixedThreadPool(32);
        for (int i = 0; i < 32; i++) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        storage.get(LEVEL_1_1).asInt();
                        storage.get(1, LEVEL_1_1, LEVEL_1_2, LEVEL_1_3)
                                .put(LEVEL_1_1, 2)
                                .put(data)
                                .asRaw();
                        storage.put(LEVEL_1_1, 4, 1);
                        storage.put(LEVEL_1_3, 4, 1);
                        storage.put(LEVEL_1_1, 5, 2);
                        storage.get(3, LEVEL_1_1);
                        storage.get(4, LEVEL_1_2);
                        Thread.yield();
                        if (System.currentTimeMillis() - t.get() > 1000) {
                            t.set(System.currentTimeMillis());
                            persistence.printCounters();
                            persistence.resetCounters();
                        }
                    }
                }
            });
        }
        service.awaitTermination(1, TimeUnit.DAYS);
    }

    private static class TestPersistence implements PropertiesStorage.Persistence {

        private long singleGet = 0;
        private long multiGet = 0;
        private long singlePut = 0;
        private long multiPut = 0;

        @Nullable
        @Override
        public Object get(final int level, final Property property) {
            singleGet++;
            System.out.println("GET " + level + " " + property);
            return null;
        }

        @Override
        public Map<Property, Object> get(final int level, final Set<Property> properties) {
            multiGet++;
            System.out.println("GET " + level + " " + properties);
            return Collections.emptyMap();
        }

        @Override
        public void put(final int level, @Nullable final Object value) {
            singlePut++;
            System.out.println("PUT " + level + " " + value);
        }

        @Override
        public void put(final int level, final Map<Property, Object> values) {
            multiPut++;
            System.out.println("PUT " + level + " " + values);
        }

        public void resetCounters() {
            synchronized (this) {
                singleGet = multiGet = singlePut = multiPut = 0;
            }
        }

        public void printCounters() {
            synchronized (this) {
                System.out.println(singleGet + "/" + multiGet + "/" + singlePut + "/" + multiPut);
            }
        }
    }

    @Group(first = "G1", second = "sg1")
    static enum TestProperties implements Property {

        @Levels(min = 0)
        LEVEL_0_1,

        @Levels(min = 1)
        LEVEL_1_1,
        @Levels(min = 1)
        LEVEL_1_2,
        @Levels(min = 1)
        LEVEL_1_3
        //
        ;

        private final String key;

        TestProperties() {
            this.key = PropertiesUtils.createKeyByAnnotations(this);
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public Type type() {
            return Type.INT;
        }

        @Override
        public Property[] requires() {
            return NO_DEPENDENCIES;
        }

        @Nullable
        @Override
        public Object defaultValue() {
            return 0;
        }
    }
}
