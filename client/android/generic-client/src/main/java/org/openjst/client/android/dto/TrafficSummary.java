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

package org.openjst.client.android.dto;

/**
 * @author Sergey Grachev
 */
public final class TrafficSummary {

    private long rpcIn;
    private long rpcOut;

    public void plusRPCIn(final long bytes) {
        rpcIn += bytes;
    }

    public void plusRPCOut(final long bytes) {
        rpcOut += bytes;
    }

    public long getRPCIn() {
        return rpcIn;
    }

    public long getRPCOut() {
        return rpcOut;
    }

    public long getTotal() {
        return rpcIn + rpcOut;
    }
}
