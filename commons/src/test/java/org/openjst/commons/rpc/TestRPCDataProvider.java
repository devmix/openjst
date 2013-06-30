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

import org.openjst.commons.rpc.objects.RPCObjectsFactory;
import org.openjst.commons.utils.DateTimeUtils;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import java.text.ParseException;
import java.util.*;

/**
 * @author Sergey Grachev
 */
public final class TestRPCDataProvider {

    private TestRPCDataProvider() {
    }

    @DataProvider
    public static Object[][] rpcRequestParsed(final ITestContext context) throws ParseException {
        final RPCParameters parameters = makeParsedParameters();

        return new Object[][]{new Object[]{
                RPCObjectsFactory.newRequest(null, "object", "method", parameters)
        }};
    }

    @DataProvider
    public static Object[][] rpcRequest(final ITestContext context) {
        final RPCParameters parameters = makeParameters();

        return new Object[][]{new Object[]{
                RPCObjectsFactory.newRequest(null, "object", "method", parameters)
        }};
    }

    @DataProvider
    public static Object[][] rpcResponse(final ITestContext context) {
        final RPCParameters parameters = makeParameters();
        return new Object[][]{new Object[]{
                RPCObjectsFactory.newResponse(null, parameters)
        }};
    }

    @DataProvider
    public static Object[][] rpcResponseError(final ITestContext context) {
        final RPCParameters parameters = makeParameters();
        return new Object[][]{new Object[]{
                RPCObjectsFactory.newResponse(null, 1, "error ошибка", parameters.getValues())
        }};
    }

    private static RPCParameters makeParsedParameters() throws ParseException {
        final Date date = DateTimeUtils.parseXmlRpcIso8601("20121225T16:43:12");

        final byte[] bytes = new byte[]{1, 2, Byte.MIN_VALUE, Byte.MAX_VALUE};

        final Object[] objects = new Object[]{
                (byte) 1, (short) 2, 3, 4L, 5.5F, 6.6,
                true,
                "test тест",
                date, date,
                null,
                bytes
        };

        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("byte", (byte) 1);
        map.put("short", (short) 2);
        map.put("int", 3);
        map.put("long", 4L);
        map.put("float", 5.5F);
        map.put("double", 6.6);
        map.put("boolean", true);
        map.put("string", "test тест");
        map.put("date", date);
        map.put("calendar", date);
        map.put("null", null);
        map.put("objects", objects);
        map.put("bytes", bytes);

        final HashMap<String, Object> rpcObject = new HashMap<String, Object>();
        rpcObject.put("fByte", (byte) 1);
        rpcObject.put("fShort", (short) 2);
        rpcObject.put("fInt", 3);
        rpcObject.put("fLong", 4L);
        rpcObject.put("fFloat", 5.5F);
        rpcObject.put("fDouble", 6.6);
        rpcObject.put("fBoolean", true);
        rpcObject.put("fString", "test тест");
        rpcObject.put("fDate", date);
        rpcObject.put("fNull", null);
        rpcObject.put("fObjects", objects);
        rpcObject.put("fBytes", bytes);
        rpcObject.put("fMap", map);

        return RPCObjectsFactory.newParameters()
                .add(Byte.MIN_VALUE).add(Byte.MAX_VALUE)
                .add(Short.MIN_VALUE).add(Short.MAX_VALUE)
                .add(Integer.MIN_VALUE).add(Integer.MAX_VALUE)
                .add(Long.MIN_VALUE).add(Long.MAX_VALUE)
                .add(Float.MIN_VALUE).add(Float.MAX_VALUE)
                .add(Double.MIN_VALUE).add(Double.MAX_VALUE)
                .add(false).add(true)
                .add("test тест")
                .add(date).add(date)
                .add(null)
                .add(bytes)
                .add(objects)
                .add(rpcObject)
                .add(map);
    }

