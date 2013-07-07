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

package org.openjst.server.mobile.server.impl;

import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.encodings.Base64;
import org.openjst.commons.protocols.auth.SecretKey;
import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.protocols.basic.constants.ProtocolResponseStatus;
import org.openjst.protocols.basic.context.SendFuture;
import org.openjst.protocols.basic.events.*;
import org.openjst.protocols.basic.exceptions.ClientNotConnectedException;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.AuthClientRequestPacket;
import org.openjst.protocols.basic.pdu.packets.AuthServerRequestPacket;
import org.openjst.protocols.basic.server.Server;
import org.openjst.protocols.basic.server.ServerEventsListener;
import org.openjst.protocols.basic.session.ClientSession;
import org.openjst.protocols.basic.session.ServerSession;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.services.PreferencesManager;
import org.openjst.server.mobile.Preferences;
import org.openjst.server.mobile.dao.AccountDAO;
import org.openjst.server.mobile.dao.ClientDAO;
import org.openjst.server.mobile.rpc.DeliveryResult;
import org.openjst.server.mobile.server.BasicProtocolService;
import org.openjst.server.mobile.server.Router;
import org.openjst.server.mobile.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * @author Sergey Grachev
 */
@Startup
@Singleton
@LocalBean
public class BasicProtocolServiceImpl implements BasicProtocolService {

    private static final Logger LOG = LoggerFactory.getLogger(BasicProtocolService.class);

    @EJB
    private PreferencesManager cfg;
    @EJB
    private ClientDAO clientDAO;
    @EJB
    private AccountDAO accountDAO;
    @Inject
    private Router router;

    private Server server;
    private ServerEventsListener serverEventsListener;

    @Override
    public DeliveryResult rpcForwardToServer(final String accountId, final String clientId, final RPCMessageFormat format, final byte[] data)
            throws ClientNotConnectedException {

        final SendFuture future = server.rpcForwardToServer(accountId, clientId, format, data).await();
        if (future.isFail()) {
            return DeliveryResult.fail(convertToDeliveryError(future.getResponseStatus()), null);
        }

        return DeliveryResult.ok();
    }

    @Override
    public DeliveryResult rpcForwardToClient(final String accountId, final String clientId, final RPCMessageFormat format, final byte[] data)
            throws ClientNotConnectedException {

        final SendFuture future = server.rpcForwardToClient(accountId, clientId, format, data).await();
        if (future.isFail()) {
            return DeliveryResult.fail(convertToDeliveryError(future.getResponseStatus()), null);
        }

        return DeliveryResult.ok();
    }

    @Override
    public DeliveryResult rpcInvoke(final String accountId, final RPCMessageFormat format, final byte[] data)
            throws ClientNotConnectedException {

        final SendFuture future = server.rpcInvoke(accountId, format, data).await();
        if (future.isFail()) {
            return DeliveryResult.fail(convertToDeliveryError(future.getResponseStatus()), null);
        }

        return DeliveryResult.ok();
    }

    @PostConstruct
    public void create() {
        try {
            server = new Server(
                    cfg.getString(Preferences.APIBasic.HOST),
                    cfg.getInteger(Preferences.APIBasic.CLIENTS_PORT),
                    cfg.getInteger(Preferences.APIBasic.SERVERS_PORT));
            server.addListener(createListener());
            server.start();
        } catch (Exception e) {
            LOG.error("Start server", e);
        }
    }

