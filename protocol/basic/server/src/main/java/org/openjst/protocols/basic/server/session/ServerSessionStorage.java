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

package org.openjst.protocols.basic.server.session;

import org.jboss.netty.channel.Channel;
import org.openjst.protocols.basic.sessions.Session;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public class ServerSessionStorage {

    private final Map<Integer, SessionInfo> channelIdToSession = new LinkedHashMap<Integer, SessionInfo>();
    private final Map<String, SessionInfo> sessionKeyToSession = new LinkedHashMap<String, SessionInfo>();

    public void registerChannel(final Channel channel, final ServerSession session) {
        synchronized (channelIdToSession) {
            final SessionInfo sessionInfo = new SessionInfo(session, channel, SessionInfo.createSessionKey(session));
            channelIdToSession.put(channel.getId(), sessionInfo);
            sessionKeyToSession.put(sessionInfo.sessionKey, sessionInfo);
        }
    }

    public void unregisterChannel(final Channel channel) {
        synchronized (channelIdToSession) {
            final SessionInfo sessionInfo = channelIdToSession.remove(channel.getId());
            if (sessionInfo != null) {
                sessionKeyToSession.remove(sessionInfo.sessionKey);
            }
        }
    }

    public Channel getChannel(final String accountId, final String userId) {
        synchronized (channelIdToSession) {
            final SessionInfo sessionInfo = sessionKeyToSession.get(SessionInfo.createSessionKey(accountId, userId));
            return sessionInfo != null ? sessionInfo.channel : null;
        }
    }

    public Session getSessionByChannel(final Channel channel) {
        return getSessionByChannel(channel.getId());
    }

    public ServerSession getSessionByChannel(final int channelId) {
        synchronized (channelIdToSession) {
            final SessionInfo sessionInfo = channelIdToSession.get(channelId);
            return sessionInfo != null ? sessionInfo.session : null;
        }
    }

    public void reset() {
        synchronized (channelIdToSession) {
            channelIdToSession.clear();
            sessionKeyToSession.clear();
        }
    }

    private static final class SessionInfo {
        public final ServerSession session;
        public final Channel channel;
        public final String sessionKey;

        private SessionInfo(final ServerSession session, final Channel channel, final String sessionKey) {
            this.session = session;
            this.channel = channel;
            this.sessionKey = sessionKey;
        }

        public static String createSessionKey(final ServerSession session) {
            return session.getAccountId() + "-" + session.getClientId();
        }

        public static String createSessionKey(final String accountId, final String clientId) {
            return accountId + "-" + clientId;
        }
    }
}
