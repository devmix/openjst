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

import org.openjst.commons.security.auth.SecretKey;
import org.openjst.commons.security.auth.SecretKeys;
import org.openjst.server.commons.AbstractEJB;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.mq.ModelQuery;
import org.openjst.server.commons.network.Actor;
import org.openjst.server.mobile.dao.AccountDAO;
import org.openjst.server.mobile.dao.ClientDAO;
import org.openjst.server.mobile.model.Account;
import org.openjst.server.mobile.model.Client;
import org.openjst.server.mobile.model.User;
import org.openjst.server.mobile.model.dto.ClientAuthenticationObj;
import org.openjst.server.mobile.model.dto.ClientConnectionObj;
import org.openjst.server.mobile.utils.UserUtils;

import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.openjst.server.commons.mq.queries.VoidQuery.*;
import static org.openjst.server.mobile.model.Queries.Client.*;

/**
 * @author Sergey Grachev
 */
@Stateless
@PermitAll
public class ClientDAOImpl extends AbstractEJB implements ClientDAO {

    @EJB
    private AccountDAO accountDAO;

    @Nullable
    public Client findByAccountAndClientId(final String accountId, final String clientId) {
        try {
            return (Client) em.createNamedQuery(FIND_BY_ACCOUNT_AND_CLIENT_NAME)
                    .setParameter("accountId", accountId)
                    .setParameter("clientId", clientId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Nullable
    public Long getClientIdOfAccountByAuthId(final Long accountId, final String clientId) {
        try {
            return (Long) em.createNamedQuery(GET_CLIENT_ID_OF_ACCOUNT_BY_AUTH_ID)
                    .setParameter("accountId", accountId)
                    .setParameter("clientId", clientId)
                    .getSingleResult();
        } catch (NoResultException ignore) {
            return null;
        }
    }

    @Nullable
    @Override
    public ClientAuthenticationObj findCachedSecretKeyOf(final String accountId, final String clientId) {
        try {
            return (ClientAuthenticationObj) em.createNamedQuery(FIND_CACHED_SECRET_KEY_OF)
                    .setParameter("accountId", accountId)
                    .setParameter("clientId", clientId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void setOfflineStatusForAll() {
        em.createNamedQuery(SET_OFFLINE_STATUS_FOR_ALL).executeUpdate();
    }

    @Override
    public Client synchronizeClient(final String accountId, final String clientId, final boolean cacheThis, final SecretKey secretKey) {
        Client client = findByAccountAndClientId(accountId, clientId);

        if (client == null) {
            final Account account = accountDAO.findByAuthId(accountId);
            if (account == null) {
                throw new IllegalArgumentException("Unknown account " + accountId);
            }
            client = new Client();
            client.setAccount(account);
            client.setAuthId(clientId);
        }

        client.setAllowServerAuthentication(cacheThis);

        switch (secretKey.getType()) {
            case PLAIN:
                client.setPasswordSalt(SecretKeys.salt(User.DEFAULT_SALT_SIZE));
                client.setPassword(UserUtils.encodePassword(new String(secretKey.get()), SecretKeys.PBKDF2WithHmacSHA1, client.getPasswordSalt()));
                break;

            default:
                client.setPassword(UserUtils.encodePassword(secretKey));
        }

        if (client.getId() == null) {
            em.persist(client);
        } else {
            em.merge(client);
        }

        return client;
    }

    @Override
    public Long getOrCreateClientId(final Long accountId, final String clientAuthId) {
        Long clientId = getClientIdOfAccountByAuthId(accountId, clientAuthId);

        if (clientId == null) {
            final Client client = new Client();
            client.setAccount(em.getReference(Account.class, accountId));
            client.setAuthId(clientAuthId);
            // TODO if connected multiple protocols may fail constraint violation exception
            em.persist(client);
            clientId = client.getId();
        }

        return clientId;
    }

    @Override
    public long getOnlineCountOf(final ModelQuery<Filter, Order, Search> query) {
        return (Long) em.createNamedQuery(GET_ONLINE_COUNT_OF)
                .setFirstResult(query.getStartIndex())
                .setMaxResults(query.getPageSize())
                .getSingleResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ClientConnectionObj> getOnlineListOf(final ModelQuery<Filter, Order, Search> query) {
        return em.createNamedQuery(GET_ONLINE_LIST_OF)
                .setFirstResult(query.getStartIndex())
                .setMaxResults(query.getPageSize())
                .getResultList();
    }

    @Override
    public Set<Actor<Long>> getAllClientsAsActors(final Long accountId) {
        @SuppressWarnings("unchecked")
        final List<Actor<Long>> result = em.createNamedQuery(GET_ALL_CLIENTS_AS_ACTORS)
                .setParameter("accountId", accountId)
                .getResultList();
        return new HashSet<Actor<Long>>(result);
    }

    @Override
    public void setOnlineStatus(final Long clientId, final ProtocolType protocolType, final String host) {
        em.createNamedQuery(SET_ONLINE_STATUS)
                .setParameter("clientId", clientId)
                .setParameter("protocolType", protocolType)
                .setParameter("host", host)
                .executeUpdate();
    }

    @Override
    public void setOfflineStatus(final Long clientId) {
        em.createNamedQuery(SET_OFFLINE_STATUS)
                .setParameter("clientId", clientId)
                .executeUpdate();
    }
}