    private ServerEventsListener createListener() {
        serverEventsListener = new ServerEventsListener() {
            @Override
            public boolean onAuthenticate(final ServerAuthenticationEvent event) {
                LOG.debug("onAuthenticate: " + event);
                final AbstractAuthPacket packet = event.getPacket();
                if (packet instanceof AuthServerRequestPacket) {
                    return onAuthenticateServer(event, (AuthServerRequestPacket) packet);
                } else if (packet instanceof AuthClientRequestPacket) {
                    return onAuthenticateClient(event, (AuthClientRequestPacket) packet);
                }
                return false;
            }

            @Override
            public void onForwardAuthenticationResponse(final ForwardAuthenticationResponseEvent event) {
                LOG.info("onForwardAuthenticationResponse {}", event);
                // TODO save into cache
            }

            @Override
            public void onForwardRPC(final ForwardRPCEvent event) {
                LOG.info("onForwardRPC {}", event);
                if (event.getSession() instanceof ClientSession) {
                    router.rpcForwardToServer(event.getSession().getAccountId(), event.getSession().getClientId(),
                            event.getFormat(), event.getData());
                } else {
                    router.rpcForwardToClient(event.getSession().getAccountId(), event.getSession().getClientId(),
                            event.getFormat(), event.getData());
                }
            }

            @Override
            public void onConnect(final ConnectEvent event) {
                LOG.info("onConnect {}", event);
                if (event.getSession() instanceof ClientSession) {
                    router.activateClientRoute(event.getSession().getAccountId(), event.getSession().getClientId(), ProtocolType.BASIC);
                } else {
                    router.activateServerRoute(event.getSession().getAccountId(), ProtocolType.BASIC);
                }
                // TODO change online status of client
            }

            @Override
            public void onDisconnect(final DisconnectEvent event) {
                LOG.info("onDisconnect {}", event);
                if (event.getSession() instanceof ClientSession) {
                    router.deactivateClientRoute(event.getSession().getAccountId(), event.getSession().getClientId(), ProtocolType.BASIC);
                } else {
                    router.deactivateServerRoute(event.getSession().getAccountId(), ProtocolType.BASIC);
                }
                // TODO change offline status of client
            }

            @Override
            public void onRPC(final RPCEvent event) {
                LOG.info("onRPC {}", event);
                // TODO invoke
            }
        };
        return serverEventsListener;
    }

    private boolean onAuthenticateClient(final ServerAuthenticationEvent event, final AuthClientRequestPacket packet) {
        final String clientId = packet.getClientId();
        final String accountId = packet.getAccountId();

        final Pair<String, byte[]> secretKeyData = clientDAO.findCachedSecretKeyOf(accountId, clientId);
        if (secretKeyData == null) {
            event.assignSession(new ClientSession(accountId, clientId)).forward();
            return true;
        }

        final SecretKey key = packet.getSecretKey();
        final SecretKey correctPassword = UserUtils.parseSecretKey(secretKeyData.first());
        final SecretKey encodedPassword = correctPassword.getType().encode(new String(key.get()), secretKeyData.second());
        if (encodedPassword.equals(correctPassword)) {
            event.assignSession(new ClientSession(accountId, clientId));
            return true;
        }

        return false;
    }

    private boolean onAuthenticateServer(final ServerAuthenticationEvent event, final AuthServerRequestPacket packet) {
        final String secretKey = Base64.encodeToString(((AuthServerRequestPacket) packet).getSecretKey().get(), true);
        final String accountId = accountDAO.findAccountIdByApiKey(secretKey);
        if (accountId == null) {
            return false;
        }
        event.assignSession(new ServerSession(accountId, secretKey));
        return true;
    }

    @PreDestroy
    public void destroy() {
        server.removeListener(serverEventsListener);
        server.stop();
    }

    private DeliveryResult.Error convertToDeliveryError(final int responseStatus) {
        switch (responseStatus) {
            case ProtocolResponseStatus.AUTH_TIMEOUT:
                return DeliveryResult.Error.AUTHENTICATION_TIMEOUT;
            case ProtocolResponseStatus.AUTH_FAIL:
                return DeliveryResult.Error.AUTHENTICATION_FAIL;
            case ProtocolResponseStatus.AUTH_UNSUPPORTED:
                return DeliveryResult.Error.AUTHENTICATION_UNSUPPORTED;
            case ProtocolResponseStatus.NO_FORWARD_SERVER:
                return DeliveryResult.Error.NO_FORWARD_SERVER;
            case ProtocolResponseStatus.NO_RESPONSE:
                return DeliveryResult.Error.NO_RESPONSE;
            default:
                return DeliveryResult.Error.AUTHENTICATION_INTERNAL_SERVER_ERROR;
        }
    }
}
