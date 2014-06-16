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
import org.openjst.commons.properties.annotations.Levels;
import org.openjst.commons.properties.exceptions.PropertyLevelViolationException;
import org.openjst.commons.properties.exceptions.PropertyValidationException;
import org.openjst.commons.properties.validators.Validators;
import org.openjst.commons.properties.values.DefaultImmutable;
import org.openjst.commons.properties.values.ValuesBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static org.openjst.commons.properties.Caches.levelOf;
import static org.openjst.commons.properties.converters.Converters.basic;

/**
 * @author Sergey Grachev
 */
final class MemoryStorage implements PropertiesStorage {

    private static final int COUNT_OF_LEVELS = 0xFF;

    private final Map<Integer, Values> cacheValues = new ConcurrentHashMap<Integer, Values>();
    private final Map<String, State> states = new ConcurrentHashMap<String, State>();

    private final Persistence persistence;
    private final boolean lazyFetch;

    public MemoryStorage(final Persistence persistence, final boolean lazyFetch) {
        this.persistence = persistence;
        this.lazyFetch = lazyFetch;
    }

    @Override
    public Property.Immutable get(final Property property) {
        final Levels defaultLevel = levelOf(property);
        return get(defaultLevel, defaultLevel.min(), property);
    }

    @Override
    public Property.Immutable get(final int level, final Property property) {
        return get(levelOf(property), level, property);
    }

    @Override
    public Property.Values get(final int level, final Property... properties) {
        checkLevel(level, properties);
        final View result = new View(level, properties);
        if (!lazyFetch) {
            result.fetch();
        }
        return result;
    }

    @Override
    public void put(final Property property, @Nullable final Object value) {
        final Levels defaultLevel = levelOf(property);
        put(defaultLevel, defaultLevel.min(), property, value);
    }

    @Override
    public void put(final Property property, @Nullable final Object value, final int level) {
        put(levelOf(property), level, property, value);
    }

    @Override
    public void putAll(final Map<Property, Object> data, final int level) {
        checkLevel(level, data);
        valuesOf(level).put(data);
    }

    private void put(final Levels levels, final int level, final Property property, @Nullable final Object value) {
        checkLevel(levels, level);
        valuesOf(level).put(property, value);
    }

    private Property.Immutable get(final Levels levels, final int level, final Property property) {
        checkLevel(levels, level);
        return valuesOf(level).get(property);
    }

    private State stateOf(final Property property) {
        return stateOf(property.key());
    }

    private State stateOf(final String property) {
        synchronized (states) {
            State state = states.get(property);
            if (state == null) {
                state = new State();
                states.put(property, state);
            }
            return state;
        }
    }

    private Values valuesOf(final Integer level) {
        synchronized (cacheValues) {
            Values values = cacheValues.get(level);
            if (values == null) {
                values = new Values(level);
                cacheValues.put(level, values);
            }
            return values;
        }
    }

    private void checkLevel(final Levels levels, final int level) {
        if (levels.max() > 0xFF) {
            throw new PropertyLevelViolationException(levels, levels.max());
        }
        if (levels.min() > 0xFF || levels.min() > levels.max()) {
            throw new PropertyLevelViolationException(levels, levels.min());
        }
        if (level > levels.max() || level < levels.min()) {
            throw new PropertyLevelViolationException(levels, level);
        }
    }

    private void checkLevel(final int level, final Property... properties) {
        for (final Property property : properties) {
            checkLevel(levelOf(property), level);
        }
    }

    private void checkLevel(final int level, final Map<Property, Object> data) {
        for (final Property property : data.keySet()) {
            checkLevel(levelOf(property), level);
        }
    }

    private static final class State implements Serializable {

        private static final long serialVersionUID = 5122475954078311581L;

        private final BitSet exists = new BitSet(COUNT_OF_LEVELS);
        private final BitSet empty = new BitSet(COUNT_OF_LEVELS);
        private final ReentrantLock mutex = new ReentrantLock(true);

        public void update(final int level, final boolean isExists) {
            exists.set(level, isExists);
            empty.set(level, !isExists);
        }

        public boolean isNonEmptyAt(final int level) {
            return !empty.get(level);
        }

        public boolean isEmptyAt(final int level) {
            return empty.get(level);
        }

        public int previousExists(final int level) {
            return exists.previousSetBit(level);
        }
    }

    private final class Values implements Property.Values, Serializable {

        private static final long serialVersionUID = 8213489227588724549L;

