/*
 * Copyright (C) 2014 OpenJST Project
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

package org.openjst.server.mobile.web.security;

import org.openjst.server.mobile.session.MobileSession;

import javax.inject.Inject;
import javax.management.*;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @author Sergey Grachev
 */
@WebListener
public class SecurityListener implements HttpSessionListener {

    @Inject
    private MobileSession session;

    @Override
    public void sessionCreated(final HttpSessionEvent se) {

    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
        try {
            // TODO check and remove this workaround for https://issues.jboss.org/browse/WFLY-3221
            clearCache(session.getUser().getName() + '@' + session.getUser().getAccount().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearCache(final String username) throws MBeanException, InstanceNotFoundException, ReflectionException, MalformedObjectNameException {
        final ObjectName domain = new ObjectName("jboss.as:subsystem=security,security-domain=OpenJSTServerMobileRealm");
        final Object[] params = {username};
        final String[] signature = {"java.lang.String"};
        final MBeanServer server = MBeanServerFactory.findMBeanServer(null).get(0);
        server.invoke(domain, "flushCache", params, signature);
    }
}
