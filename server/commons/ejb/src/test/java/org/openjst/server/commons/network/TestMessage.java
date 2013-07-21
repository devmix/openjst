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

package org.openjst.server.commons.network;

import org.openjst.commons.rpc.RPCMessageFormat;

/**
 * @author Sergey Grachev
 */
public final class TestMessage implements Message<Long> {

    private final Long id;
    private final RPCMessageFormat format;
    private final byte[] data;

    public TestMessage(final Long id, final byte[] data, final RPCMessageFormat format) {
        this.id = id;
        this.data = data;
        this.format = format;
    }

    public TestMessage(final Long id) {
        this(id, null, null);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TestMessage{" +
                "id=" + id +
                ", format=" + format +
                ", data=" + data +
                '}';
    }
}
