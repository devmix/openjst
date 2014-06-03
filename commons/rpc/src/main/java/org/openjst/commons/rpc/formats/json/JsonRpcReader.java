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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.rpc.RPCMessage;
import org.openjst.commons.rpc.RPCParameters;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.commons.rpc.objects.RPCObjectsFactory;
import org.openjst.commons.rpc.utils.RPCUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Sergey Grachev
 */
final class JsonRpcReader {

    private final JsonParser jp;
    private final boolean useExtensions;
    private String version;
    private String objectName;
    private String methodName;
    private String id;
    private RPCParameters parameters = RPCObjectsFactory.newParameters();
    private Integer errorCode;
    private String errorString;
    private Object error;
    private boolean isError = false;
    private boolean isResponse = false;

    public JsonRpcReader(final boolean useExtensions, final byte[] data) throws IOException {
        this.useExtensions = useExtensions;
        jp = new JsonFactory().createJsonParser(Arrays.copyOf(data, data.length));
        jp.setCodec(new JsonRpcObjectCodec());
    }

    public <T extends RPCMessage> T read() throws IOException, RPCException {
        jp.nextToken();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            final String fieldName = jp.getCurrentName();
            jp.nextToken();

            if (JsonRpc.FIELD_JSONRPC.equals(fieldName)) {

                version = jp.getText();

            } else if (JsonRpc.FIELD_METHOD.equals(fieldName)) {

                final String name = jp.getText();
                final Pair<String, String> method = RPCUtils.parseMethod(name);
                if (method == null) {
                    throw RPCException.newIncorrectMethodName(name);
                }
                objectName = method.first();
                methodName = method.second();

            } else if (JsonRpc.FIELD_PARAMS.equals(fieldName)) {

                if (JsonToken.START_ARRAY == jp.getCurrentToken()) {

                    final Object[] objects = jp.readValueAs(Object[].class);
                    parameters.addAll(objects);

                } else {
                    parameters.add(jp.readValueAs(Object.class));
                }


            } else if (JsonRpc.FIELD_RESULT.equals(fieldName)) {

                isResponse = true;
                if (JsonToken.START_ARRAY == jp.getCurrentToken()) {

                    final Object[] objects = jp.readValueAs(Object[].class);
                    parameters.addAll(objects);

                } else {
                    parameters.add(jp.readValueAs(Object.class));
                }

            } else if (JsonRpc.FIELD_ID.equals(fieldName)) {

                id = jp.getText();

            } else if (JsonRpc.FIELD_ERROR.equals(fieldName)) {

                isResponse = isError = true;
                jp.nextToken();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    if (JsonRpc.FIELD_CODE.equals(fieldName)) {
                        errorCode = jp.getValueAsInt();
                    } else if (JsonRpc.FIELD_MESSAGE.equals(fieldName)) {
                        errorString = jp.getText();
                    } else if (JsonRpc.FIELD_ERROR.equals(fieldName)) {
                        error = jp.readValueAs(Object.class);
                    } else {
                        throw RPCException.newUnexpectedJsonField(fieldName);
                    }
                }

            } else {

                throw RPCException.newUnexpectedJsonField(fieldName);

            }
        }
        jp.close();

        if (isError) {
            //noinspection unchecked
            return (T) RPCObjectsFactory.newResponse(id, errorCode, errorString, error);
        }

        if (isResponse) {
            //noinspection unchecked
            return (T) RPCObjectsFactory.newResponse(id, parameters);
        }

        //noinspection unchecked
        return (T) RPCObjectsFactory.newRequest(id, objectName, methodName, parameters);
    }

}
