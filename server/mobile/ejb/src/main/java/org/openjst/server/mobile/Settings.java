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

package org.openjst.server.mobile;

import org.openjst.protocols.basic.constants.ProtocolBasicConstants;
import org.openjst.server.commons.model.types.SettingValueType;
import org.openjst.server.commons.settings.Setting;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public final class Settings {

    private Settings() {
    }

    public static Set<Setting> getAll() {
        final Set<Setting> result = new LinkedHashSet<Setting>();
        Collections.addAll(result, UI.values());
        return result;
    }

    public enum UI implements Setting {

        SCRIPTS_CACHE("ui.scripts.cache", SettingValueType.BOOLEAN, true),
        SCRIPTS_DEBUG("ui.scripts.debug", SettingValueType.BOOLEAN, false),
        SCRIPTS_COMBINE("ui.scripts.combine", SettingValueType.BOOLEAN, true);

        //<editor-fold desc="implementation">
        private final String name;
        private final SettingValueType type;
        private final Object defaultValue;

        private UI(final String name, final SettingValueType type, final Object defaultValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        @Override
        public String key() {
            return name;
        }

        @Override
        public SettingValueType type() {
            return type;
        }

        @Override
        public Object defaultValue() {
            return defaultValue;
        }

        @Override
        public String toString() {
            return name;
        }
        //</editor-fold>
    }

    public enum APIBasic implements Setting {

        HOST("api.basic.host", SettingValueType.STRING, "localhost"),
        // clients to server connections
        CLIENTS_PORT("api.basic.clients-port", SettingValueType.INTEGER, ProtocolBasicConstants.DEFAULT_CLIENTS_PORT),
        // servers to server connections
        SERVERS_PORT("api.basic.servers-port", SettingValueType.INTEGER, ProtocolBasicConstants.DEFAULT_SERVERS_PORT);

        //<editor-fold desc="implementation">
        private final String name;
        private final SettingValueType type;
        private final Object defaultValue;

        private APIBasic(final String name, final SettingValueType type, final Object defaultValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        @Override
        public String key() {
            return name;
        }

        @Override
        public SettingValueType type() {
            return type;
        }

        @Override
        public Object defaultValue() {
            return defaultValue;
        }

        @Override
        public String toString() {
            return name;
        }
        //</editor-fold>
    }
}
