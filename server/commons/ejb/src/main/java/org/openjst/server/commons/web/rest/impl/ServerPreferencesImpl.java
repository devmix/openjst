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

package org.openjst.server.commons.web.rest.impl;

import org.openjst.server.commons.dao.ServerPropertyDAO;
import org.openjst.server.commons.model.ServerPreference;
import org.openjst.server.commons.mq.model.ServerPreferenceModel;
import org.openjst.server.commons.web.rest.ServerPreferences;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Grachev
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ServerPreferencesImpl implements ServerPreferences {

    @EJB
    private ServerPropertyDAO serverPropertyDAO;

    @Override
    public List<ServerPreferenceModel> list() {
        final List<ServerPreference> list = serverPropertyDAO.getList();
        final List<ServerPreferenceModel> result = new ArrayList<ServerPreferenceModel>();
        for (final ServerPreference property : list) {
            result.add(ServerPreferenceModel.newServerPropertyModel(property.getName(), property.getValue(), property.getType()));
        }
        return result;
    }

    @Override
    public void update(final List<ServerPreferenceModel> preferences) {
        // TODO
    }
}
