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

package org.openjst.protocols.basic.server;

import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.openjst.protocols.basic.events.Event;
import org.openjst.protocols.basic.events.ForwardAuthenticationResponseEvent;
import org.openjst.protocols.basic.events.ProtocolEventsProducer;
import org.openjst.protocols.basic.events.ServerAuthenticationEvent;

/**
 * @author Sergey Grachev
 */
public final class ServerEventsProducer extends ProtocolEventsProducer<ServerEventsListener> {

    private static final InternalLogger LOG =
            InternalLoggerFactory.getInstance(ServerEventsProducer.class.getName());

    public boolean onAuthenticate(final ServerAuthenticationEvent event) {
        for (final Object listener : getListeners()) {
            try {
                if (((ServerEventsListener) listener).onAuthenticate(event)) {
                    return true;
                }
            } catch (Exception e) {
                LOG.error("onAuthenticate", e);
            }
        }
        return false;
    }

    @Override
    protected void processEvent(final Event event) {
        if (event instanceof ForwardAuthenticationResponseEvent) {
            fireForwardAuthenticationResponseEvent((ForwardAuthenticationResponseEvent) event);
        } else {
            super.processEvent(event);
        }
    }

    private void fireForwardAuthenticationResponseEvent(final ForwardAuthenticationResponseEvent event) {
        for (final Object listener : getListeners()) {
            try {
                ((ServerEventsListener) listener).onForwardAuthenticationResponse(event);
            } catch (Exception e) {
                LOG.error("onForwardAuthenticationResponse", e);
            }
        }
    }
}
