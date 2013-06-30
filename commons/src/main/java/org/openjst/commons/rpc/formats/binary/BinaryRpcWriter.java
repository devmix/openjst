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

import org.openjst.commons.io.buffer.ArrayDataBuffer;
import org.openjst.commons.io.buffer.DataBufferException;
import org.openjst.commons.rpc.RPCParameters;
import org.openjst.commons.rpc.RPCRequest;
import org.openjst.commons.rpc.RPCResponse;
import org.openjst.commons.rpc.exceptions.RPCException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
final class BinaryRpcWriter {

    private final ArrayDataBuffer buffer;

    public BinaryRpcWriter() {
        buffer = new ArrayDataBuffer();
    }

    public byte[] write(final RPCRequest request) throws DataBufferException, RPCException {
        writeTag(BinaryRpcUtils.TAG_REQUEST);
        writeObjectName(request.getObject());
        writeMethodName(request.getMethod());
        writeParameters(request.getParameters());
        writeCloseTag();

        return buffer.toByteArray();
    }

    public byte[] write(final RPCResponse response) throws DataBufferException, RPCException {
        if (response.isError()) {
            writeTag(BinaryRpcUtils.TAG_RESPONSE_ERROR);
            writeErrorCode(response.getErrorCode());
            writeErrorString(response.getErrorString());
            writeError(response.getError());
        } else {
            writeTag(BinaryRpcUtils.TAG_RESPONSE);
            writeParameters(response.getParameters());
        }
        writeCloseTag();

        return buffer.toByteArray();
    }

    private void writeObjectName(final String objectName) throws DataBufferException {
        if (objectName == null || objectName.trim().isEmpty()) {
            return;
        }

        writeTag(BinaryRpcUtils.TAG_OBJECT);
        buffer.writeUtf8(objectName);
    }

    private void writeMethodName(final String methodName) throws RPCException, DataBufferException {
        if (methodName == null || methodName.trim().isEmpty()) {
            throw RPCException.newMissedMethodName();
        }

        writeTag(BinaryRpcUtils.TAG_METHOD);
        buffer.writeUtf8(methodName);
    }

    private void writeParameters(final RPCParameters parameters) throws DataBufferException, RPCException {
        if (parameters.isEmpty()) {
            return;
        }

        writeTag(BinaryRpcUtils.TAG_PARAMETERS);
        for (final Object obj : parameters.getValues()) {
            writeValue(obj);
        }

        writeCloseTag();
    }

    private void writeValue(final Object value) throws DataBufferException, RPCException {
        if (value == null) {

            writeTag(BinaryRpcUtils.TAG_VALUE_NIL);

        } else if (value instanceof Float) {

            writeTag(BinaryRpcUtils.TAG_VALUE_FLOAT32);
            buffer.writeFloat32((Float) value);

        } else if (value instanceof Double) {

            writeTag(BinaryRpcUtils.TAG_VALUE_FLOAT64);
            buffer.writeFloat64((Double) value);

        } else if (value instanceof Byte) {

            writeTag(BinaryRpcUtils.TAG_VALUE_INT8);
            buffer.writeInt8((Byte) value);

        } else if (value instanceof Short) {

            writeTag(BinaryRpcUtils.TAG_VALUE_INT16);
            buffer.writeInt16((Short) value);

        } else if (value instanceof Integer) {

            writeTag(BinaryRpcUtils.TAG_VALUE_INT32);
            buffer.writeInt32((Integer) value);

        } else if (value instanceof Long) {

            writeTag(BinaryRpcUtils.TAG_VALUE_INT64);
            buffer.writeInt64((Long) value);

        } else if (value instanceof Boolean) {

            writeTag(BinaryRpcUtils.TAG_VALUE_BOOLEAN8);
            buffer.writeBoolean8((Boolean) value);

        } else if (value instanceof String) {

            writeTag(BinaryRpcUtils.TAG_VALUE_UTF8);
            buffer.writeUtf8((String) value);

        } else if (value instanceof Date) {

            writeTag(BinaryRpcUtils.TAG_VALUE_DATE);
            buffer.writeInt64(((Date) value).getTime());

        } else if (value instanceof Calendar) {

            writeTag(BinaryRpcUtils.TAG_VALUE_DATE);
            buffer.writeInt64(((Calendar) value).getTime().getTime());

        } else if (value instanceof List) {

            writeTag(BinaryRpcUtils.TAG_VALUE_ARRAY);
            for (final Object object : (List) value) {
                writeValue(object);
            }
            writeCloseTag();

        } else if (value instanceof Map) {

            writeTag(BinaryRpcUtils.TAG_VALUE_MAP);
            for (final Object object : ((Map) value).entrySet()) {
                final Map.Entry entry = (Map.Entry) object;
                writeMapEntry(entry.getKey().toString(), entry.getValue());
            }
            writeCloseTag();

        } else if (value instanceof Object[]) {

            writeTag(BinaryRpcUtils.TAG_VALUE_ARRAY);
            for (final Object object : (Object[]) value) {
                writeValue(object);
            }
            writeCloseTag();

        } else if (value instanceof byte[]) {

            final byte[] bytes = (byte[]) value;
            writeTag(BinaryRpcUtils.TAG_VALUE_BINARY);
            buffer.writeInt32(bytes.length);
            buffer.writeBytes(bytes);

        } else {

            final Class valueClass = value.getClass();
            final Field[] fields = valueClass.getDeclaredFields();
            writeTag(BinaryRpcUtils.TAG_VALUE_MAP);
            if (fields.length > 0) {
                for (final Field field : fields) {
                    if (Modifier.isTransient(field.getModifiers())) {
                        continue;
                    }

                    final boolean accessible = field.isAccessible();
                    if (!accessible) {
                        field.setAccessible(true);
                    }

                    try {
                        writeMapEntry(field.getName(), field.get(value));
                    } catch (IllegalAccessException e) {
                        throw RPCException.newNested(e);
                    } finally {
                        if (!accessible) {
                            field.setAccessible(false);
                        }
                    }
                }
            }
            writeCloseTag();
        }
    }

    private void writeMapEntry(final String name, final Object value) throws DataBufferException, RPCException {
        writeTag(BinaryRpcUtils.TAG_VALUE_MAP_ENTRY);
        writeMapKey(name);
        writeMapValue(value);
        writeCloseTag();
    }

    private void writeMapKey(final String key) throws DataBufferException, RPCException {
        writeTag(BinaryRpcUtils.TAG_VALUE_MAP_KEY);
        writeValue(key);
    }

    private void writeMapValue(final Object value) throws DataBufferException, RPCException {
        writeTag(BinaryRpcUtils.TAG_VALUE_MAP_VALUE);
        writeValue(value);
    }

    private void writeErrorString(final String value) throws DataBufferException {
        writeTag(BinaryRpcUtils.TAG_ERROR_STRING);
        buffer.writeUtf8(value);
    }

    private void writeErrorCode(final int value) throws DataBufferException, RPCException {
        writeTag(BinaryRpcUtils.TAG_ERROR_CODE);
        buffer.writeVLQInt32(value);
    }

    private void writeError(final Object value) throws DataBufferException, RPCException {
        writeTag(BinaryRpcUtils.TAG_ERROR_OBJECT);
        writeValue(value);
    }

    private void writeTag(final int tag) throws DataBufferException {
        buffer.writeVLQInt32(tag);
    }

    private void writeCloseTag() throws DataBufferException {
        buffer.writeVLQInt32(BinaryRpcUtils.TAG_RESERVED_CLOSE);
    }
}
