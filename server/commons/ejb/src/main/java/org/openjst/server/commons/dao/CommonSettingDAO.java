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

package org.openjst.server.commons.dao;

import org.jetbrains.annotations.Nullable;
import org.openjst.server.commons.model.CommonSetting;
import org.openjst.server.commons.settings.Setting;
import org.openjst.server.commons.settings.SettingChangeEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public interface CommonSettingDAO {

    void update(Set<SettingChangeEvent.Item> settings);

    void update(Setting property, @Nullable Object value);

    List<CommonSetting> getList();

    @Nullable
    String findValueOfPreference(String name);

    Map<String, String> getValues(Set<String> query);
}
