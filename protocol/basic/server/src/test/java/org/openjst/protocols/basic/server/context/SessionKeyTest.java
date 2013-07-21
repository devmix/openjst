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

package org.openjst.protocols.basic.server.context;

import org.openjst.protocols.basic.session.Session;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sergey Grachev
 */
public final class SessionKeyTest {

    @Test(groups = "unit")
    public void test() throws InterruptedException {
        final SessionKey clientKey = new SessionKey("1", "1");
        final SessionKey serverKey = new SessionKey("1");
        final Session session = mock(Session.class);

        when(session.getClientId()).thenReturn("1");
        when(session.getAccountId()).thenReturn("1");

        assertThat(clientKey).isNotEqualTo(serverKey);
        assertThat(clientKey).isEqualTo(new SessionKey(session, SessionKey.Type.CLIENT));
        assertThat(serverKey).isEqualTo(new SessionKey(session, SessionKey.Type.SERVER));
    }
}
