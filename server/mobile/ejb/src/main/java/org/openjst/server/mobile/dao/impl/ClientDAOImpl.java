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

package org.openjst.server.mobile.dao.impl;

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.dto.tuples.Tuples;
import org.openjst.server.commons.AbstractEJB;
import org.openjst.server.mobile.dao.ClientDAO;
import org.openjst.server.mobile.model.Queries;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * @author Sergey Grachev
 */
@Stateless(name = ClientDAOImpl.NAME)
public class ClientDAOImpl extends AbstractEJB implements ClientDAO {

    static final String NAME = "ClientDAO";

    @Nullable
    @Override
    public Pair<String, byte[]> findCachedSecretKeyOf(final String accountId, final String clientId) {
        try {
            final Object[] key = (Object[]) em.createNamedQuery(Queries.Client.FIND_CACHED_SECRET_KEY_OF)
                    .setParameter("accountId", accountId)
                    .setParameter("clientId", clientId)
                    .getSingleResult();
            return Tuples.newPair((String) key[0], (byte[]) key[1]);
        } catch (NoResultException e) {
            return null;
        }
    }
}
