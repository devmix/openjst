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

package org.openjst.server.mobile.services.impl;

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.dto.tuples.Triple;
import org.openjst.commons.protocols.auth.SecretKey;
import org.openjst.commons.protocols.auth.SecretKeys;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;
import org.openjst.protocols.basic.pdu.packets.AuthRequestBasicPacket;
import org.openjst.protocols.basic.pdu.packets.PresenceStatePacket;
import org.openjst.protocols.basic.pdu.packets.RPCPacket;
import org.openjst.protocols.basic.server.Server;
import org.openjst.protocols.basic.server.ServerEventsListener;
import org.openjst.protocols.basic.server.session.ServerSession;
import org.openjst.protocols.basic.sessions.Session;
import org.openjst.server.commons.preferences.PreferenceChangeEvent;
import org.openjst.server.commons.services.PreferencesManager;
import org.openjst.server.mobile.Preferences;
import org.openjst.server.mobile.dao.UserDAO;
import org.openjst.server.mobile.services.MobileClientsServerService;
import org.openjst.server.mobile.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;

/**
 * @author Sergey Grachev
 */
@Singleton
@Startup
@LocalBean
public class MobileClientsServerServiceImpl implements MobileClientsServerService {

    private static final Logger LOG = LoggerFactory.getLogger(MobileClientsServerService.class);

    @EJB
    private PreferencesManager cfg;

    @EJB
    private UserDAO userDAO;

    private Server server;

    @PostConstruct
    public void create() {
        try {
            server = new Server(cfg.getString(Preferences.APIBasic.HOST), cfg.getInteger(Preferences.APIBasic.PORT));
            server.addListener(new ServerEventsListener() {
                @Override
                public void onPresenceState(final PresenceStatePacket packet) {
                    LOG.info("onPresenceState {}", packet);
                }

                @Nullable
                @Override
                public ServerSession onTryAuthenticate(final AuthRequestBasicPacket packet) {
                    final SecretKey key = packet.getSecretKey();
                    if (SecretKeys.PLAIN.equals(key.getType())) {
                        final Triple<Long, String, byte[]> secretKeyData = userDAO.findSecretKeyOf(packet.getAccountId(), packet.getClientId());
                        if (secretKeyData != null) {
                            final SecretKey correctPassword = UserUtils.parseSecretKey(secretKeyData.second());
                            final SecretKey encodedPassword = correctPassword.getType().encode(new String(key.get()), secretKeyData.third());
                            if (encodedPassword.equals(correctPassword)) {
                                return new ServerSession(packet.getAccountId(), packet.getClientId());
                            }
                        }
                    }
                    return null;
                }

                @Override
                public void onConnect(final Session session) {
                    LOG.info("onConnect {}", session);
                }

                @Override
                public void onAuthorizationFail(final int errorCode, final AbstractAuthPacket authRequest) {
                    LOG.info("onAuthorizationFail {} {}", errorCode, authRequest);
                }

                @Override
                public void onDisconnect(final Session session) {
                    LOG.info("onDisconnect {}", session);
                }

                @Override
                public void onRPC(final Session session, final RPCPacket packet) {
                    LOG.info("onRPC {} {}", session, packet);
                }
            });
            server.start();
        } catch (Exception e) {
            LOG.error("Start server", e);
        }
    }

    @PreDestroy
    public void destroy() {
        server.stop();
    }

    private void afterServerPreferenceChange(@Observes(during = TransactionPhase.AFTER_SUCCESS) final PreferenceChangeEvent event) {
        LOG.info("Event {}", event);
    }
}
