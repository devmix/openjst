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
import org.openjst.commons.protocols.auth.SecretKey;
import org.openjst.commons.protocols.auth.SecretKeys;
import org.openjst.server.commons.AbstractEJB;
import org.openjst.server.mobile.dao.AccountDAO;
import org.openjst.server.mobile.dao.ClientDAO;
import org.openjst.server.mobile.model.Account;
import org.openjst.server.mobile.model.Client;
import org.openjst.server.mobile.model.Queries;
import org.openjst.server.mobile.model.User;
import org.openjst.server.mobile.model.dto.ClientAuthenticationObj;
import org.openjst.server.mobile.utils.UserUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

/**
 * @author Sergey Grachev
 */
@Stateless(name = ClientDAOImpl.NAME)
public class ClientDAOImpl extends AbstractEJB implements ClientDAO {

    static final String NAME = "ClientDAO";

    @EJB
    private AccountDAO accountDAO;

    @Nullable
    public Client findByAccountAndClientId(final String accountId, final String clientId) {
        try {
            return (Client) em.createNamedQuery(Queries.Client.FIND_BY_ACCOUNT_AND_CLIENT_NAME)
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
            return (Long) em.createNamedQuery(Queries.Client.GET_CLIENT_ID_OF_ACCOUNT_BY_AUTH_ID)
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
            return (ClientAuthenticationObj) em.createNamedQuery(Queries.Client.FIND_CACHED_SECRET_KEY_OF)
                    .setParameter("accountId", accountId)
                    .setParameter("clientId", clientId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void changeStatusOfflineForAll() {
        em.createNamedQuery(Queries.Client.CHANGE_STATUS_OFFLINE_FOR_ALL).executeUpdate();
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
    public void changeStatusOnline(final Long clientId, final boolean isOnline) {
        em.createNamedQuery(Queries.Client.CHANGE_STATUS_ONLINE)
                .setParameter("clientId", clientId)
                .setParameter("isOnline", isOnline)
                .executeUpdate();
    }
}
