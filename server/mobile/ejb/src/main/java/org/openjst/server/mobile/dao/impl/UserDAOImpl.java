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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.openjst.commons.dto.tuples.Triple;
import org.openjst.commons.dto.tuples.Tuples;
import org.openjst.commons.protocols.auth.SecretKeys;
import org.openjst.server.commons.AbstractEJB;
import org.openjst.server.commons.model.types.RoleType;
import org.openjst.server.commons.mq.ModelQuery;
import org.openjst.server.commons.mq.queries.VoidQuery;
import org.openjst.server.mobile.cdi.beans.MobileSession;
import org.openjst.server.mobile.dao.UserDAO;
import org.openjst.server.mobile.model.Account;
import org.openjst.server.mobile.model.Queries;
import org.openjst.server.mobile.model.User;
import org.openjst.server.mobile.mq.model.UserModel;
import org.openjst.server.mobile.mq.queries.UserQuery;
import org.openjst.server.mobile.utils.UserUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import java.util.List;

/**
 * @author Sergey Grachev
 */
@Stateless(name = UserDAOImpl.NAME)
public class UserDAOImpl extends AbstractEJB implements UserDAO {

    static final String NAME = "UserDAO";

    @Inject
    private MobileSession session;

    @Override
    @Nullable
    public User findById(final Long id) {
        try {
            return (User) em.createNamedQuery(Queries.User.FIND_BY_ID).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Nullable
    @Override
    public User findByAuthId(final String authId) {
        try {
            return (User) em.createNamedQuery(Queries.User.FIND_BY_AUTH_ID).setParameter("authId", authId).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Nullable
    @Override
    public User findByAccountAndName(final String accountAuthId, final String clientAuthId) {
        try {
            return (User) em.createNamedQuery(Queries.User.FIND_BY_ACCOUNT_AND_NAME)
                    .setParameter("accountAuthId", accountAuthId).setParameter("clientAuthId", clientAuthId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public Triple<Long, String, byte[]> findSecretKeyOf(final String account, final String user) {
        try {
            final Object[] key = (Object[]) em.createNamedQuery(Queries.User.FIND_SECRET_KEY_OF)
                    .setParameter("account", account)
                    .setParameter("user", user)
                    .getSingleResult();

            return Tuples.newTriple((Long) key[0], (String) key[1], (byte[]) key[2]);
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @Nullable
    public RoleType findUserRole(final Long userId) {
        try {
            return (RoleType) em.createNamedQuery(Queries.User.FIND_USER_ROLE)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<User> getListOf(final ModelQuery<UserQuery.Filter, UserQuery.Order, VoidQuery.Search> query) {
        //noinspection unchecked
        return em.createNamedQuery(Queries.User.GET_LIST_OF)
                .setParameter("accountId", query.getFilterValue(UserQuery.Filter.ACCOUNT_ID))
                .setFirstResult(query.getStartIndex()).setMaxResults(query.getPageSize()).getResultList();
    }

    @Override
    public long getCountOf(final ModelQuery<UserQuery.Filter, UserQuery.Order, VoidQuery.Search> query) {
        return (Long) em.createNamedQuery(Queries.User.GET_COUNT_OF)
                .setParameter("accountId", query.getFilterValue(UserQuery.Filter.ACCOUNT_ID))
                .getSingleResult();
    }

    @Override
    public User update(final User entity, final UserModel model) {
        entity.setAuthId(model.getAuthId());
        entity.setName(model.getName());
        entity.setRole(model.getRole());
        entity.setLanguage(model.getLanguage());
        final String password = StringUtils.trimToNull(model.getPassword());
        if (password != null) {
            entity.setPasswordSalt(SecretKeys.salt(User.DEFAULT_SALT_SIZE));
            entity.setPassword(UserUtils.encodePassword(password, SecretKeys.PBKDF2WithHmacSHA1, entity.getPasswordSalt()));
        }
        return em.merge(entity);
    }

    @Override
    public void delete(final Long id) {
        final User e = em.getReference(User.class, id);
        em.lock(e, LockModeType.PESSIMISTIC_WRITE);
        if (e.isSystem()) {
            throw new IllegalArgumentException("Can't delete system user");
        }
        em.remove(e);
    }

    @Override
    public User create(final UserModel model) {
        final User e = new User();
        e.setAuthId(model.getAuthId());
        e.setName(model.getName());
        e.setAccount(em.find(Account.class, model.getAccount().getId()));
        e.setRole(model.getRole());
        e.setLanguage(model.getLanguage());
        final String password = StringUtils.trimToNull(model.getPassword());
        if (password != null) {
            e.setPasswordSalt(SecretKeys.salt(User.DEFAULT_SALT_SIZE));
            e.setPassword(UserUtils.encodePassword(password, SecretKeys.PBKDF2WithHmacSHA1, e.getPasswordSalt()));
        }
        em.persist(e);
        return e;
    }
}
