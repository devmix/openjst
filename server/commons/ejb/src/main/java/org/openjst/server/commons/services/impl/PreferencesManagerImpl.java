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

package org.openjst.server.commons.services.impl;

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.cache.CacheOnDemand;
import org.openjst.commons.cache.Caches;
import org.openjst.server.commons.dao.ServerPropertyDAO;
import org.openjst.server.commons.model.types.PreferenceType;
import org.openjst.server.commons.preferences.PreferenceChangeEvent;
import org.openjst.server.commons.preferences.ServerPreference;
import org.openjst.server.commons.services.PreferencesManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.MessageProducer;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sergey Grachev
 */
@Startup
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class PreferencesManagerImpl implements PreferencesManager, Serializable {

    private static final long serialVersionUID = -4185189570168420033L;

    private static final int CACHED_PROPERTIES_SIZE = 0xFF;
    private static final String MISMATCH_TYPES = "Mismatch value type '%s' and property type '%s'";

    @EJB
    private ServerPropertyDAO serverPropertyDAO;

    @Inject
    private Event<PreferenceChangeEvent> preferenceChangeEvents;

    private final Map<String, MessageProducer> listeners = new ConcurrentHashMap<String, MessageProducer>();
    private CacheOnDemand<String, Object, ServerPreference> cache;

    @PostConstruct
    public void create() {
        cache = Caches.newCacheOnDemandLRU(new CacheOnDemand.Listener<String, Object, ServerPreference>() {
            @Nullable
            @Override
            public Object onFetch(final String key, @Nullable final ServerPreference userData) {
                final String value = serverPropertyDAO.findValueOfPreference(key);
                if (userData == null || value == null) {
                    return null;
                }
                return convertProperty(userData, value);
            }
        }, CACHED_PROPERTIES_SIZE, true);
    }

    @PreDestroy
    public void destroy() {
        cache.clear();
    }

    @Override
    public String getString(final ServerPreference property) {
        if (!PreferenceType.STRING.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.STRING, property.type()));
        }
        return (String) get(property);
    }

    @Override
    public int getInteger(final ServerPreference property) {
        if (!PreferenceType.INTEGER.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.INTEGER, property.type()));
        }
        return (Integer) get(property);
    }

    @Override
    public long getLong(final ServerPreference property) {
        if (!PreferenceType.LONG.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.LONG, property.type()));
        }
        return (Long) get(property);
    }

    @Override
    public float getFloat(final ServerPreference property) {
        if (!PreferenceType.FLOAT.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.FLOAT, property.type()));
        }
        return (Float) get(property);
    }

    @Override
    public double getDouble(final ServerPreference property) {
        if (!PreferenceType.DOUBLE.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.DOUBLE, property.type()));
        }
        return (Double) get(property);
    }

    @Override
    public boolean getBoolean(final ServerPreference property) {
        if (!PreferenceType.BOOLEAN.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.BOOLEAN, property.type()));
        }
        return (Boolean) get(property);
    }

    @Override
    public Date getDate(final ServerPreference property) {
        if (!PreferenceType.DATE.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.DATE, property.type()));
        }
        return (Date) get(property);
    }

    @Override
    public Map<ServerPreference, Object> getList(final ServerPreference... properties) {
        final Map<ServerPreference, Object> result = new HashMap<ServerPreference, Object>(properties.length);
        final Map<String, ServerPreference> missed = new HashMap<String, ServerPreference>(properties.length);
        for (final ServerPreference property : properties) {
            if (cache.contains(property.key())) {
                result.put(property, cache.get(property.key()));
            } else {
                missed.put(property.key(), property);
            }
        }

        if (!missed.isEmpty()) {
            final Map<String, String> values = serverPropertyDAO.getValues(missed.keySet());
            for (final Map.Entry<String, String> entry : values.entrySet()) {
                final String key = entry.getKey();
                final ServerPreference property = missed.get(key);
                if (property != null) {
                    final Object value = convertProperty(property, entry.getValue());
                    cache.put(key, value);
                    result.put(property, value);
                }
            }
        }

        return result;
    }

    @Override
    public void setString(final ServerPreference property, final String value) {
        if (!PreferenceType.STRING.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.STRING, property.type()));
        }
        set(property, value);
    }

    @Override
    public void setInteger(final ServerPreference property, final int value) {
        if (!PreferenceType.INTEGER.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.INTEGER, property.type()));
        }
        set(property, value);
    }

    @Override
    public void setLong(final ServerPreference property, final long value) {
        if (!PreferenceType.LONG.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.LONG, property.type()));
        }
        set(property, value);
    }

    @Override
    public void setFloat(final ServerPreference property, final float value) {
        if (!PreferenceType.FLOAT.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.FLOAT, property.type()));
        }
        set(property, value);
    }

    @Override
    public void setDouble(final ServerPreference property, final double value) {
        if (!PreferenceType.DOUBLE.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.DOUBLE, property.type()));
        }
        set(property, value);
    }

    @Override
    public void setBoolean(final ServerPreference property, final boolean value) {
        if (!PreferenceType.BOOLEAN.equals(property.type())) {
            throw new IllegalArgumentException("Mismatch value type and property type");
        }
        set(property, value);
    }

    @Override
    public void setDate(final ServerPreference property, final Date value) {
        if (!PreferenceType.DATE.equals(property.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, PreferenceType.DATE, property.type()));
        }
        set(property, value.getTime());
    }

    private void notifyChange(final String name, final Object newValue, final Object oldValue) {
        preferenceChangeEvents.fire(PreferenceChangeEvent.newInstance(name, newValue, oldValue));
    }

    private Object get(final ServerPreference property) {
        final Object value = cache.get(property.key());
        return value == null ? property.defaultValue() : value;
    }

    public void set(final ServerPreference property, final Object value) {
        final Object oldValue = cache.get(property.key());
        if (oldValue == null ? value != null : !oldValue.equals(value)) {
            serverPropertyDAO.update(property, value);
            cache.put(property.key(), value);
            notifyChange(property.key(), value, oldValue);
        }
    }

    private Object convertProperty(final ServerPreference property, final String value) {
        switch (property.type()) {
            case INTEGER: {
                return Integer.parseInt(value);
            }
            case LONG: {
                return Long.parseLong(value);
            }
            case FLOAT: {
                return Float.parseFloat(value);
            }
            case STRING: {
                return value;
            }
            case DATE: {
                final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(Long.parseLong(value));
                return calendar.getTime();
            }
            case BOOLEAN: {
                return Boolean.parseBoolean(value);
            }
            case DOUBLE: {
                return Double.parseDouble(value);
            }
        }

        throw new UnsupportedOperationException("Unknown type of property " + property.type());
    }
}
