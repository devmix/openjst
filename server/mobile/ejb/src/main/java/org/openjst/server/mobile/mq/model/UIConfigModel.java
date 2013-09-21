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

package org.openjst.server.mobile.mq.model;

import org.openjst.commons.dto.properties.GroupedProperties;

import java.util.Map;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("UnusedDeclaration")
public final class UIConfigModel {

    private final Map<String, String> i18n;
    private final GroupedProperties<String, String, Object> preferences;
    private final Map<String, Enum<?>[]> enums;
    private final UserModel user;

    public UIConfigModel(final UserModel user, final Map<String, String> i18n,
                         final GroupedProperties<String, String, Object> preferences,
                         final Map<String, Enum<?>[]> enums) {
        this.user = user;
        this.i18n = i18n;
        this.preferences = preferences;
        this.enums = enums;
    }

    public Map<String, String> getI18n() {
        return i18n;
    }

    public UserModel getUser() {
        return user;
    }

    public GroupedProperties<String, String, Object> getPreferences() {
        return preferences;
    }

    public Map<String, Enum<?>[]> getEnums() {
        return enums;
    }
}
