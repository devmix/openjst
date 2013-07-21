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

package org.openjst.server.commons.network;

import org.jetbrains.annotations.Nullable;
import org.openjst.server.commons.model.types.MessageDeliveryState;

/**
 * @author Sergey Grachev
 */
public final class DeliveryResult {

    private static final DeliveryResult DELIVERY_OK = new DeliveryResult(MessageDeliveryState.OK, null);

    private final MessageDeliveryState state;
    private final String message;

    private DeliveryResult(final MessageDeliveryState state, final String message) {
        this.state = state;
        this.message = message;
    }

    public static DeliveryResult as(final MessageDeliveryState state, @Nullable final String message) {
        return new DeliveryResult(state, message);
    }

    public static DeliveryResult ok() {
        return DELIVERY_OK;
    }

    public boolean isSuccess() {
        return MessageDeliveryState.OK.equals(state);
    }

    public MessageDeliveryState getState() {
        return state;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DeliveryResult result = (DeliveryResult) o;
        return state == result.state;
    }

    @Override
    public int hashCode() {
        return state != null ? state.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DeliveryResult{" +
                "state=" + state +
                ", message='" + message + '\'' +
                '}';
    }
}
