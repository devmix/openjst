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

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLWriter;
import org.openjst.commons.encodings.Base64;
import org.openjst.commons.rpc.RPCParameters;
import org.openjst.commons.rpc.RPCRequest;
import org.openjst.commons.rpc.RPCResponse;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.commons.utils.DateTimeUtils;
import org.openjst.commons.utils.RPCUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
final class XmlRpcWriter {

    private final ByteArrayOutputStream outputStream;
    private final XMLWriter writer;
    private final boolean useExtensions;

    public XmlRpcWriter(final boolean useExtensions) {
        this.useExtensions = useExtensions;
        outputStream = new ByteArrayOutputStream();
        writer = new XMLWriter(outputStream);
    }

    public byte[] write(final RPCRequest request) throws IOException, RPCException {
        final IXMLElement root = new XMLElement(XmlRpcUtils.TAG_METHOD_CALL);

        createPCData(root, XmlRpcUtils.TAG_METHOD_NAME, RPCUtils.makeMethodName(request.getObject(), request.getMethod()));
        createParams(createChild(root, XmlRpcUtils.TAG_PARAMS), request.getParameters());

        writer.write(root, false, 0, true);

        return outputStream.toByteArray();
    }

    public byte[] write(final RPCResponse response) throws IOException, RPCException {
        final IXMLElement root = new XMLElement(XmlRpcUtils.TAG_METHOD_RESPONSE);

        if (response.isError()) {
            final IXMLElement structEl = createChild(root, XmlRpcUtils.TAG_FAULT, XmlRpcUtils.TAG_VALUE, XmlRpcUtils.TAG_STRUCT);

            // fault code
            IXMLElement memberEl = createChild(structEl, XmlRpcUtils.TAG_MEMBER);
            createPCData(memberEl, XmlRpcUtils.TAG_NAME, XmlRpcUtils.VALUE_FAULT_CODE);
            createValue(createChild(memberEl, XmlRpcUtils.TAG_VALUE), response.getErrorCode());

            // fault string
            memberEl = createChild(structEl, XmlRpcUtils.TAG_MEMBER);
            createPCData(memberEl, XmlRpcUtils.TAG_NAME, XmlRpcUtils.VALUE_FAULT_STRING);
            createValue(createChild(memberEl, XmlRpcUtils.TAG_VALUE), response.getErrorString());

            // fault string
            if (useExtensions && response.getError() != null) {
                memberEl = createChild(structEl, XmlRpcUtils.TAG_MEMBER);
                createPCData(memberEl, XmlRpcUtils.TAG_NAME, XmlRpcUtils.VALUE_FAULT_CUSTOM);
                createValue(createChild(memberEl, XmlRpcUtils.TAG_VALUE), response.getError());
            }
        } else {
            createParams(createChild(root, XmlRpcUtils.TAG_PARAMS), response.getParameters());
        }

        writer.write(root, false, 0, true);

        return outputStream.toByteArray();
    }

    private static IXMLElement createChild(final IXMLElement root, final String tag) {
        final IXMLElement el = new XMLElement(tag);
        root.addChild(el);
        return el;
    }

    private static IXMLElement createChild(final IXMLElement root, final String... tags) {
        IXMLElement el = root;
        for (final String tag : tags) {
            final IXMLElement newEl = new XMLElement(tag);
            el.addChild(newEl);
            el = newEl;
        }
        return el;
    }

    private static IXMLElement createPCData(final IXMLElement root, final String tag, final String data) {
        final IXMLElement methodName = new XMLElement(tag);
        methodName.createPCDataElement();
        final IXMLElement pcData = methodName.createPCDataElement();
        pcData.setContent(data);
        methodName.addChild(pcData);
        root.addChild(methodName);
        return methodName;
    }

    private static void createParams(final IXMLElement root, final RPCParameters parameters) throws RPCException {
        final Object[] values = parameters.getValues();
        if (values.length == 0) {
            return;
        }

        for (final Object param : values) {
            createValue(createChild(root, XmlRpcUtils.TAG_PARAM, XmlRpcUtils.TAG_VALUE), param);
        }
    }

    private static void createValue(final IXMLElement root, final Object value) throws RPCException {
        if (value == null) {

            createChild(root, XmlRpcUtils.TAG_NIL);

        } else if (value instanceof Float || value instanceof Double) {

            createChild(root, XmlRpcUtils.TAG_DOUBLE).setContent(value.toString());

        } else if (value instanceof Number) {

            createChild(root, XmlRpcUtils.TAG_INT).setContent(value.toString());

        } else if (value instanceof Boolean) {

            createChild(root, XmlRpcUtils.TAG_BOOLEAN).setContent(value.toString());

        } else if (value instanceof String) {

            createChild(root, XmlRpcUtils.TAG_STRING).setContent(value.toString());

        } else if (value instanceof Date) {

            createChild(root, XmlRpcUtils.TAG_DATETIME_ISO8601)
                    .setContent(DateTimeUtils.formatXmlRpcIso8601((Date) value));

        } else if (value instanceof Calendar) {

            createChild(root, XmlRpcUtils.TAG_DATETIME_ISO8601)
                    .setContent(DateTimeUtils.formatXmlRpcIso8601(((Calendar) value).getTime()));

        } else if (value instanceof List) {

            final IXMLElement dataEl = createChild(root, XmlRpcUtils.TAG_ARRAY, XmlRpcUtils.TAG_DATA);
            for (final Object object : (List) value) {
                createValue(createChild(dataEl, XmlRpcUtils.TAG_VALUE), object);
            }

        } else if (value instanceof Map) {

            final IXMLElement structEl = createChild(root, XmlRpcUtils.TAG_STRUCT);
            for (final Object object : ((Map) value).entrySet()) {
                final Map.Entry entry = (Map.Entry) object;
                final IXMLElement memberEl = createChild(structEl, XmlRpcUtils.TAG_MEMBER);
                createPCData(memberEl, XmlRpcUtils.TAG_NAME, entry.getKey().toString());
                createValue(createChild(memberEl, XmlRpcUtils.TAG_VALUE), entry.getValue());
            }

        } else if (value instanceof Object[]) {

            final IXMLElement dataEl = createChild(root, XmlRpcUtils.TAG_ARRAY, XmlRpcUtils.TAG_DATA);
            for (final Object object : (Object[]) value) {
                createValue(createChild(dataEl, XmlRpcUtils.TAG_VALUE), object);
            }

        } else if (value instanceof byte[]) {

            createChild(root, XmlRpcUtils.TAG_BASE64).setContent(Base64.encodeToString((byte[]) value, false));

        } else {

            final Class valueClass = value.getClass();
            final Field[] fields = valueClass.getDeclaredFields();
            final IXMLElement structEl = createChild(root, XmlRpcUtils.TAG_STRUCT);
            if (fields.length > 0) {
                for (final Field field : fields) {
                    if (Modifier.isTransient(field.getModifiers())) {
                        continue;
                    }

                    final boolean accessible = field.isAccessible();
                    if (!accessible) {
                        field.setAccessible(true);
                    }

                    final IXMLElement memberEl = createChild(structEl, XmlRpcUtils.TAG_MEMBER);
                    try {
                        createPCData(memberEl, XmlRpcUtils.TAG_NAME, field.getName());
                        createValue(createChild(memberEl, XmlRpcUtils.TAG_VALUE), field.get(value));
                    } catch (IllegalAccessException e) {
                        throw RPCException.newNested(e);
                    } finally {
                        if (!accessible) {
                            field.setAccessible(false);
                        }
                    }
                }
            }

        }
    }
}
