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

import org.openjst.commons.properties.Caches;
import org.openjst.commons.properties.Property;
import org.openjst.commons.properties.annotations.Value;
import org.openjst.commons.properties.storages.annotations.Requires;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

import static org.openjst.commons.properties.storages.annotations.Requires.Field;

/**
 * @author Sergey Grachev
 */
public final class RequiresTest {

    @Test(groups = "unit")
    public void test() {

    }

    private Storage getStorage() {
        return StorageBuilder.newMemory(new Storage.Persistence() {
            @Nullable
            @Override
            public Object get(final int level, final Property property) {
                return null;
            }

            @Override
            public Map<Property, Object> get(final int level, final Set<Property> properties) {
                return null;
            }

            @Override
            public void put(final int level, @Nullable final Object value) {

            }

            @Override
            public void put(final int level, final Map<Property, Object> values) {

            }
        });
    }

    private static enum TestProperties implements Property {

        @Value(type = Type.BOOLEAN)
        @Requires({@Field({"P2", "P3"}), @Field(of = TestProperties.class, value = {"P4", "P5"})})
        EXTERNAL_DEPENDENCY,

        @Value(type = Type.BOOLEAN)
        P1,

        @Value(type = Type.BOOLEAN)
        @Requires(@Field("P3"))
        P2,

        @Value(type = Type.BOOLEAN)
        @Requires(@Field("P2"))
        P3,

        @Value(type = Type.BOOLEAN)
        P4,

        @Value(type = Type.BOOLEAN)
        P5,

        @Value(type = Type.BOOLEAN)
        P6,
        //
        ;

        private final String key = Caches.keyOf(this);

        @Override
        public String key() {
            return key;
        }
    }
}
