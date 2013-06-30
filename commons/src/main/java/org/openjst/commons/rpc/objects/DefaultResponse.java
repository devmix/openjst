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

package org.openjst.commons.rpc.objects;

import org.openjst.commons.rpc.RPCParameters;
import org.openjst.commons.rpc.RPCResponse;

/**
 * @author Sergey Grachev
 */
final class DefaultResponse implements RPCResponse {

    private final String id;
    private final int errorCode;
    private final String errorString;
    private final RPCParameters parameters;
    private final Object error;
    private final boolean responseWithError;

    public DefaultResponse(final String id, final int errorCode, final String errorString) {
        this.id = id;
        this.errorCode = errorCode;
        this.errorString = errorString;
        this.error = null;
        this.parameters = null;
        this.responseWithError = true;
    }

    public DefaultResponse(final String id, final RPCParameters parameters) {
        this.id = id;
        this.errorCode = 0;
        this.errorString = null;
        this.error = null;
        this.parameters = parameters;
        this.responseWithError = false;
    }

    public DefaultResponse(final String id, final int errorCode, final String errorString, final Object error) {
        this.id = id;
        this.errorCode = errorCode;
        this.errorString = errorString;
        this.error = error;
        this.parameters = null;
        this.responseWithError = true;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorString() {
        return errorString;
    }

    public String getId() {
        return id;
    }

    public RPCParameters getParameters() {
        return parameters;
    }

    public Object getError() {
        return error;
    }

    public boolean isError() {
        return responseWithError;
    }

    @Override
    public String toString() {
        return "DefaultResponse{" +
                "id='" + id + '\'' +
                ", errorCode=" + errorCode +
                ", errorString='" + errorString + '\'' +
                ", parameters=" + parameters +
                ", error=" + error +
                ", responseWithError=" + responseWithError +
                '}';
    }
}
