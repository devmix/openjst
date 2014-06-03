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
import com.fasterxml.jackson.core.JsonGenerator;
import org.openjst.commons.rpc.RPCParameters;
import org.openjst.commons.rpc.RPCRequest;
import org.openjst.commons.rpc.RPCResponse;
import org.openjst.commons.rpc.utils.RPCUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Sergey Grachev
 */
final class JsonRpcWriter {

    private final ByteArrayOutputStream stream;
    private final JsonGenerator generator;
    private final boolean useExtensions;

    public JsonRpcWriter(boolean useExtensions) throws IOException {
        this.useExtensions = useExtensions;
        stream = new ByteArrayOutputStream();
        generator = new JsonFactory().createJsonGenerator(stream);
        generator.setCodec(new JsonRpcObjectCodec());
    }

    public byte[] write(final RPCRequest request) throws IOException {
        generator.writeStartObject();
        generator.writeStringField(JsonRpc.FIELD_JSONRPC, "2.0");
        generator.writeStringField(JsonRpc.FIELD_METHOD, RPCUtils.makeMethodName(request.getObject(), request.getMethod()));
        final RPCParameters parameters = request.getParameters();
        if (!parameters.isEmpty()) {
            generator.writeFieldName(JsonRpc.FIELD_PARAMS);
            generator.writeObject(parameters.getValues());
        }
        generator.writeStringField(JsonRpc.FIELD_ID, request.getId());
        generator.writeEndObject();
        generator.close();
        return stream.toByteArray();
    }

    public byte[] write(final RPCResponse response) throws IOException {
        generator.writeStartObject();
        generator.writeStringField(JsonRpc.FIELD_JSONRPC, "2.0");
        if (response.isError()) {
            generator.writeFieldName(JsonRpc.FIELD_ERROR);
            generator.writeStartObject();
            generator.writeNumberField(JsonRpc.FIELD_CODE, response.getErrorCode());
            generator.writeStringField(JsonRpc.FIELD_MESSAGE, response.getErrorString());
            if (useExtensions && response.getError() != null) {
                generator.writeObjectField(JsonRpc.FIELD_ERROR, response.getError());
            }
            generator.writeEndObject();
        } else {
            generator.writeObjectField(JsonRpc.FIELD_RESULT, response.getParameters().getValues());
        }
        generator.writeStringField(JsonRpc.FIELD_ID, response.getId());
        generator.writeEndObject();
        generator.close();
        return stream.toByteArray();
    }
}
