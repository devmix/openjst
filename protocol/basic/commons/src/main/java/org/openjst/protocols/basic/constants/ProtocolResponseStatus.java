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

package org.openjst.protocols.basic.constants;

/**
 * @author Sergey Grachev
 */
public final class ProtocolResponseStatus {
    // common errors
    public static final short OK = 0;

    // authenticate errors
    public static final short AUTH_FAIL = 100;
    public static final short AUTH_UNEXPECTED_PACKET = 101;
    public static final short AUTH_UNSUPPORTED = 102;
    public static final short AUTH_INCORRECT_HANDSHAKE = 103;
    public static final short AUTH_TIMEOUT = 104;
    public static final short AUTH_INTERNAL_ERROR = 105;
    public static final short AUTH_INTERRUPTED = 106;
    public static final short NO_FORWARD_RECIPIENT = 108;
    public static final short NO_RESPONSE = 109;

    private ProtocolResponseStatus() {
    }
}
