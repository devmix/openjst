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
import org.openjst.commons.rpc.RPCRequest;

import javax.annotation.Nullable;

/**
 * @author Sergey Grachev
 */
final class DefaultRequest implements RPCRequest {

    private final String id;
    private final String objectName;
    private final String methodName;
    private final RPCParameters parameters;

    public DefaultRequest(final String id, @Nullable final String objectName, final String methodName,
                          @Nullable final RPCParameters parameters) {
        this.id = id;
        this.objectName = objectName;
        this.methodName = methodName;
        this.parameters = parameters == null ? RPCObjectsFactory.emptyParameters() : parameters;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public String getObject() {
        return objectName;
    }

    public String getMethod() {
        return methodName;
    }

    public RPCParameters getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "DefaultRequest{" +
                "id='" + id + '\'' +
                ", objectName='" + objectName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
