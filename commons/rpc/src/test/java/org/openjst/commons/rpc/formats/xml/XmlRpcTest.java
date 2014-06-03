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

package org.openjst.commons.rpc.formats.xml;

import org.apache.commons.io.IOUtils;
import org.openjst.commons.rpc.*;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class XmlRpcTest {

    @Test(groups = "unit")
    public void testRequestRead() throws IOException, RPCException {
        final String data = IOUtils.toString(ClassLoader.getSystemResourceAsStream("rpc/example-rpc-call-request.xml"));
        final RPCFormat format = RPCMessageFormat.XML.newFormatter(true);
        final RPCRequest request = format.read(data.getBytes("UTF-8"));

        System.out.println(request);
    }

    @Test(groups = "unit")
    public void testResponseRead() throws IOException, RPCException {
        final String data = IOUtils.toString(ClassLoader.getSystemResourceAsStream("rpc/example-rpc-call-response.xml"));
        final RPCFormat parser = RPCMessageFormat.XML.newFormatter(true);
        final RPCResponse response = parser.read(data.getBytes(Charset.forName("UTF-8")));

        assertThat(response.isError()).isFalse();

        System.out.println(response);
    }

    @Test(groups = "unit")
    public void testResponseFaultRead() throws IOException, RPCException {
        final String data = IOUtils.toString(ClassLoader.getSystemResourceAsStream("rpc/example-rpc-call-response-fault.xml"));
        final RPCFormat parser = RPCMessageFormat.XML.newFormatter(true);
        final RPCResponse response = parser.read(data.getBytes(Charset.forName("UTF-8")));

        assertThat(response.isError()).isTrue();

        System.out.println(response);
    }

    @Test(groups = "unit", dataProviderClass = TestRPCDataProvider.class, dataProvider = "rpcRequest")
    public void testRequestWrite(final RPCRequest request) throws RPCException, IOException {
        final RPCFormat format = RPCMessageFormat.XML.newFormatter(true);

        final byte[] result = format.write(request);

        System.out.println(result.length);
        System.out.println(new String(result));
    }

    @Test(groups = "unit", dataProviderClass = TestRPCDataProvider.class, dataProvider = "rpcResponse")
    public void testResponseWrite(final RPCResponse response) throws RPCException {
        final RPCFormat data = RPCMessageFormat.XML.newFormatter(true);

        final byte[] result = data.write(response);

        System.out.println(result.length);
        System.out.println(new String(result));
    }

    @Test(groups = "unit", dataProviderClass = TestRPCDataProvider.class, dataProvider = "rpcResponseError")
    public void testResponseErrorWrite(final RPCResponse response) throws RPCException {
        final RPCFormat data = RPCMessageFormat.XML.newFormatter(true);

        final byte[] result = data.write(response);

        System.out.println(result.length);
        System.out.println(new String(result));
    }
}