    private static RPCParameters makeParameters() {
        final byte[] bytes = new byte[]{1, 2, Byte.MIN_VALUE, Byte.MAX_VALUE};

        final Object[] objects = new Object[]{
                (byte) 1, (short) 2, 3, 4L, 5.5F, 6.6,
                true,
                "test тест",
                new Date(), Calendar.getInstance(),
                null,
                bytes
        };

        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("byte", (byte) 1);
        map.put("short", (short) 2);
        map.put("int", 3);
        map.put("long", 4L);
        map.put("float", 5.5F);
        map.put("double", 6.6);
        map.put("boolean", true);
        map.put("string", "test тест");
        map.put("date", new Date());
        map.put("calendar", Calendar.getInstance());
        map.put("null", null);
        map.put("objects", objects);
        map.put("bytes", bytes);

        final List list = Arrays.asList(objects);

        final TestFormatsRPCObject rpcObject = new TestFormatsRPCObject(
                (byte) 1, (short) 2, 3, 4L, 5.5F, 6.6,
                true,
                "test тест",
                new Date(),
                null,
                objects,
                bytes,
                map
        );

        return RPCObjectsFactory.newParameters()
                .add(Byte.MIN_VALUE).add(Byte.MAX_VALUE)
                .add(Short.MIN_VALUE).add(Short.MAX_VALUE)
                .add(Integer.MIN_VALUE).add(Integer.MAX_VALUE)
                .add(Long.MIN_VALUE).add(Long.MAX_VALUE)
                .add(Float.MIN_VALUE).add(Float.MAX_VALUE)
                .add(Double.MIN_VALUE).add(Double.MAX_VALUE)
                .add(false).add(true)
                .add("test тест")
                .add(new Date()).add(Calendar.getInstance())
                .add(null)
                .add(bytes)
                .add(objects)
                .add(rpcObject)
                .add(list)
                .add(map);
    }

    @SuppressWarnings("UnusedDeclaration")
    public static final class TestFormatsRPCObject {

        private transient final String transientField = "transientField";
        private final byte fByte;
        private final short fShort;
        private final int fInt;
        private final long fLong;
        private final float fFloat;
        private final double fDouble;
        private final boolean fBoolean;
        private final String fString;
        private final Date fDate;
        private final Object fNull;
        private final Object[] fObjects;
        private final byte[] fBytes;
        private final HashMap<String, Object> fMap;

        public TestFormatsRPCObject() {
            this.fByte = 0;
            this.fShort = 0;
            this.fInt = 0;
            this.fLong = 0;
            this.fFloat = 0;
            this.fDouble = 0;
            this.fBoolean = false;
            this.fString = null;
            this.fDate = null;
            this.fNull = null;
            this.fObjects = null;
            this.fBytes = null;
            this.fMap = null;
        }

        public TestFormatsRPCObject(final byte fByte, final short fShort, final int fInt, final long fLong,
                                    final float fFloat, final double fDouble,
                                    final boolean fBoolean, final String fString, final Date fDate,
                                    final Object fNull, final Object[] fObjects, final byte[] fBytes,
                                    final HashMap<String, Object> fMap) {

            this.fByte = fByte;
            this.fShort = fShort;
            this.fInt = fInt;
            this.fLong = fLong;
            this.fFloat = fFloat;
            this.fDouble = fDouble;
            this.fBoolean = fBoolean;
            this.fString = fString;
            this.fDate = fDate;
            this.fNull = fNull;
            this.fObjects = fObjects;
            this.fBytes = fBytes;
            this.fMap = fMap;
        }

        public String getTransientField() {
            return transientField;
        }

        public byte getByte() {
            return fByte;
        }

        public short getShort() {
            return fShort;
        }

        public int getInt() {
            return fInt;
        }

        public long getLong() {
            return fLong;
        }

        public float getFloat() {
            return fFloat;
        }

        public double getDouble() {
            return fDouble;
        }

        public boolean isfBoolean() {
            return fBoolean;
        }

        public String getString() {
            return fString;
        }

        public Date getDate() {
            return fDate;
        }

        public Object getNull() {
            return fNull;
        }

        public Object[] getObjects() {
            return fObjects;
        }

        public byte[] getBytes() {
            return fBytes;
        }

        public HashMap<String, Object> getMap() {
            return fMap;
        }

        @Override
        public String toString() {
            return "TestFormatsRPCObject{" +
                    "transientField='" + transientField + '\'' +
                    ", fByte=" + fByte +
                    ", fShort=" + fShort +
                    ", fInt=" + fInt +
                    ", fLong=" + fLong +
                    ", fFloat=" + fFloat +
                    ", fDouble=" + fDouble +
                    ", fBoolean=" + fBoolean +
                    ", fString='" + fString + '\'' +
                    ", fDate=" + fDate +
                    ", fNull=" + fNull +
                    ", fObjects=" + (fObjects == null ? null : Arrays.asList(fObjects)) +
                    ", fBytes=" + Arrays.toString(fBytes) +
                    ", fMap=" + fMap +
                    '}';
        }
    }
}
