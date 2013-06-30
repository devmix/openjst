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

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.io.buffer.ArrayDataBuffer;
import org.openjst.commons.io.buffer.DataBufferException;
import org.openjst.commons.rpc.RPCMessage;
import org.openjst.commons.rpc.RPCParameters;
import org.openjst.commons.rpc.RPCRequest;
import org.openjst.commons.rpc.RPCResponse;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.commons.rpc.objects.RPCObjectsFactory;

import java.util.*;

/**
 * @author Sergey Grachev
 */
final class BinaryRpcReader {

    private final ArrayDataBuffer buffer;
    private int currentTag;

    public BinaryRpcReader(final byte[] data) {
        buffer = new ArrayDataBuffer(Arrays.copyOf(data, data.length));
    }

    @Nullable
    public <T extends RPCMessage> T read() throws DataBufferException, RPCException {
        readNextTag();
        switch (currentTag) {
            case BinaryRpcUtils.TAG_REQUEST: {
                //noinspection unchecked
                return (T) readRequest();
            }
            case BinaryRpcUtils.TAG_RESPONSE: {
                //noinspection unchecked
                return (T) readResponse();
            }
            case BinaryRpcUtils.TAG_RESPONSE_ERROR: {
                //noinspection unchecked
                return (T) readResponseError();
            }
            default: {
                throw RPCException.newUnexpectedBinaryTag(currentTag);
            }
        }
    }

    private RPCResponse readResponse() throws DataBufferException, RPCException {
        readNextTag();
        switch (currentTag) {
            case BinaryRpcUtils.TAG_PARAMETERS: {
                return RPCObjectsFactory.newResponse("", readParameters());
            }
            default: {
                throw RPCException.newUnexpectedBinaryTag(currentTag);
            }
        }
    }

    private RPCResponse readResponseError() throws DataBufferException, RPCException {
        Integer errorCode = null;
        String errorString = null;
        Object error = null;

        while (readNextTag()) {
            switch (currentTag) {
                case BinaryRpcUtils.TAG_ERROR_CODE: {
                    errorCode = buffer.readVLQInt32();
                    break;
                }
                case BinaryRpcUtils.TAG_ERROR_STRING: {
                    errorString = buffer.readUtf8();
                    break;
                }
                case BinaryRpcUtils.TAG_ERROR_OBJECT: {
                    readNextTag();
                    error = readValue();
                    break;
                }
                default: {
                    throw RPCException.newUnexpectedBinaryTag(currentTag);
                }
            }
        }

        return RPCObjectsFactory.newResponse("", errorCode == null ? 0 : errorCode, errorString, error);
    }

    private RPCRequest readRequest() throws DataBufferException, RPCException {
        String objectName = null;
        String methodName = null;
        RPCParameters parameters = null;

        while (readNextTag()) {
            switch (this.currentTag) {
                case BinaryRpcUtils.TAG_OBJECT: {
                    objectName = buffer.readUtf8();
                    break;
                }
                case BinaryRpcUtils.TAG_METHOD: {
                    methodName = buffer.readUtf8();
                    break;
                }
                case BinaryRpcUtils.TAG_PARAMETERS: {
                    parameters = readParameters();
                    break;
                }
                default: {
                    throw RPCException.newUnexpectedBinaryTag(currentTag);
                }
            }
        }

        if (methodName == null) {
            throw RPCException.newMissedMethodName();
        }

        return RPCObjectsFactory.newRequest("", objectName, methodName, parameters);
    }

    private RPCParameters readParameters() throws DataBufferException, RPCException {
        RPCParameters parameters = null;

        while (readNextTag()) {
            if (parameters == null) {
                parameters = RPCObjectsFactory.newParameters();
            }
            parameters.add(readValue());
        }

        return parameters;
    }

    private Object readValue() throws DataBufferException, RPCException {
        switch (currentTag) {
            case BinaryRpcUtils.TAG_VALUE_NIL: {
                return null;
            }
            case BinaryRpcUtils.TAG_VALUE_FLOAT32: {
                return buffer.readFloat32();
            }
            case BinaryRpcUtils.TAG_VALUE_FLOAT64: {
                return buffer.readFloat64();
            }
            case BinaryRpcUtils.TAG_VALUE_INT8: {
                return buffer.readInt8();
            }
            case BinaryRpcUtils.TAG_VALUE_INT16: {
                return buffer.readInt16();
            }
            case BinaryRpcUtils.TAG_VALUE_INT32: {
                return buffer.readInt32();
            }
            case BinaryRpcUtils.TAG_VALUE_INT64: {
                return buffer.readInt64();
            }
            case BinaryRpcUtils.TAG_VALUE_BOOLEAN8: {
                return buffer.readBoolean8();
            }
            case BinaryRpcUtils.TAG_VALUE_UTF8: {
                return buffer.readUtf8();
            }
            case BinaryRpcUtils.TAG_VALUE_DATE: {
                final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(buffer.readInt64());
                return calendar.getTime();
            }
            case BinaryRpcUtils.TAG_VALUE_ARRAY: {
                return readArrays();
            }
            case BinaryRpcUtils.TAG_VALUE_BINARY: {
                return buffer.readBytes(buffer.readInt32());
            }
            case BinaryRpcUtils.TAG_VALUE_MAP: {
                return readMap();
            }
            default: {
                throw RPCException.newUnexpectedBinaryTag(currentTag);
            }
        }
    }

    private Object readArrays() throws DataBufferException, RPCException {
        final List<Object> array = new LinkedList<Object>();

        while (readNextTag()) {
            array.add(readValue());
        }

        return array.toArray();
    }

    private Map<String, Object> readMap() throws DataBufferException, RPCException {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();

        while (readNextTag()) {
            switch (currentTag) {
                case BinaryRpcUtils.TAG_VALUE_MAP_ENTRY: {
                    readMapEntry(result);
                    break;
                }
                default: {
                    throw RPCException.newUnexpectedBinaryTag(currentTag);
                }
            }
        }

        return result;
    }

    private void readMapEntry(final Map<String, Object> result) throws DataBufferException, RPCException {
        String name = null;
        Object value = null;

        while (readNextTag()) {
            switch (currentTag) {
                case BinaryRpcUtils.TAG_VALUE_MAP_KEY: {
                    readNextTag();
                    name = (String) readValue();
                    break;
                }
                case BinaryRpcUtils.TAG_VALUE_MAP_VALUE: {
                    readNextTag();
                    value = readValue();
                    break;
                }
                default: {
                    throw RPCException.newUnexpectedBinaryTag(currentTag);
                }
            }
        }

        result.put(name, value);
    }

    private boolean readNextTag() throws DataBufferException {
        if (buffer.getOffset() < buffer.getSize()) {
            currentTag = buffer.readVLQInt32();
            return currentTag != BinaryRpcUtils.TAG_RESERVED_CLOSE;
        }
        return false;
    }
}
