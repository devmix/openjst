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


import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
final class XmlRpcUtils {

    public static final String TAG_METHOD_RESPONSE = "methodResponse";
    public static final String TAG_METHOD_CALL = "methodCall";
    public static final String TAG_METHOD_NAME = "methodName";
    public static final String TAG_PARAMS = "params";
    public static final String TAG_PARAM = "param";
    public static final String TAG_VALUE = "value";
    public static final String TAG_ARRAY = "array";
    public static final String TAG_BASE64 = "base64";
    public static final String TAG_BOOLEAN = "boolean";
    public static final String TAG_DATETIME_ISO8601 = "dateTime.iso8601";
    public static final String TAG_DOUBLE = "double";
    public static final String TAG_I4 = "i4";
    public static final String TAG_INT = "int";
    public static final String TAG_STRING = "string";
    public static final String TAG_NIL = "nil";
    public static final String TAG_STRUCT = "struct";
    public static final String TAG_DATA = "data";
    public static final String TAG_MEMBER = "member";
    public static final String TAG_NAME = "name";
    public static final String TAG_FAULT = "fault";

    public static final String VALUE_FAULT_CODE = "faultCode";
    public static final String VALUE_FAULT_STRING = "faultString";
    public static final String VALUE_FAULT_CUSTOM = "faultCustom";

    public static final Element ELEMENT_ROOT = new Element(State.ROOT);
    public static final Element ELEMENT_METHOD_RESPONSE = new Element(State.METHOD_RESPONSE);
    public static final Element ELEMENT_METHOD_CALL = new Element(State.METHOD_CALL);
    public static final Element ELEMENT_METHOD_NAME = new Element(State.METHOD_NAME);
    public static final Element ELEMENT_PARAMS = new Element(State.PARAMS);

    private XmlRpcUtils() {
    }

    public static StringBuilder readString(final Reader reader) throws IOException {
        final char[] buffer = new char[128];
        final StringBuilder sb = new StringBuilder();
        int count;
        while ((count = reader.read(buffer)) > 0) {
            sb.append(buffer, 0, count);
        }
        return sb;
    }

    public static enum State {
        ROOT() {
            @Override
            public Element newStackElement() {
                return ELEMENT_ROOT;
            }
        },
        METHOD_RESPONSE() {
            @Override
            public Element newStackElement() {
                return ELEMENT_METHOD_RESPONSE;
            }
        },
        METHOD_CALL() {
            @Override
            public Element newStackElement() {
                return ELEMENT_METHOD_CALL;
            }
        },
        METHOD_NAME() {
            @Override
            public Element newStackElement() {
                return ELEMENT_METHOD_NAME;
            }
        },
        PARAMS() {
            @Override
            public Element newStackElement() {
                return ELEMENT_PARAMS;
            }
        },
        PARAM,
        VALUE,
        ARRAY,
        DATA,
        STRUCT,
        MEMBER,
        NAME,
        BASE64,
        BOOLEAN,
        DATETIME,
        DOUBLE,
        INT,
        STRING,
        NIL, FAULT;

        public Element newStackElement() {
            return new Element(this);
        }
    }

    public static final class Element {
        public final State parentState;
        public String name;
        public Object value;

        public Element(final State parentState) {
            this.parentState = parentState;
        }

        public void addToArray(final Object value) {
            if (this.value == null) {
                this.value = new ArrayList<Object>(1);
            }
            @SuppressWarnings("unchecked")
            final List<Object> asList = (List<Object>) this.value;
            asList.add(value);
        }

        public void addObjectField(final String name, final Object value) {
            if (this.value == null) {
                this.value = new HashMap<String, Object>(1);
            }
            @SuppressWarnings("unchecked")
            final Map<String, Object> asMap = (Map<String, Object>) this.value;
            asMap.put(name, value);
        }
    }

    public static enum DataType {
        REQUEST, RESPONSE
    }
}
