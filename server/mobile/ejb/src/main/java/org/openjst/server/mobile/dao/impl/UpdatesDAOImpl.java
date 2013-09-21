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
import org.openjst.server.mobile.dao.UpdatesDAO;
import org.openjst.server.mobile.events.UpdateChangeEvent;
import org.openjst.server.mobile.model.Account;
import org.openjst.server.mobile.model.Update;
import org.openjst.server.mobile.model.dto.UpdateToSentObj;
import org.openjst.server.mobile.mq.model.UpdateModel;
import org.openjst.server.mobile.mq.queries.UpdateQuery;
import org.openjst.server.mobile.respository.UpdatesRepository;
import org.openjst.server.mobile.session.MobileSession;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

import static org.openjst.server.mobile.model.Queries.Update.*;

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

    @Inject
    private Event<UpdateChangeEvent> updateEvents;

    @Override
    public Pair<List<Update>, Long> getListOf(final ModelQuery<UpdateQuery.Filter, UpdateQuery.Order, VoidQuery.Search> query) {
        final Long accountId = session.isAdministrator()
                ? (Long) query.getFilterValue(UpdateQuery.Filter.ACCOUNT_ID) : (Long) session.getAccountId();

        @SuppressWarnings("unchecked")
        final List<Update> list = em.createNamedQuery(GET_LIST_OF)
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
        e.setOS(model.getOS());
        e.setDescription(model.getDescription());
        e.setMajor(version.getMajor());
        e.setMinor(version.getMinor());
        e.setBuild(version.getBuild());
        e.setUploadDate(new Date());
        e.setLastUploadId(model.getUploadId());
        em.persist(e);

        updatesRepository.store(e.getAccount().getId(), e.getId(), e.getLastUploadId(), e.getOS());
        updateEvents.fire(new UpdateChangeEvent(UpdateChangeEvent.Action.ADD, e.getId()));

        return e;
    }

    @Nullable
    @Override
    public Update findById(final Long id) {
        try {
            return (Update) em.createNamedQuery(FIND_BY_ID)
                    .setParameter("id", id)
                    .setParameter("accountId", session.isAdministrator() ? null : session.getAccountId())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Update getById(final Long id) {
        return (Update) em.createNamedQuery(FIND_BY_ID)
                .setParameter("id", id)
                .setParameter("accountId", session.isAdministrator() ? null : session.getAccountId())
                .getSingleResult();
    }

    @Override
    public Update update(final Update e, final UpdateModel model) {
        final ApplicationVersion version = ApplicationVersion.parse(model.getVersion());

        e.setOS(model.getOS());
        e.setDescription(model.getDescription());
        e.setMajor(version.getMajor());
        e.setMinor(version.getMinor());
        e.setBuild(version.getBuild());

        if (!e.getLastUploadId().equals(model.getUploadId())) {
            e.setUploadDate(new Date());
            e.setLastUploadId(model.getUploadId());
            updatesRepository.store(e.getAccount().getId(), e.getId(), e.getLastUploadId(), e.getOS());
        }

        updateEvents.fire(new UpdateChangeEvent(UpdateChangeEvent.Action.UPDATE, e.getId()));

        return em.merge(e);
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
        updatesRepository.remove(e.getAccount().getId(), e.getId(), e.getOS());
        updateEvents.fire(new UpdateChangeEvent(UpdateChangeEvent.Action.REMOVE, e.getId()));
        em.remove(e);
    }

    @Override
    public Long getCountForAccount(final Long id) {
        return (Long) em.createNamedQuery(GET_COUNT_OF)
                .setParameter("accountId", session.isAdministrator() ? id : session.getAccountId())
                .getSingleResult();
    }

    @Override
    public UpdateToSentObj getUpdateToSent(final Long updateId) {
        return (UpdateToSentObj) em.createNamedQuery(GET_UPDATE_TO_SENT)
                .setParameter("updateId", updateId)
                .getSingleResult();
    }
}
