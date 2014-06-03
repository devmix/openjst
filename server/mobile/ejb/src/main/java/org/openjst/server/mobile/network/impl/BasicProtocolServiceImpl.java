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

import org.openjst.commons.conversion.encodings.Base64;
import org.openjst.commons.security.auth.SecretKey;
import org.openjst.protocols.basic.constants.ProtocolResponseStatus;
import org.openjst.protocols.basic.context.SendFuture;
import org.openjst.protocols.basic.events.*;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.beans.Parameter;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.AuthClientRequestPacket;
import org.openjst.protocols.basic.pdu.packets.AuthServerRequestPacket;
import org.openjst.protocols.basic.server.Server;
import org.openjst.protocols.basic.server.ServerEventsListener;
import org.openjst.server.commons.model.types.MessageDeliveryState;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.network.Actor;
import org.openjst.server.commons.network.DeliveryResult;
import org.openjst.server.commons.services.SettingsManager;
import org.openjst.server.mobile.Settings;
import org.openjst.server.mobile.dao.AccountDAO;
import org.openjst.server.mobile.dao.ClientDAO;
import org.openjst.server.mobile.model.Client;
import org.openjst.server.mobile.model.dto.AccountAuthenticationObj;
import org.openjst.server.mobile.model.dto.ClientAuthenticationObj;
import org.openjst.server.mobile.model.dto.RPCMessageObj;
import org.openjst.server.mobile.network.BasicProtocolService;
import org.openjst.server.mobile.network.NetworkService;
import org.openjst.server.mobile.network.session.ClientSession;
import org.openjst.server.mobile.network.session.ServerSession;
import org.openjst.server.mobile.rpc.RpcHandler;
import org.openjst.server.mobile.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.PermitAll;
import javax.ejb.*;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
@Startup
@Singleton(name = BasicProtocolServiceImpl.NAME)
@TransactionManagement(TransactionManagementType.BEAN)
@PermitAll
public class BasicProtocolServiceImpl implements BasicProtocolService<Actor<Long>, RPCMessageObj> {

    public static final String NAME = "BasicProtocolService";

    private static final Logger LOG = LoggerFactory.getLogger(BasicProtocolService.class);

    @EJB
    private SettingsManager cfg;

    @EJB
    private ClientDAO clientDAO;

    @EJB
    private AccountDAO accountDAO;

    @EJB
    private NetworkService networkService;

    @EJB
    private RpcHandler rpcHandler;

    private Server server;
    private ServerEventsListener serverEventsListener;

    @Override
    public DeliveryResult sendToClient(final Actor<Long> actor, final RPCMessageObj message) {
        final SendFuture future;
        try {
            future = server.rpcForwardToClient(
                    message.getAccountId(), message.getClientId(), message.getFormat(), message.getData()).await();
            if (future.isFail()) {
                return DeliveryResult.as(convertToDeliveryError(future.getResponseStatus()), null);
            }
        } catch (ClientNotConnectedException e) {
            return DeliveryResult.as(MessageDeliveryState.NO_FORWARD_RECIPIENT, e.getMessage());
        }
        return DeliveryResult.ok();
    }

    @Override
    public DeliveryResult sendToServer(final Actor<Long> actor, final RPCMessageObj message) {
        final SendFuture future;
        try {
            future = this.server.rpcForwardToServer(
                    message.getAccountId(), message.getClientId(), message.getFormat(), message.getData()).await();
            if (future.isFail()) {
                return DeliveryResult.as(convertToDeliveryError(future.getResponseStatus()), null);
            }
        } catch (ClientNotConnectedException e) {
            return DeliveryResult.as(MessageDeliveryState.NO_FORWARD_RECIPIENT, e.getMessage());
        }
        return DeliveryResult.ok();
    }

    @PostConstruct
    private void create() {
        try {
            server = new Server(
                    cfg.getString(Settings.APIBasic.HOST),
                    cfg.getInteger(Settings.APIBasic.CLIENTS_PORT),
                    cfg.getInteger(Settings.APIBasic.SERVERS_PORT));
            server.addListener(createListener());
            server.start();
        } catch (Exception e) {
            LOG.error("Start server", e);
        }
    }

    @PreDestroy
    private void destroy() {
        server.stop();
        server.removeListener(serverEventsListener);
    }

    private ServerEventsListener createListener() {
        serverEventsListener = new ServerEventsListener() {
            @Override
            public boolean onAuthenticate(final ServerAuthenticationEvent event) {
                return BasicProtocolServiceImpl.this.onAuthenticate(event);
            }

            @Override
            public void onForwardAuthenticationResponse(final ForwardAuthenticationResponseEvent event) {
                BasicProtocolServiceImpl.this.onForwardAuthenticationResponse(event);
            }

            @Override
            public void onConnect(final ConnectEvent event) {
                BasicProtocolServiceImpl.this.onConnect(event);
            }

            @Override
            public void onDisconnect(final DisconnectEvent event) {
                BasicProtocolServiceImpl.this.onDisconnect(event);
            }

            @Override
            public void onRPC(final RPCEvent event) {
                BasicProtocolServiceImpl.this.onRPC(event);
            }
        };
        return serverEventsListener;
    }

    private void onRPC(final RPCEvent event) {
        if (event.isForward()) {
            onRPCForward(event);
        } else {
            onRPCInvoke(event);
        }
    }

