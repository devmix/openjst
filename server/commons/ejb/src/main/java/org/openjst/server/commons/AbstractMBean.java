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

package org.openjst.server.commons;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJBException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * @author Sergey Grachev
 */
public abstract class AbstractMBean extends AbstractEJB {

    private static final String DEFAULT_DOMAIN = "ojst";

    protected ObjectName objectName;
    protected MBeanServer mBeanServer;

    protected String getName() {
        return this.getClass().getSimpleName();
    }

    protected String getDomain() {
        return DEFAULT_DOMAIN;
    }

    @SuppressWarnings("EjbThisExpressionInspection")
    @PostConstruct
    public void create() {
        try {
            objectName = new ObjectName(getDomain(), "name", getName());
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
            destroy();
            ManagementFactory.getPlatformMBeanServer().registerMBean(this, objectName);
        } catch (Exception e) {
            throw new EJBException(e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (mBeanServer != null && mBeanServer.isRegistered(objectName)) {
                mBeanServer.unregisterMBean(objectName);
            }
        } catch (Exception e) {
            throw new EJBException(e);
        }
    }
}
