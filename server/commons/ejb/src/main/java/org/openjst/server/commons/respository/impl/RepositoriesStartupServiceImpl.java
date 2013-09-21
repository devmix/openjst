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

package org.openjst.server.commons.respository.impl;

import org.openjst.server.commons.respository.ContentRepository;
import org.openjst.server.commons.respository.RepositoriesStartupService;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * @author Sergey Grachev
 */
@Startup
@Singleton(name = RepositoriesStartupServiceImpl.NAME)
public class RepositoriesStartupServiceImpl implements RepositoriesStartupService {

    static final String NAME = "RepositoriesStartupService";

    @Inject
    @Any
    private Instance<ContentRepository> repositories;

    @PostConstruct
    private void create() {
        for (final ContentRepository repository : repositories) {
            if (!repository.isExists()) {
                repository.initialize();
            }
        }
    }
}