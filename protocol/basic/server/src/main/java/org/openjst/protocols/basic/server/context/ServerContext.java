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

package org.openjst.protocols.basic.server.context;

import org.jboss.netty.channel.Channel;
import org.openjst.protocols.basic.context.AbstractContext;
import org.openjst.protocols.basic.session.Session;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class ServerContext extends AbstractContext {

    private final Map<Integer, SessionContext> channelIdToSession = new LinkedHashMap<Integer, SessionContext>();
    private final Map<SessionKey, SessionContext> sessionKeyToSession = new LinkedHashMap<SessionKey, SessionContext>();
    private final Map<ForwardAuthenticationCallbackKey, WeakReference<ForwardAuthenticationCallback>> forwardAuthentication
            = new HashMap<ForwardAuthenticationCallbackKey, WeakReference<ForwardAuthenticationCallback>>();

    public void registerClientChannel(final Channel channel, final Session session) {
        registerChannel(channel, session, SessionKey.Type.CLIENT);
    }

    public void registerServerChannel(final Channel channel, final Session session) {
        registerChannel(channel, session, SessionKey.Type.SERVER);
    }

    public void registerChannel(final Channel channel, final Session session, final SessionKey.Type type) {
        synchronized (channelIdToSession) {
            final SessionContext sessionContext = new SessionContext(session, channel, new SessionKey(session, type));
            channelIdToSession.put(channel.getId(), sessionContext);
            sessionKeyToSession.put(sessionContext.sessionKey, sessionContext);
        }
    }

    public void unregisterChannel(final Channel channel) {
        synchronized (channelIdToSession) {
            final SessionContext sessionContext = channelIdToSession.remove(channel.getId());
            if (sessionContext != null) {
                sessionKeyToSession.remove(sessionContext.sessionKey);
            }
        }
    }

    public Channel getClientChannel(final String accountId, final String clientId) {
        synchronized (channelIdToSession) {
            final SessionContext sessionContext = sessionKeyToSession.get(new SessionKey(accountId, clientId));
            return sessionContext != null ? sessionContext.channel : null;
        }
    }

    public Channel getServerChannel(final String accountId) {
        synchronized (channelIdToSession) {
            final SessionContext sessionContext = sessionKeyToSession.get(new SessionKey(accountId));
            return sessionContext != null ? sessionContext.channel : null;
        }
    }

    public Session getSessionByChannel(final Channel channel) {
        return getSessionByChannel(channel.getId());
    }

    public Session getSessionByChannel(final int channelId) {
        synchronized (channelIdToSession) {
            final SessionContext sessionContext = channelIdToSession.get(channelId);
            return sessionContext != null ? sessionContext.session : null;
        }
    }

    public void reset() {
        super.reset();
        synchronized (channelIdToSession) {
            channelIdToSession.clear();
            sessionKeyToSession.clear();
        }
    }

    public void createForwardAuthentication(final int channelId, final long newPacketId, final ForwardAuthenticationCallback callback) {
        synchronized (forwardAuthentication) {
            forwardAuthentication.put(new ForwardAuthenticationCallbackKey(channelId, newPacketId),
                    new WeakReference<ForwardAuthenticationCallback>(callback));
        }
    }

    public ForwardAuthenticationCallback removeForwardAuthentication(final int channelId, final long packetId) {
        synchronized (forwardAuthentication) {
            final WeakReference<ForwardAuthenticationCallback> reference
                    = forwardAuthentication.remove(new ForwardAuthenticationCallbackKey(channelId, packetId));
            return reference == null ? null : reference.get();
        }
    }
}
