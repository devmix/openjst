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

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.server.commons.AbstractEJB;
import org.openjst.server.commons.model.types.MessageDeliveryState;
import org.openjst.server.commons.network.Actor;
import org.openjst.server.mobile.dao.RPCMessagesDAO;
import org.openjst.server.mobile.model.Account;
import org.openjst.server.mobile.model.Client;
import org.openjst.server.mobile.model.RPCMessageForwardToClient;
import org.openjst.server.mobile.model.RPCMessageForwardToServer;
import org.openjst.server.mobile.model.dto.RPCMessageObj;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.*;

import static org.openjst.server.mobile.model.Queries.RPCMessage.*;

/**
 * @author Sergey Grachev
 */
@Stateless
@PermitAll
public class RPCMessagesDAOImpl extends AbstractEJB implements RPCMessagesDAO<Actor<Long>, RPCMessageObj> {

    private static final List<MessageDeliveryState> COMPLETED_STATES = Arrays.asList(
            MessageDeliveryState.OK,
            MessageDeliveryState.INTERNAL_SERVER_ERROR
    );

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void createForwardToServer(final Long accountId, final Long clientId, final RPCMessageFormat format, final byte[] data) {
        final RPCMessageForwardToServer rpc = new RPCMessageForwardToServer();
        rpc.setClient(em.getReference(Client.class, clientId));
        rpc.setAccount(em.getReference(Account.class, accountId));
        rpc.setFormat(format);
        rpc.setData(data);
        em.merge(rpc);
    }

    @Override
    public int getCountFromClients(final long accountId) {
        return ((Long) em.createNamedQuery(GET_COUNT_FROM_CLIENTS)
                .setParameter("accountId", accountId)
                .getSingleResult()).intValue();
    }

    @Override
    public int getCountFromServer(final long accountId) {
        return ((Long) em.createNamedQuery(GET_COUNT_FROM_SERVER)
                .setParameter("accountId", accountId)
                .getSingleResult()).intValue();
    }

    @Override
    public int getCountDeliveredToClients(final long accountId) {
        return ((Long) em.createNamedQuery(GET_COUNT_DELIVERED_TO_CLIENTS)
                .setParameter("accountId", accountId)
                .setParameter("state", COMPLETED_STATES)
                .getSingleResult()).intValue();
    }

    @Override
    public int getCountDeliveredToServer(final long accountId) {
        return ((Long) em.createNamedQuery(GET_COUNT_DELIVERED_TO_SERVER)
                .setParameter("accountId", accountId)
                .setParameter("state", COMPLETED_STATES)
                .getSingleResult()).intValue();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void createForwardToClient(final Long accountId, final Long clientId, final RPCMessageFormat format, final byte[] data) {
        final RPCMessageForwardToClient rpc = new RPCMessageForwardToClient();
        rpc.setClient(em.getReference(Client.class, clientId));
        rpc.setAccount(em.getReference(Account.class, accountId));
        rpc.setFormat(format);
        rpc.setData(data);
        em.merge(rpc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RPCMessageObj> getNotDeliveredToClients(final Actor<Long> actor, final int maxSize) {
        return em.createNamedQuery(GET_NOT_DELIVERED_TO_CLIENTS)
                .setParameter("recipientId", actor.getId())
                .setParameter("successStates", COMPLETED_STATES)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RPCMessageObj> getNotDeliveredToServers(final Actor<Long> actor, final int maxSize) {
        return em.createNamedQuery(GET_NOT_DELIVERED_TO_SERVERS)
                .setParameter("recipientId", actor.getId())
                .setParameter("successStates", COMPLETED_STATES)
                .getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void deliverySuccess(final RPCMessageObj message) {
        em.createNamedQuery(DELIVERY_SUCCESS)
                .setParameter("state", MessageDeliveryState.OK)
                .setParameter("msgId", message.getId())
                .executeUpdate();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void deliveryFail(final RPCMessageObj message, final MessageDeliveryState error, final String errorMessage) {
        em.createNamedQuery(DELIVERY_FAIL)
                .setParameter("state", MessageDeliveryState.OK)
                .setParameter("msgId", message.getId())
                .setParameter("msg", errorMessage)
                .executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Actor<Long>> filterClientsWithNotDeliveredMessages(final Set<Actor<Long>> actors) {
        final List<Long> ids = new ArrayList<Long>(actors.size());
        for (final Actor<Long> recipient : actors) {
            ids.add(recipient.getId());
        }
        return new HashSet<Actor<Long>>(em.createNamedQuery(FILTER_CLIENTS_WITH_NOT_DELIVERED_MESSAGES)
                .setParameter("recipients", ids)
                .setParameter("successStates", COMPLETED_STATES)
                .getResultList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Actor<Long>> filterServersWithNotDeliveredMessages(final Set<Actor<Long>> actors) {
        final List<Long> ids = new ArrayList<Long>(actors.size());
        for (final Actor<Long> recipient : actors) {
            ids.add(recipient.getId());
        }
        return new HashSet<Actor<Long>>(em.createNamedQuery(FILTER_SERVERS_WITH_NOT_DELIVERED_MESSAGES)
                .setParameter("recipients", ids)
                .setParameter("successStates", COMPLETED_STATES)
                .getResultList());
    }
}
