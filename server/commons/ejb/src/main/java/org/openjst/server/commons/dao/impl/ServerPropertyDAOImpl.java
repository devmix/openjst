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

package org.openjst.server.commons.dao.impl;

import org.jetbrains.annotations.Nullable;
import org.openjst.server.commons.AbstractEJB;
import org.openjst.server.commons.dao.ServerPropertyDAO;
import org.openjst.server.commons.model.CommonsQueries;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.util.*;

/**
 * @author Sergey Grachev
 */
@Stateless
public class ServerPropertyDAOImpl extends AbstractEJB implements ServerPropertyDAO {

    @Override
    public void update(final org.openjst.server.commons.preferences.ServerPreference property, @Nullable final Object value) {
        final org.openjst.server.commons.model.ServerPreference newProperty = new org.openjst.server.commons.model.ServerPreference();
        newProperty.setName(property.key());
        newProperty.setValue(value == null ? null : String.valueOf(value));
        newProperty.setType(property.type());
        em.merge(newProperty);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<org.openjst.server.commons.model.ServerPreference> getList() {
        return (List<org.openjst.server.commons.model.ServerPreference>) em.createNamedQuery(CommonsQueries.ServerProperty.GET_ALL).getResultList();
    }

    @Nullable
    @Override
    public String findValueOfPreference(final String name) {
        try {
            return (String) em.createNamedQuery(CommonsQueries.ServerProperty.FIND_VALUE_OF_PREFERENCE)
                    .setParameter("name", name).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Map<String, String> getValues(final Set<String> query) {
        if (query.isEmpty()) {
            return Collections.emptyMap();
        }

        @SuppressWarnings("unchecked")
        final List<String[]> list = em.createNamedQuery(CommonsQueries.ServerProperty.FIND_VALUES)
                .setParameter("names", query).getResultList();

        final Map<String, String> result = new HashMap<String, String>(list.size());
        for (final String[] row : list) {
            result.put(row[0], row[1]);

        }

        return result;
    }
}