        private final Map<String, Value> storage = new ConcurrentHashMap<String, Value>();
        private final Integer level;

        public Values(final Integer level) {
            this.level = level;
        }

        @Override
        public Property.Mutable get(final Property property) {
            final String key = property.key();
            final State state = stateOf(property);
            state.mutex.lock();
            try {
                Value value = storage.get(key);
                if (value == null) {
                    value = new Value(property);
                    storage.put(key, value);
                }
                return value;
            } finally {
                state.mutex.unlock();
            }
        }

        @Override
        public Property.Values put(final Property property, @Nullable final Object value) {
            final Object oldValue = get(property).get();
            if (value == null ? oldValue == null : value.equals(oldValue)) {
                return this;
            }

            final State state = stateOf(property);
            state.mutex.lock();
            try {
                final Object normalized = validateAndNormalize(property, value);
                persistence.put(level, normalized);
                state.update(level, normalized != null);
                get(property).set(normalized);
            } finally {
                state.mutex.unlock();
            }

            return this;
        }

        @Override
        public Property.Values put(final Map<Property, Object> values) {
            if (values.isEmpty()) {
                return this;
            }

            final Map<Property, Object> modified = new HashMap<Property, Object>(values);
            final Map<Property, State> states = lockPersist(values, modified);
            try {
                persistence.put(level, modified);

                for (final Property property : values.keySet()) {
                    final State state = states.get(property);
                    if (state != null) {
                        final Object value = values.get(property);
                        state.update(level, value != null);
                        get(property).set(value);
                    }
                }
            } finally {
                unlockProperties(states.values());
            }

            return this;
        }

        @Override
        public Property.Values clear(final Property property) {
            valuesOf(level).clear(property);
            return this;
        }

        @Override
        public Map<String, Object> asRaw() {
            fetch();

            final Map<String, Object> result = new HashMap<String, Object>(storage.size());
            for (final Map.Entry<String, Value> entry : storage.entrySet()) {
                result.put(entry.getKey(), entry.getValue().get());
            }

            return result;
        }

        @Override
        public Map<String, Object> asRaw(final Property... properties) {
            final Set<Property> unique = new LinkedHashSet<Property>(properties.length);
            Collections.addAll(unique, properties);

            fetch(unique);

            final Map<String, Object> result = new HashMap<String, Object>(unique.size());
            for (final Property property : unique) {
                result.put(property.key(), get(property).get());
            }

            return result;
        }

        private void fetch() {
            fetch0(lockForFetch());
        }

        private void fetch(final Set<Property> properties) {
            fetch0(lockForFetch(properties));
        }

        private void fetch0(final Map<String, State> states) {
            try {
                final Set<Property> notFetched = new LinkedHashSet<Property>();
                for (final String property : states.keySet()) {
                    final Value value = storage.get(property); // always exists
                    if (!value.isFetched()) {
                        notFetched.add(value.property());
                    }
                }

                if (!notFetched.isEmpty()) {
                    final Map<Property, Object> fetched = persistence.get(level, notFetched);
                    if (!fetched.isEmpty()) {
                        for (final Map.Entry<Property, Object> entry : fetched.entrySet()) {
                            final Property property = entry.getKey();
                            get(property).set(validateAndNormalize(property, entry.getValue()));
                        }
                    }
                }
            } finally {
                unlockProperties(states.values());
            }
        }

        private Map<String, State> lockForFetch() {
            final Map<String, State> states = new HashMap<String, State>(storage.size());
            try {
                for (final Map.Entry<String, Value> entry : storage.entrySet()) {
                    final Value value = entry.getValue();
                    final Property property = value.property();
                    lockNotFetched(states, property, value);
                }
            } catch (final Exception ignore) {
                unlockProperties(states.values());
                ignore.printStackTrace();
            }
            return states;
        }

        private Map<String, State> lockForFetch(final Collection<Property> properties) {
            final Map<String, State> states = new HashMap<String, State>(properties.size());
            try {
                for (final Property property : properties) {
                    final Value value = (Value) get(property);
                    lockNotFetched(states, property, value);
                }
            } catch (final Exception ignore) {
                unlockProperties(states.values());
                ignore.printStackTrace();
            }
            return states;
        }

        private void lockNotFetched(final Map<String, State> states, final Property property, final Value value) {
            if (!value.isFetched()) {
                final State state = stateOf(property);
                states.put(property.key(), state);
                state.mutex.lock();
            }
        }

