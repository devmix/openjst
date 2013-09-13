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

package org.openjst.server.mobile.web.rest.impl;

import org.openjst.server.commons.cdi.interceptors.UIService;
import org.openjst.server.commons.mq.access.ModelAccessRestriction;
import org.openjst.server.commons.mq.results.QuerySingleResult;
import org.openjst.server.commons.mq.results.Result;
import org.openjst.server.commons.services.SettingsManager;
import org.openjst.server.commons.settings.Setting;
import org.openjst.server.commons.settings.SettingGroups;
import org.openjst.server.mobile.web.rest.Settings;

import javax.ejb.EJB;
import javax.ws.rs.core.MultivaluedMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.openjst.server.commons.settings.SettingGroups.*;
import static org.openjst.server.mobile.Settings.APIBasic;
import static org.openjst.server.mobile.Settings.UI;

/**
 * @author Sergey Grachev
 */
@UIService
@ModelAccessRestriction(requireAdministrator = true)
public class SettingsImpl implements Settings {

    //TODO automatically generate tabs on UI using groups

    private static final SettingGroups GROUPS = new SettingGroups(
            group("interface",
                    section("scripts",
                            setting(UI.SCRIPTS_CACHE),
                            setting(UI.SCRIPTS_COMBINE),
                            setting(UI.SCRIPTS_DEBUG))),

            group("protocols",
                    section("basic",
                            setting(APIBasic.HOST),
                            setting(APIBasic.CLIENTS_PORT),
                            setting(APIBasic.SERVERS_PORT)))
    );

    @EJB
    private SettingsManager settingsManager;

    @Override
    public QuerySingleResult<Group> read(final String groupId) {
        final Group group = GROUPS.get(groupId);

        if (group == null) {
            return Result.notExists();
        }

        return QuerySingleResult.Builder.<Group>newInstance().value(group.clone().assignValues(settingsManager)).build();
    }

    @Override
    public QuerySingleResult<Void> update(final String groupId, final MultivaluedMap<String, String> values) {
        final Group group = GROUPS.get(groupId);

        if (group == null) {
            return Result.notExists();
        }

        final Map<String, Setting> settings = group.settings();
        final Map<Setting, Object> settingsForUpdate = new LinkedHashMap<Setting, Object>();
        for (final String key : values.keySet()) {
            if (settings.containsKey(key)) {
                final Setting setting = settings.get(key);
                final Object value = settingsManager.convertSetting(setting, values.getFirst(key));
                settingsForUpdate.put(setting, value);
            }
        }

        if (!settingsForUpdate.isEmpty()) {
            settingsManager.set(settingsForUpdate);
        }

        return QuerySingleResult.Builder.<Void>newInstance().status(Result.Status.OK).build();
    }
}
