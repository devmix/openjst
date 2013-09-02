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
import org.openjst.server.commons.dao.CommonSettingDAO;
import org.openjst.server.commons.model.types.SettingValueType;
import org.openjst.server.commons.services.SettingsManager;
import org.openjst.server.commons.settings.Setting;
import org.openjst.server.commons.settings.SettingChangeEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;

/**
 * @author Sergey Grachev
 */
@Startup
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class SettingsManagerImpl implements SettingsManager, Serializable {

    private static final long serialVersionUID = -4185189570168420033L;

    private static final int CACHED_PROPERTIES_SIZE = 0xFF;
    private static final String MISMATCH_TYPES = "Mismatch value type '%s' and property type '%s'";

    @EJB
    private CommonSettingDAO commonSettingDAO;

    @Inject
    private Event<SettingChangeEvent> preferenceChangeEvents;

    private CacheOnDemand<String, Object, Setting> cache;

    @PostConstruct
    public void create() {
        cache = Caches.newCacheOnDemandLRU(new CacheOnDemand.Listener<String, Object, Setting>() {
            @Nullable
            @Override
            public Object onFetch(final String key, @Nullable final Setting userData) {
                final String value = commonSettingDAO.findValueOfPreference(key);
                if (userData == null || value == null) {
                    return null;
                }
                return convertSetting(userData, value);
            }
        }, CACHED_PROPERTIES_SIZE, true);
    }

    @PreDestroy
    public void destroy() {
        cache.clear();
    }

    @Override
    public Object get(final Setting setting) {
        final Object value = cache.get(setting.key());
        return value == null ? setting.defaultValue() : value;
    }

    @Override
    public String getString(final Setting setting) {
        if (!SettingValueType.STRING.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.STRING, setting.type()));
        }
        return (String) get(setting);
    }

    @Override
    public int getInteger(final Setting setting) {
        if (!SettingValueType.INTEGER.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.INTEGER, setting.type()));
        }
        return (Integer) get(setting);
    }

    @Override
    public long getLong(final Setting setting) {
        if (!SettingValueType.LONG.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.LONG, setting.type()));
        }
        return (Long) get(setting);
    }

    @Override
    public float getFloat(final Setting setting) {
        if (!SettingValueType.FLOAT.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.FLOAT, setting.type()));
        }
        return (Float) get(setting);
    }

    @Override
    public double getDouble(final Setting setting) {
        if (!SettingValueType.DOUBLE.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.DOUBLE, setting.type()));
        }
        return (Double) get(setting);
    }

    @Override
    public boolean getBoolean(final Setting setting) {
        if (!SettingValueType.BOOLEAN.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.BOOLEAN, setting.type()));
        }
        return (Boolean) get(setting);
    }

    @Override
    public Date getDate(final Setting setting) {
        if (!SettingValueType.DATE.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.DATE, setting.type()));
        }
        return (Date) get(setting);
    }

    @Override
    public Map<Setting, Object> getList(final Setting... settings) {
        final Map<Setting, Object> result = new HashMap<Setting, Object>(settings.length);
        final Map<String, Setting> missed = new HashMap<String, Setting>(settings.length);
        for (final Setting property : settings) {
            if (cache.contains(property.key())) {
                result.put(property, cache.get(property.key()));
            } else {
                missed.put(property.key(), property);
            }
        }

        if (!missed.isEmpty()) {
            final Map<String, String> values = commonSettingDAO.getValues(missed.keySet());
            for (final Map.Entry<String, String> entry : values.entrySet()) {
                final String key = entry.getKey();
                final Setting property = missed.get(key);
                if (property != null) {
                    final Object value = convertSetting(property, entry.getValue());
                    cache.put(key, value);
                    result.put(property, value);
                }
            }
        }

        return result;
    }

    @Override
    public void setString(final Setting setting, final String value) {
        if (!SettingValueType.STRING.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.STRING, setting.type()));
        }
        set(setting, value);
    }

    @Override
    public void setInteger(final Setting setting, final int value) {
        if (!SettingValueType.INTEGER.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.INTEGER, setting.type()));
        }
        set(setting, value);
    }

    @Override
    public void setLong(final Setting setting, final long value) {
        if (!SettingValueType.LONG.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.LONG, setting.type()));
        }
        set(setting, value);
    }

    @Override
    public void setFloat(final Setting setting, final float value) {
        if (!SettingValueType.FLOAT.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.FLOAT, setting.type()));
        }
        set(setting, value);
    }

    @Override
    public void setDouble(final Setting setting, final double value) {
        if (!SettingValueType.DOUBLE.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.DOUBLE, setting.type()));
        }
        set(setting, value);
    }

    @Override
    public void setBoolean(final Setting setting, final boolean value) {
        if (!SettingValueType.BOOLEAN.equals(setting.type())) {
            throw new IllegalArgumentException("Mismatch value type and property type");
        }
        set(setting, value);
    }

    @Override
    public void setDate(final Setting setting, final Date value) {
        if (!SettingValueType.DATE.equals(setting.type())) {
            throw new IllegalArgumentException(String.format(MISMATCH_TYPES, SettingValueType.DATE, setting.type()));
        }
        set(setting, value.getTime());
    }

    private void notifyChange(final String name, final Object newValue, final Object oldValue) {
        preferenceChangeEvents.fire(SettingChangeEvent.newInstance(name, newValue, oldValue));
    }

    @Override
    public void set(final Setting setting, final Object value) {
        final Object oldValue = cache.get(setting.key());
        if (oldValue == null ? value != null : !oldValue.equals(value)) {
            commonSettingDAO.update(setting, value);
            cache.put(setting.key(), value);
            notifyChange(setting.key(), value, oldValue);
        }
    }

    @Override
    public Object convertSetting(final Setting setting, final String value) {
        switch (setting.type()) {
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

        throw new UnsupportedOperationException("Unknown type of property " + setting.type());
    }
}
