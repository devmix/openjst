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

package org.openjst.server.mobile.network.impl;

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.network.Actor;
import org.openjst.server.commons.network.MessageProducer;
import org.openjst.server.commons.network.Route;
import org.openjst.server.commons.network.Router;
import org.openjst.server.commons.network.producer.MessageProducerFactory;
import org.openjst.server.commons.network.router.RouterFactory;
import org.openjst.server.mobile.dao.AccountDAO;
import org.openjst.server.mobile.dao.ClientDAO;
import org.openjst.server.mobile.dao.RPCMessagesDAO;
import org.openjst.server.mobile.model.dto.RPCMessageObj;
import org.openjst.server.mobile.model.dto.SimpleActorObj;
import org.openjst.server.mobile.network.BasicProtocolService;
import org.openjst.server.mobile.network.NetworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.PermitAll;
import javax.ejb.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
@Startup
@Singleton(name = NetworkServiceImpl.NAME)
@TransactionManagement(TransactionManagementType.BEAN)
@PermitAll
public class NetworkServiceImpl implements NetworkService {

    public static final String NAME = "NetworkService";
    public static final Logger LOG = LoggerFactory.getLogger(NetworkService.class);
    public static final int THREADS_PER_PRODUCER = 2;

    @EJB
    private RPCMessagesDAO<Actor<Long>, RPCMessageObj> rpcDAO;

    @EJB
    private ClientDAO clientDAO;

    @EJB
    private AccountDAO accountDAO;

    @EJB
    private BasicProtocolService<Actor<Long>, RPCMessageObj> basicProtocolService;

    private final Map<ProtocolType, Route<Actor<Long>, RPCMessageObj>> routes = new HashMap<ProtocolType, Route<Actor<Long>, RPCMessageObj>>();

    private Router<Actor<Long>, RPCMessageObj> clientsMsgRouter;
    private MessageProducer<RPCMessagesDAO<Actor<Long>, RPCMessageObj>, Actor<Long>, RPCMessageObj> clientsMsgProducer;

    private Router<Actor<Long>, RPCMessageObj> serversMsgRouter;
    private MessageProducer<RPCMessagesDAO<Actor<Long>, RPCMessageObj>, Actor<Long>, RPCMessageObj> serversMsgProducer;

    @PostConstruct
    private void create() {
        routes.clear();
        routes.put(ProtocolType.BASIC, basicProtocolService);

        clientsMsgRouter = RouterFactory.createRouterForClients(routes);
        clientsMsgProducer = MessageProducerFactory.newProducerForClients(clientsMsgRouter, rpcDAO, THREADS_PER_PRODUCER);
        clientsMsgProducer.start();

        serversMsgRouter = RouterFactory.createRouterForServers(routes);
        serversMsgProducer = MessageProducerFactory.newProducerForServers(serversMsgRouter, rpcDAO, THREADS_PER_PRODUCER);
        serversMsgProducer.start();
    }

    @PreDestroy
    private void destroy() {
        if (clientsMsgProducer != null) {
            clientsMsgProducer.stop();
            clientsMsgProducer = null;
        }

        if (serversMsgProducer != null) {
            serversMsgProducer.stop();
            serversMsgProducer = null;
        }
    }

    @Override
    public void clientRPCForwardTo(final Long accountId, final Long clientId, final RPCMessageFormat format, final byte[] data) {
        LOG.debug("createForwardToClient accountId {} clientId {} msg-fmt {} msg {}", accountId, clientId, format, data);
        rpcDAO.createForwardToClient(accountId, clientId, format, data);
        clientsMsgProducer.checkMessagesFor(new SimpleActorObj(clientId));
    }

    @Override
    public void clientRPCForwardBroadcast(final Long accountId, final RPCMessageFormat format, final byte[] data, final boolean onlyOnline) {
        LOG.debug("clientRPCForwardBroadcast accountId {} msg-fmt {} msg {} onlyOnline {}", accountId, format, data, onlyOnline);
        final Set<Actor<Long>> actors = onlyOnline ? clientsMsgRouter.getConnected() : clientDAO.getAllClientsAsActors(accountId);
        for (final Actor<Long> actor : actors) {
            clientRPCForwardTo(accountId, actor.getId(), format, data);
        }
    }

    @Override
    public void serverRPCForwardTo(final Long accountId, final Long clientId, final RPCMessageFormat format, final byte[] data) {
        LOG.debug("createForwardToServer accountId {} clientId {} msg-fmt {} msg {}", accountId, clientId, format, data);
        rpcDAO.createForwardToServer(accountId, clientId, format, data);
        serversMsgProducer.checkMessagesFor(new SimpleActorObj(accountId));
    }

    @Override
    public void clientConnected(final Actor<Long> actor, final ProtocolType protocolType, final String host) {
        clientDAO.setOnlineStatus(actor.getId(), protocolType, host);
        clientsMsgRouter.connected(actor, protocolType);
        clientsMsgProducer.checkMessagesFor(actor);
    }

    @Override
    public void clientDisconnected(final Actor<Long> actor, final ProtocolType protocolType) {
        clientsMsgRouter.disconnected(actor, protocolType);
        if (!clientsMsgRouter.isConnected(actor)) {
            clientDAO.setOfflineStatus(actor.getId());
        }
    }

    @Override
    public void serverConnected(final Actor<Long> actor, final ProtocolType protocolType, final String remoteHost) {
        accountDAO.setOnlineStatus(actor.getId(), protocolType, remoteHost);
        serversMsgRouter.connected(actor, protocolType);
        serversMsgProducer.checkMessagesFor(actor);
    }

    @Override
    public void serverDisconnect(final Actor<Long> actor, final ProtocolType protocolType) {
        serversMsgRouter.disconnected(actor, protocolType);
        if (!serversMsgRouter.isConnected(actor)) {
            accountDAO.setOfflineStatus(actor.getId());
        }
    }
}
