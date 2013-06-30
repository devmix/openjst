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

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.rpc.RPCContext;
import org.openjst.commons.rpc.RPCParameters;
import org.openjst.commons.rpc.RPCRequest;
import org.openjst.commons.rpc.RPCResponse;

/**
 * @author Sergey Grachev
 */
public final class RPCObjectsFactory {

    private static final RPCParameters EMPTY_PARAMETERS = new EmptyParameters();

    private RPCObjectsFactory() {
    }

    public static RPCParameters emptyParameters() {
        return EMPTY_PARAMETERS;
    }

    public static RPCRequest newRequest(final String id, @Nullable final String objectName, final String methodName,
                                        @Nullable final RPCParameters parameters) {
        return new DefaultRequest(id, objectName, methodName, parameters);
    }

    public static RPCContext newContext(final boolean useCache) {
        return new DefaultContext(useCache);
    }

    public static RPCContext newContext() {
        return new DefaultContext();
    }

    public static RPCResponse newResponse(final String id, final int errorCode, final String errorMessage) {
        return new DefaultResponse(id, errorCode, errorMessage);
    }

    public static RPCResponse newResponse(final String id, final RPCParameters parameters) {
        return new DefaultResponse(id, parameters);
    }

    public static RPCResponse newResponse(final String id, final int errorCode, final String errorMessage, final Object error) {
        return new DefaultResponse(id, errorCode, errorMessage, error);
    }

    public static RPCParameters newParameters() {
        return new DefaultParameters();
    }
}
