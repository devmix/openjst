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
import org.openjst.server.commons.model.types.PreferenceType;
import org.openjst.server.commons.preferences.ServerPreference;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public final class Preferences {

    private Preferences() {
    }

    public static Set<ServerPreference> getAll() {
        final Set<ServerPreference> result = new LinkedHashSet<ServerPreference>();
        Collections.addAll(result, UI.values());
        return result;
    }

    public enum UI implements ServerPreference {

        SCRIPTS_CACHE("ui.scripts.cache", PreferenceType.BOOLEAN, true),
        SCRIPTS_DEBUG("ui.scripts.debug", PreferenceType.BOOLEAN, false),
        SCRIPTS_COMBINE("ui.scripts.combine", PreferenceType.BOOLEAN, true),
        SCRIPTS_USE_CONSOLE_OUTPUT("ui.scripts.log.useConsoleOutput", PreferenceType.BOOLEAN, false);

        //<editor-fold desc="implementation">
        private final String name;
        private final PreferenceType type;
        private final Object defaultValue;

        private UI(final String name, final PreferenceType type, final Object defaultValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        @Override
        public String key() {
            return name;
        }

        @Override
        public PreferenceType type() {
            return type;
        }

        @Override
        public Object defaultValue() {
            return defaultValue;
        }
        //</editor-fold>
    }

    public enum APIBasic implements ServerPreference {

        HOST("api.basic.host", PreferenceType.STRING, "localhost"),
        // clients to server connections
        CLIENTS_PORT("api.basic.clients-port", PreferenceType.INTEGER, ProtocolBasicConstants.DEFAULT_CLIENTS_PORT),
        // servers to server connections
        SERVERS_PORT("api.basic.servers-port", PreferenceType.INTEGER, ProtocolBasicConstants.DEFAULT_SERVERS_PORT);

        //<editor-fold desc="implementation">
        private final String name;
        private final PreferenceType type;
        private final Object defaultValue;

        private APIBasic(final String name, final PreferenceType type, final Object defaultValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        @Override
        public String key() {
            return name;
        }

        @Override
        public PreferenceType type() {
            return type;
        }

        @Override
        public Object defaultValue() {
            return defaultValue;
        }
        //</editor-fold>
    }
}
