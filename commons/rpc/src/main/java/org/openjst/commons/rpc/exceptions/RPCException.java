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

package org.openjst.commons.rpc.exceptions;

/**
 * @author Sergey Grachev
 */
public final class RPCException extends Exception {

    private static final long serialVersionUID = 3060612293778758038L;

    private RPCException(final String message) {
        super(message);
    }

    private RPCException(final Throwable cause) {
        super(cause);
    }

    public static RPCException newNested(final Exception e) {
        return new RPCException(e);
    }

    public static RPCException newMissedMethodName() {
        return new RPCException(String.format("Missed name of method"));
    }

    public static RPCException newUnexpectedXmlTag(final String tag, final int lineNumber) {
        return new RPCException(String.format("Unexpected tag '%s' at line %d", tag, lineNumber));
    }

    public static RPCException newUnexpectedXmlPCData(final String pcData, final int lineNumber) {
        return new RPCException(String.format("Unexpected PCData '%s' at line %d", pcData, lineNumber));
    }

    public static RPCException newIncorrectMethodName(final String methodName) {
        return new RPCException(String.format("Incorrect name of method '%s'", methodName));
    }

    public static RPCException newUnknownMessageFormat() {
        return new RPCException("Unknown format of RPC message");
    }

    public static RPCException newUnexpectedBinaryTag(final int tag) {
        return new RPCException(String.format("Unexpected binary tag '%d'", tag));
    }

    public static RPCException newUnexpectedJsonField(final String fieldName) {
        return new RPCException(String.format("Unexpected JSON field '%s'", fieldName));
    }

    public static RPCException newWithMessage(final String message) {
        return new RPCException(message);
    }
}
