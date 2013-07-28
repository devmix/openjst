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

package org.openjst.server.mobile.services.impl;

import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;
import org.openjst.server.mobile.Environment;
import org.openjst.server.mobile.services.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Sergey Grachev
 */
@Singleton
@Startup
public class CacheServiceImpl implements CacheService {

    private static final Logger LOG = LoggerFactory.getLogger(CacheService.class);

    private CacheContainer cacheContainer;

    @PostConstruct
    private void init() {
        try {
            cacheContainer = (CacheContainer) new InitialContext().lookup(Environment.Cache.CONTAINER);
            clearWebUI();
        } catch (NamingException e) {
            LOG.error("Can't get Infinispan cache", e);
        }
    }

    @Override
    public Cache<String, String> getWebUI() {
        return cacheContainer.getCache(Environment.Cache.WEB_IU);
    }

    @Override
    public void clearWebUI() {
        getWebUI().clear();
    }
}
