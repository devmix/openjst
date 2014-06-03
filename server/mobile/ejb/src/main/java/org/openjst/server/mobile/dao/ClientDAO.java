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

package org.openjst.server.mobile.dao;

import org.openjst.commons.security.auth.SecretKey;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.mq.ModelQuery;
import org.openjst.server.commons.network.Actor;
import org.openjst.server.mobile.model.Client;
import org.openjst.server.mobile.model.dto.ClientAuthenticationObj;
import org.openjst.server.mobile.model.dto.ClientConnectionObj;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

import static org.openjst.server.commons.mq.queries.VoidQuery.*;

/**
 * @author Sergey Grachev
 */
public interface ClientDAO {

    @Nullable
    ClientAuthenticationObj findCachedSecretKeyOf(String accountId, String clientId);

    @Nullable
    Client findByAccountAndClientId(String accountId, String clientId);

    void setOnlineStatus(Long clientId, ProtocolType protocolType, String host);

    void setOfflineStatus(Long clientId);

    void setOfflineStatusForAll();

    Client synchronizeClient(String accountId, String clientId, boolean cache, SecretKey secretKey);

    Long getOrCreateClientId(Long accountId, String clientId);

    long getOnlineCountOf(ModelQuery<Filter, Order, Search> query);

    List<ClientConnectionObj> getOnlineListOf(ModelQuery<Filter, Order, Search> query);

    Set<Actor<Long>> getAllClientsAsActors(Long accountId);
}
