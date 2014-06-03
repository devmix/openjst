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

package org.openjst.server.mobile.listeners;

import org.openjst.server.commons.network.Actor;
import org.openjst.server.commons.settings.Setting;
import org.openjst.server.commons.settings.SettingChangeEvent;
import org.openjst.server.mobile.Settings;
import org.openjst.server.mobile.model.dto.RPCMessageObj;
import org.openjst.server.mobile.network.BasicProtocolService;

import javax.annotation.security.PermitAll;
import javax.ejb.*;
import javax.enterprise.event.Observes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static javax.enterprise.event.TransactionPhase.AFTER_COMPLETION;

/**
 * @author Sergey Grachev
 */
@Singleton
@Asynchronous
@Lock(LockType.READ)
@PermitAll
public class SettingsEventsListener {

    private static final Set<Setting> BASIC_PROTOCOL_SETTINGS = new HashSet<Setting>(Arrays.asList(Settings.APIBasic.values()));

    @EJB
    private BasicProtocolService<Actor<Long>, RPCMessageObj> basicProtocolService;

    public void afterSettingUpdate(@Observes(during = AFTER_COMPLETION) final SettingChangeEvent event) {
        if (event.containsAny(BASIC_PROTOCOL_SETTINGS)) {
            basicProtocolService.restart();
            System.out.println(event);
        }
    }
}