        private Map<Property, State> lockPersist(final Map<Property, Object> values, final Map<Property, Object> modified) {
            final Map<Property, State> states = new HashMap<Property, State>(values.size());
            try {
                for (final Map.Entry<Property, Object> entry : values.entrySet()) {
                    final Property property = entry.getKey();
                    final Object value = validateAndNormalize(property, entry.getValue());
                    final Object oldValue = get(property).get();
                    if (value == null ? oldValue == null : value.equals(oldValue)) {
                        continue;
                    }
                    final State state = stateOf(property);
                    states.put(property, state);
                    state.mutex.lock();
                    modified.put(property, value);
                }
            } catch (final Exception ignore) {
                unlockProperties(states.values());
                modified.clear();
                if (ignore instanceof PropertyValidationException) {
                    throw (PropertyValidationException) ignore;
                }
                ignore.printStackTrace();
            }
            return states;
        }

        private void unlockProperties(final Collection<State> states) {
            for (final State state : states) {
                try {
                    state.mutex.unlock();
                } catch (final Exception ignore) {
                    ignore.printStackTrace();
                }
            }
        }

        private Object validateAndNormalize(final Property property, @Nullable final Object value) {
            final Object normalized = value == null ? null : basic().asOf(property.type(), value);
            Validators.standard().validate(property, normalized);
            return normalized;
        }

        private final class Value extends DefaultImmutable implements Property.Mutable {

            private static final long serialVersionUID = 6932215138332366181L;
            private boolean fetched;

            public Value(final Property property) {
                super(property, property.defaultValue());
                this.value = null; // clear default value
            }

            @Override
            public Property.Mutable set(@Nullable final Object value) {
                value(value);
                return this;
            }

            @Nullable
            @Override
            protected Object value() {
                final State state = stateOf(property);
                state.mutex.lock();
                try {
                    if (initialized && !fetched) { // load from external source
                        if (state.isNonEmptyAt(level)) {
                            final Object persistenceValue = persistence.get(level, property);
                            state.update(level, persistenceValue != null);
                            this.value = persistenceValue;
                        }
                        fetched = true;
                    }

                    if (state.isEmptyAt(level)) { // find 'super' property
                        final int existsOnLevel = state.previousExists(level);
                        if (existsOnLevel != -1 && existsOnLevel >= levelOf(property).min()) {
                            return valuesOf(existsOnLevel).get(property).get();
                        }
                        return property.defaultValue();
                    }

                } finally {
                    state.mutex.unlock();
                }
                return this.value;
            }

            @Override
            protected void value(@Nullable final Object value) {
                this.value = value;
                if (initialized) {
                    if (!fetched) { // set before get
                        fetched = true;
                    }
                }
            }

            public boolean isFetched() {
                return fetched;
            }
        }
    }

    private final class View implements Property.Values, Serializable {

        private static final long serialVersionUID = -6615159002504012694L;

        private final Set<Property> properties;
        private final int level;

        public View(final int level, final Property... properties) {
            this.level = level;
            this.properties = new HashSet<Property>(properties.length);
            Collections.addAll(this.properties, properties);
        }

        @Override
        public Property.Immutable get(final Property property) {
            return properties.contains(property) ? valuesOf(level).get(property) : ValuesBuilder.nullValue();
        }

        @Override
        public Property.Values put(final Property property, @Nullable final Object value) {
            if (properties.contains(property)) {
                valuesOf(level).put(property, value);
            }
            return this;
        }

        @Override
        public Property.Values put(final Map<Property, Object> values) {
            if (values.isEmpty()) {
                return this;
            }

            final Map<Property, Object> available = new HashMap<Property, Object>(values.size());
            for (final Map.Entry<Property, Object> entry : values.entrySet()) {
                final Property property = entry.getKey();
                if (properties.contains(property)) {
                    available.put(property, entry.getValue());
                }
            }
            valuesOf(level).put(available);

            return this;
        }

        @Override
        public Property.Values clear(final Property property) {
            if (properties.contains(property)) {
                valuesOf(level).clear(property);
            }
            return this;
        }

        @Override
        public Map<String, Object> asRaw() {
            return valuesOf(level).asRaw(properties.toArray(new Property[properties.size()]));
        }

        @Override
        public Map<String, Object> asRaw(final Property... properties) {
            return valuesOf(level).asRaw(properties);
        }

        private void fetch() {
            valuesOf(level).fetch(properties);
        }
    }
}
