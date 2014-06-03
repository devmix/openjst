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

import org.openjst.server.commons.AbstractEJB;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.mq.ModelQuery;
import org.openjst.server.commons.mq.queries.VoidQuery;
import org.openjst.server.mobile.dao.AccountDAO;
import org.openjst.server.mobile.model.Account;
import org.openjst.server.mobile.model.dto.AccountAuthenticationObj;
import org.openjst.server.mobile.model.dto.AccountConnectionObj;
import org.openjst.server.mobile.mq.model.AccountModel;
import org.openjst.server.mobile.mq.queries.AccountQuery;

import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import java.util.List;

import static org.openjst.server.commons.mq.queries.VoidQuery.*;
import static org.openjst.server.mobile.model.Queries.Account.*;

/**
 * @author Sergey Grachev
 */
@Stateless
@PermitAll
public class AccountDAOImpl extends AbstractEJB implements AccountDAO {

    @Nullable
    @Override
    public Account findById(final long id) {
        try {
            return (Account) em.createNamedQuery(FIND_BY_ID).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Account update(final Account account, final AccountModel model) {
        account.setAuthId(model.getAuthId());
        account.setName(model.getName());
        account.setApiKey(model.getApiKey());
        return em.merge(account);
    }

    @Override
    public Account create(final AccountModel model) {
        final Account e = new Account();
        e.setAuthId(model.getAuthId());
        e.setName(model.getName());
        e.setApiKey(model.getApiKey());
        em.persist(e);
        return e;
    }

    @Override
    public List<Account> getListOf(final ModelQuery<VoidQuery.Filter, AccountQuery.Order, VoidQuery.Search> query) {
        //noinspection unchecked
        return em.createNamedQuery(GET_LIST_OF)
                .setFirstResult(query.getStartIndex())
                .setMaxResults(query.getPageSize())
                .getResultList();
    }

    @Override
    public long getCountOf(final ModelQuery<VoidQuery.Filter, AccountQuery.Order, VoidQuery.Search> query) {
        return (Long) em.createNamedQuery(GET_COUNT_OF)
                .getSingleResult();
    }

    @Override
    public void delete(final Long id) {
        final Account account = em.getReference(Account.class, id);
        em.lock(account, LockModeType.PESSIMISTIC_WRITE);
        if (account.isSystem()) {
            throw new IllegalArgumentException("Can't delete system account");
        }
        em.remove(account);
    }

    @Override
    public void setOfflineStatus(final Long accountId) {
        em.createNamedQuery(SET_OFFLINE_STATUS)
                .setParameter("accountId", accountId)
                .executeUpdate();
    }

    @Override
    public void setOnlineStatus(final Long accountId, final ProtocolType protocolType, final String remoteHost) {
        em.createNamedQuery(SET_ONLINE_STATUS)
                .setParameter("accountId", accountId)
                .setParameter("protocolType", protocolType)
                .setParameter("host", remoteHost)
                .executeUpdate();
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
    public List<AccountConnectionObj> getOnlineListOf(final ModelQuery<Filter, Order, Search> query) {
        return em.createNamedQuery(GET_ONLINE_LIST_OF)
                .setFirstResult(query.getStartIndex())
                .setMaxResults(query.getPageSize())
                .getResultList();
    }

    @Nullable
    @Override
    public Account findSystemAccount() {
        @SuppressWarnings("unchecked")
        final List<Account> accounts = em.createNamedQuery(FIND_SYSTEM).getResultList();
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    @Override
    @Nullable
    public AccountAuthenticationObj findAccountByApiKey(final String apiKey) {
        try {
            return (AccountAuthenticationObj) em.createNamedQuery(FIND_ACCOUNT_BY_API_KEY)
                    .setParameter("apiKey", apiKey).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Nullable
    @Override
    public Account findByAuthId(final String authId) {
        try {
            return (Account) em.createNamedQuery(FIND_BY_AUTH_ID).setParameter("authId", authId).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public int getCountOfClients(final long accountId) {
        return ((Long) em.createNamedQuery(GET_COUNT_OF_CLIENTS)
                .setParameter("accountId", accountId)
                .getSingleResult()).intValue();
    }

    @Override
    public int getCountOfUsers(final long accountId) {
        return ((Long) em.createNamedQuery(GET_COUNT_OF_USERS)
                .setParameter("accountId", accountId)
                .getSingleResult()).intValue();
    }
}
