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

package org.openjst.client.android.commons.managers.impl;

import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.openjst.client.android.commons.managers.SessionManager;
import org.openjst.client.android.commons.managers.SettingsManager;
import org.openjst.protocols.basic.session.Session;

/**
 * @author Sergey Grachev
 */
@JSTInject(SessionManager.class)
public class DefaultSessionManager implements SessionManager {

    @JSTInject
    private SettingsManager settings;

    private Session currentSession;

    public String getAccountId() {
        return currentSession == null ? "<UNKNOWN>" : currentSession.getAccountId();
    }

    public String getClientId() {
        return currentSession == null ? "<UNKNOWN>" : currentSession.getClientId();
    }

    public void setSession(final Session session) {
        this.currentSession = session;
    }
}
