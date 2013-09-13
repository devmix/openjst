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
import org.openjst.commons.dto.ApplicationVersion;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.dto.tuples.Tuples;
import org.openjst.server.commons.AbstractEJB;
import org.openjst.server.commons.mq.ModelQuery;
import org.openjst.server.commons.mq.queries.VoidQuery;
import org.openjst.server.mobile.cdi.beans.MobileSession;
import org.openjst.server.mobile.dao.UpdatesDAO;
import org.openjst.server.mobile.model.Account;
import org.openjst.server.mobile.model.Queries;
import org.openjst.server.mobile.model.Update;
import org.openjst.server.mobile.mq.model.UpdateModel;
import org.openjst.server.mobile.mq.queries.UpdateQuery;
import org.openjst.server.mobile.respository.UpdatesRepository;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

/**
 * @author Sergey Grachev
 */
@Stateless(name = UpdatesDAOImpl.NAME)
public class UpdatesDAOImpl extends AbstractEJB implements UpdatesDAO {

    public static final String NAME = "UpdatesDAO";

    @Inject
    private MobileSession session;

    @EJB
    private UpdatesRepository updatesRepository;

    @Override
    public Pair<List<Update>, Long> getListOf(final ModelQuery<UpdateQuery.Filter, UpdateQuery.Order, VoidQuery.Search> query) {
        final Long accountId = session.isAdministrator()
                ? (Long) query.getFilterValue(UpdateQuery.Filter.ACCOUNT_ID) : (Long) session.getAccountId();

        @SuppressWarnings("unchecked")
        final List<Update> list = em.createNamedQuery(Queries.Update.GET_LIST_OF)
                .setParameter("accountId", accountId)
                .setFirstResult(query.getStartIndex()).setMaxResults(query.getPageSize()).getResultList();

        final Long total = getCountForAccount(accountId);

        return Tuples.newPair(list, total);
    }

    @Override
    public Update create(final UpdateModel model) {
        final ApplicationVersion version = ApplicationVersion.parse(model.getVersion());

        final Update e = new Update();
        e.setAccount(em.find(Account.class, session.isAdministrator() ? model.getAccountId() : session.getAccountId()));
        e.setDescription(model.getDescription());
        e.setMajor(version.getMajor());
        e.setMinor(version.getMinor());
        e.setBuild(version.getBuild());
        e.setUploadDate(new Date());
        e.setLastUploadId(model.getUploadId());
        em.persist(e);

        updatesRepository.store(e.getAccount().getId(), e.getId(), e.getLastUploadId());

        return e;
    }

    @Nullable
    @Override
    public Update findById(final Long id) {
        try {
            return (Update) em.createNamedQuery(Queries.Update.FIND_BY_ID)
                    .setParameter("id", id)
                    .setParameter("accountId", session.isAdministrator() ? null : session.getAccountId())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Update getById(final Long id) {
        return (Update) em.createNamedQuery(Queries.Update.FIND_BY_ID)
                .setParameter("id", id)
                .setParameter("accountId", session.isAdministrator() ? null : session.getAccountId())
                .getSingleResult();
    }

    @Override
    public Update update(final Update entity, final UpdateModel model) {
        final ApplicationVersion version = ApplicationVersion.parse(model.getVersion());

        entity.setDescription(model.getDescription());
        entity.setMajor(version.getMajor());
        entity.setMinor(version.getMinor());
        entity.setBuild(version.getBuild());

        if (!entity.getLastUploadId().equals(model.getUploadId())) {
            entity.setUploadDate(new Date());
            entity.setLastUploadId(model.getUploadId());
            updatesRepository.store(entity.getAccount().getId(), entity.getId(), entity.getLastUploadId());
        }

        return em.merge(entity);
    }

    @Nullable
    @Override
    public Update update(final Long id, final UpdateModel model) {
        final Update e = findById(id);
        return e != null ? update(e, model) : null;
    }

    @Override
    public void delete(final Long id) {
        final Update e = getById(id);
        updatesRepository.remove(e.getAccount().getId(), e.getId());
        em.remove(e);
    }

    @Override
    public Long getCountForAccount(final Long id) {
        return (Long) em.createNamedQuery(Queries.Update.GET_COUNT_OF)
                .setParameter("accountId", session.isAdministrator() ? id : session.getAccountId())
                .getSingleResult();
    }
}
