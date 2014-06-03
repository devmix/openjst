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

package org.openjst.commons.rpc;

import org.openjst.commons.rpc.exceptions.RPCException;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class RPCContextTest {

    @Test(groups = "unit", dataProviderClass = TestRPCDataProvider.class, dataProvider = "rpcRequestParsed")
    public void testInvokeRequest(final RPCRequest request) throws RPCException {
        final RPCContext context = RPC.newInstance().registerHandler("consumer", new Consumer());
        Object response = context
                .invoke("consumer", "testInvokeRequestPrimitives", request.getParameters());

        response = context
                .invoke("consumer", "testInvokeRequest", request.getParameters());

        System.out.println(response);

    }

    @SuppressWarnings("UnusedDeclaration")
    public static final class Consumer {

        public Object testInvokeRequestPrimitives(
                final byte byteMin, final byte byteMax,
                final short shortMin, final short shortMax,
                final int intMin, final int intMax,
                final long longMin, final long longMax,
                final float floatMin, final float floatMax,
                final double doubleMin, final double doubleMax,
                final boolean boolFalse, final boolean boolTrue,
                final String str,
                final Date date, final Date dateCalendar,
                final Object nul,
                final byte[] bytes,
                final Object[] objects,
                final TestRPCDataProvider.TestFormatsRPCObject rpcObject,
                final Map map) {


            return "response string";
        }

        public Object testInvokeRequest(
                final Byte byteMin, final Byte byteMax,
                final Short shortMin, final Short shortMax,
                final Integer intMin, final Integer intMax,
                final Long longMin, final Long longMax,
                final Float floatMin, final Float floatMax,
                final Double doubleMin, final Double doubleMax,
                final Boolean boolFalse, final Boolean boolTrue,
                final String str,
                final Date date, final Date dateCalendar,
                final Object nul,
                final byte[] bytes,
                final Object[] objects,
                final TestRPCDataProvider.TestFormatsRPCObject rpcObject,
                final Map map) {


            return "response string";
        }
    }
}
