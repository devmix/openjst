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

import org.openjst.commons.properties.Property;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
final class DefaultValues implements Property.Values, Serializable {

    private static final long serialVersionUID = -8139458314878168072L;

    private final Map<String, Property.Mutable> storage = new HashMap<String, Property.Mutable>();
    private final boolean autoCreate;

    public DefaultValues(final boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    @Override
    public Property.Immutable of(final Property property) {
        final String key = property.key();
        Property.Mutable value;
        synchronized (storage) {
            value = storage.get(key);
            if (value == null) {
                if (autoCreate) {
                    final Object defaultValue = property.defaultValue();
                    value = ValuesBuilder.mutable(property, defaultValue);
                    storage.put(key, value);
                } else {
                    return ValuesBuilder.nullValue();
                }
            }
        }
        return value;
    }

    @Override
    public Property.Values put(final Property property, @Nullable final Object value) {
        if (value == null) {
            clear(property);
        } else {
            final String key = property.key();
            synchronized (storage) {
                final Property.Mutable exists = storage.get(key);
                if (exists != null) {
                    exists.set(value);
                } else {
                    storage.put(key, ValuesBuilder.mutable(property, value));
                }
            }
        }
        return this;
    }

    @Override
    public Property.Values put(final Map<Property, Object> values) {
        if (!values.isEmpty()) {
            synchronized (storage) {
                for (final Map.Entry<Property, Object> entry : values.entrySet()) {
                    put(entry.getKey(), entry.getValue());
                }
            }
        }
        return this;
    }

    @Override
    public Property.Values clear(final Property property) {
        synchronized (storage) {
            storage.remove(property.key());
        }
        return this;
    }

    @Override
    public Map<String, Object> asRaw() {
        synchronized (storage) {
            if (storage.isEmpty()) {
                return Collections.emptyMap();
            }

            final Map<String, Object> result = new HashMap<String, Object>(storage.size());
            for (final Map.Entry<String, Property.Mutable> entry : storage.entrySet()) {
                result.put(entry.getKey(), entry.getValue().get());
            }

            return result;
        }
    }

    @Override
    public Map<String, Object> asRaw(final Property... properties) {
        if (properties.length == 0) {
            return Collections.emptyMap();
        }

        synchronized (storage) {
            final Map<String, Object> result = new HashMap<String, Object>(properties.length);
            for (final Property property : properties) {
                result.put(property.key(), of(property).get());
            }
            return result;
        }
    }
}
