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

/**
 * @author Sergey Grachev
 */
final class ForwardAuthenticationCallbackKey {

    private final int channelId;
    private final long packetId;

    public ForwardAuthenticationCallbackKey(final int channelId, final long packetId) {
        this.channelId = channelId;
        this.packetId = packetId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ForwardAuthenticationCallbackKey that = (ForwardAuthenticationCallbackKey) o;
        return channelId == that.channelId && packetId == that.packetId;
    }

    @Override
    public int hashCode() {
        int result = channelId;
        result = 31 * result + (int) (packetId ^ (packetId >>> 32));
        return result;
    }
}
