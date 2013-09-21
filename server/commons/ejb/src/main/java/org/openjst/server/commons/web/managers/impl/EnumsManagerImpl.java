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

package org.openjst.server.commons.web.managers.impl;

import org.apache.commons.lang3.StringUtils;
import org.openjst.server.commons.model.ModelName;
import org.openjst.server.commons.web.managers.EnumsManager;
import org.openjst.server.commons.web.qualifiers.UIEnum;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
@ApplicationScoped
public class EnumsManagerImpl implements EnumsManager {

    private final Map<String, Enum<?>[]> cache = new LinkedHashMap<String, Enum<?>[]>();

    @Inject
    @UIEnum
    @SuppressWarnings("CdiInjectionPointsInspection")
    private Instance<List<Class<?>>> enums;

    @Override
    public Map<String, Enum<?>[]> getList() {
        synchronized (cache) {
            if (cache.isEmpty()) {
                for (final List<Class<?>> item : enums) {
                    for (final Class<?> clazz : item) {
                        final ModelName modelName = clazz.getAnnotation(ModelName.class);
                        final String className = modelName == null || StringUtils.isBlank(modelName.value())
                                ? clazz.getSimpleName() : modelName.value();
                        final Object[] names = clazz.getEnumConstants();
                        if (names != null) {
                            cache.put(className, (Enum<?>[]) names);
                        }
                    }
                }
            }
        }
        return cache;
    }
}
