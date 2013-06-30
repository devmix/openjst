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

package org.openjst.client.android.commons.events.types;

import org.openjst.client.android.commons.events.Event;
import org.openjst.protocols.basic.sessions.Session;

/**
 * @author Sergey Grachev
 */
public class ConnectionEvent implements Event {

    private Session session = null;

    public ConnectionEvent(final Session session) {
        this.session = session;
    }

    public boolean isSuccess() {
        return session != null;
    }

    public Session getSession() {
        return session;
    }
}
