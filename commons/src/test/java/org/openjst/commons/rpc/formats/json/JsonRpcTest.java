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

package org.openjst.commons.rpc.formats.json;

import org.openjst.commons.rpc.*;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.testng.annotations.Test;

/**
 * @author Sergey Grachev
 */
public final class JsonRpcTest {

    @Test(groups = "unit", dataProviderClass = TestRPCDataProvider.class, dataProvider = "rpcRequest")
    public void testRequestWrite(final RPCRequest request) throws RPCException {
        final RPCFormat data = RPCMessageFormat.JSON.newFormatter(true);

        final byte[] result = data.write(request);

        System.out.println(new String(result));
        System.out.println("Size: " + result.length);
    }

    @Test(groups = "unit", dataProviderClass = TestRPCDataProvider.class, dataProvider = "rpcResponse")
    public void testResponseWrite(final RPCResponse response) throws RPCException {
        final RPCFormat data = RPCMessageFormat.JSON.newFormatter(true);

        final byte[] result = data.write(response);

        System.out.println(new String(result));
        System.out.println("Size: " + result.length);
    }

    @Test(groups = "unit", dataProviderClass = TestRPCDataProvider.class, dataProvider = "rpcResponseError")
    public void testResponseErrorWrite(final RPCResponse response) throws RPCException {
        final RPCFormat data = RPCMessageFormat.JSON.newFormatter(true);

        final byte[] result = data.write(response);

        System.out.println(new String(result));
        System.out.println("Size: " + result.length);
    }
}