    private void onRPCInvoke(final RPCEvent event) {
        LOG.info("onRPC {}", event);

        rpcHandler.invoke(event.getSession(), event.getFormat(), event.getData());
    }

    private void onRPCForward(final RPCEvent event) {
        LOG.info("onRPC forward {}", event);

        if (event.getSession() instanceof ClientSession) {
            final ClientSession session = (ClientSession) event.getSession();

            networkService.serverRPCForwardTo(
                    session.getAccountPersistenceId(), session.getClientPersistenceId(), event.getFormat(), event.getData());
        } else {
            final ServerSession session = (ServerSession) event.getSession();

            final long clientId = clientDAO.getOrCreateClientId(session.getAccountPersistenceId(), event.getRecipientId());

            networkService.clientRPCForwardTo(
                    session.getAccountPersistenceId(), clientId, event.getFormat(), event.getData());

        }
    }

    private boolean onAuthenticate(final ServerAuthenticationEvent event) {
        LOG.debug("onAuthenticate: " + event);

        final AbstractAuthPacket packet = event.getPacket();
        if (packet instanceof AuthServerRequestPacket) {
            return onAuthenticateServer(event, (AuthServerRequestPacket) packet);
        } else if (packet instanceof AuthClientRequestPacket) {
            return onAuthenticateClient(event, (AuthClientRequestPacket) packet);
        }

        return false;
    }

    private void onConnect(final ConnectEvent event) {
        LOG.info("onConnect {}", event);

        if (event.getSession() instanceof ClientSession) {
            networkService.clientConnected((ClientSession) event.getSession(), ProtocolType.BASIC, event.getRemoteHost());
        } else {
            networkService.serverConnected((ServerSession) event.getSession(), ProtocolType.BASIC, event.getRemoteHost());
        }
    }

    private void onDisconnect(final DisconnectEvent event) {
        LOG.info("onDisconnect {}", event);

        if (event.getSession() instanceof ClientSession) {
            networkService.clientDisconnected((ClientSession) event.getSession(), ProtocolType.BASIC);
        } else {
            networkService.serverDisconnect((ServerSession) event.getSession(), ProtocolType.BASIC);
        }
    }

    private void onForwardAuthenticationResponse(final ForwardAuthenticationResponseEvent event) {
        LOG.info("onForwardAuthenticationResponse {}", event);

        final Set<Parameter> parameters = event.getResponse().getParameters();
        if (parameters == null || parameters.isEmpty()) {
            return;
        }

        final ClientSession session = (ClientSession) event.getSession();

        boolean cache = false;
        for (final Parameter parameter : parameters) {
            if (Parameter.CACHE.equals(parameter.getKey()) && Boolean.TRUE.equals(parameter.getValue())) {
                cache = true;
                break;
            }
        }

        final Client client = clientDAO.synchronizeClient(session.getAccountId(), session.getClientId(), cache,
                event.getRequest().getSecretKey());

        session.setAccountPersistenceId(client.getAccount().getId());
        session.setClientPersistenceId(client.getId());
    }

    private boolean onAuthenticateClient(final ServerAuthenticationEvent event, final AuthClientRequestPacket packet) {
        final String clientId = packet.getClientId();
        final String accountId = packet.getAccountId();

        final ClientAuthenticationObj authData = clientDAO.findCachedSecretKeyOf(accountId, clientId);
        if (authData == null) {
            event.assignSession(new ClientSession(accountId, clientId)).forward();
            return true;
        }

        final SecretKey key = packet.getSecretKey();
        final SecretKey correctPassword = UserUtils.parseSecretKey(authData.getPassword());
        final SecretKey encodedPassword = correctPassword.getType().encode(new String(key.get()), authData.getSalt());
        if (encodedPassword.equals(correctPassword)) {
            event.assignSession(new ClientSession(authData.getAccountId(), authData.getClientId(), accountId, clientId));
            return true;
        }

        return false;
    }

    private boolean onAuthenticateServer(final ServerAuthenticationEvent event, final AuthServerRequestPacket packet) {
        final String secretKey = Base64.encodeToString(packet.getSecretKey().get(), true);
        final AccountAuthenticationObj account = accountDAO.findAccountByApiKey(secretKey);
        if (account == null) {
            return false;
        }
        event.assignSession(new ServerSession(account.getId(), account.getAuthId(), secretKey));
        return true;
    }

    private MessageDeliveryState convertToDeliveryError(final int responseStatus) {
        switch (responseStatus) {
            case ProtocolResponseStatus.AUTH_TIMEOUT:
                return MessageDeliveryState.AUTHENTICATION_TIMEOUT;
            case ProtocolResponseStatus.AUTH_FAIL:
                return MessageDeliveryState.AUTHENTICATION_FAIL;
            case ProtocolResponseStatus.AUTH_UNSUPPORTED:
                return MessageDeliveryState.AUTHENTICATION_UNSUPPORTED;
            case ProtocolResponseStatus.NO_FORWARD_RECIPIENT:
                return MessageDeliveryState.NO_FORWARD_RECIPIENT;
            case ProtocolResponseStatus.NO_RESPONSE:
                return MessageDeliveryState.NO_RESPONSE;
            default:
                return MessageDeliveryState.AUTHENTICATION_INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    public void restart() {
        destroy();
        create();
    }
}
