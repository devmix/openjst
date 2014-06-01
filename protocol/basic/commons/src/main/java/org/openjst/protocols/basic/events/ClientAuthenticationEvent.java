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

package org.openjst.protocols.basic.events;

import org.openjst.protocols.basic.pdu.beans.Parameter;
import org.openjst.protocols.basic.pdu.packets.AbstractAuthPacket;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public final class ClientAuthenticationEvent implements Event {

    private final AbstractAuthPacket packet;
    private final Set<Parameter> parameters = new HashSet<Parameter>();
    private short status;

    public ClientAuthenticationEvent(final AbstractAuthPacket packet) {
        this.packet = packet;
    }

    public AbstractAuthPacket getPacket() {
        return packet;
    }

    public ClientAuthenticationEvent addResponseParameters(final Set<Parameter> parameters) {
        parameters.addAll(parameters);
        return this;
    }

    public <K, V> void addResponseParameter(final K key, final V value) {
        parameters.add(Parameter.newParameter(key, value));
    }

    public ClientAuthenticationEvent status(final short status) {
        this.status = status;
        return this;
    }

    public Set<Parameter> getParameters() {
        return parameters;
    }

    public short getStatus() {
        return status;
    }

    public ClientAuthenticationEvent cacheCredentials(final boolean allow, @Nullable final Date expire) {
        parameters.add(Parameter.newParameter(Parameter.CACHE, allow));
        if (allow && expire != null) {
            parameters.add(Parameter.newParameter(Parameter.CACHE_EXPIRE, expire.getTime()));
        }
        return this;
    }

    @Override
    public String toString() {
        return "ClientAuthenticationEvent{" +
                "packet=" + packet +
                ", parameters=" + parameters +
                ", status=" + status +
                '}';
    }
}
