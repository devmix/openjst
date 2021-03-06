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

/**
 * @author Sergey Grachev
 */
public final class Environment {

    private Environment() {
    }

    public static final class Cache {

        public static final String CONTAINER = "java:jboss/infinispan/container/openjst-cache";
        public static final String WEB_IU = "openjst-web-ui-cache";

        private Cache() {
        }
    }

    public static final class JMX {

        public static final String DOMAIN = "openjst-mobile-server";

        private JMX() {
        }
    }

    public static final class Repository {

        public static final String DEFAULT = "server-mobile";

        private Repository() {
        }
    }
}
