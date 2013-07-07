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

package org.openjst.server.mobile.rpc;

import org.jetbrains.annotations.Nullable;

/**
 * @author Sergey Grachev
 */
public final class DeliveryResult {

    private static final DeliveryResult DELIVERY_OK = new DeliveryResult(Error.NONE, null);

    private final Error error;
    private final String message;

    private DeliveryResult(final Error error, final String message) {
        this.error = error;
        this.message = message;
    }

    public static DeliveryResult fail(final Error error, @Nullable final String message) {
        return new DeliveryResult(error, message);
    }

    public static DeliveryResult ok() {
        return DELIVERY_OK;
    }

    public boolean isSuccess() {
        return Error.NONE.equals(error);
    }

    public Error getError() {
        return error;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DeliveryResult{" +
                "error=" + error +
                ", message='" + message + '\'' +
                '}';
    }

    public static enum Error {
        NONE,
        AUTHENTICATION_TIMEOUT,
        AUTHENTICATION_FAIL,
        AUTHENTICATION_UNSUPPORTED,
        AUTHENTICATION_INTERNAL_SERVER_ERROR,
        NO_FORWARD_SERVER,
        NO_RESPONSE
    }
}
