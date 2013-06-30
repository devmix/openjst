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

package org.openjst.commons.rpc.formats.binary;

import org.openjst.commons.io.buffer.DataBufferException;
import org.openjst.commons.rpc.RPCFormat;
import org.openjst.commons.rpc.RPCMessage;
import org.openjst.commons.rpc.RPCRequest;
import org.openjst.commons.rpc.RPCResponse;
import org.openjst.commons.rpc.exceptions.RPCException;

/**
 * @author Sergey Grachev
 */
public class BinaryRpc implements RPCFormat {

    private final boolean useExtensions;

    public BinaryRpc(final boolean useExtensions) {
        this.useExtensions = useExtensions;
    }

    public <T extends RPCMessage> T read(final byte[] data) throws RPCException {
        try {
            return new BinaryRpcReader(data).read();
        } catch (DataBufferException e) {
            throw RPCException.newNested(e);
        }
    }

    public byte[] write(final RPCRequest request) throws RPCException {
        try {
            return new BinaryRpcWriter().write(request);
        } catch (DataBufferException e) {
            throw RPCException.newNested(e);
        }
    }

    public byte[] write(final RPCResponse response) throws RPCException {
        try {
            return new BinaryRpcWriter().write(response);
        } catch (DataBufferException e) {
            throw RPCException.newNested(e);
        }
    }
}
