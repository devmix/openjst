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

package org.openjst.protocols.basic.utils;

import org.openjst.protocols.basic.constants.ProtocolResponseStatus;

/**
 * @author Sergey Grachev
 */
public final class LogUtils {

    private LogUtils() {
    }

    public static StringBuilder statusAsStringBuilder(final int status) {
        final StringBuilder msg = new StringBuilder();
        switch (status) {
            case ProtocolResponseStatus.AUTH_UNEXPECTED_PACKET:
                msg.append("Unauthorized access, waiting for authentication request");
                break;
            case ProtocolResponseStatus.AUTH_UNSUPPORTED:
                msg.append("Unauthorized access or unsupported authentication method");
                break;
            case ProtocolResponseStatus.AUTH_FAIL:
                msg.append("Authentication failed");
                break;
            case ProtocolResponseStatus.AUTH_INTERNAL_ERROR:
                msg.append("Internal error");
                break;
            case ProtocolResponseStatus.AUTH_INCORRECT_HANDSHAKE:
                msg.append("Server request ID not match to client request ID");
                break;
            case ProtocolResponseStatus.AUTH_TIMEOUT:
                msg.append("Authentication timeout, no response");
                break;

            case ProtocolResponseStatus.NO_FORWARD_SERVER:
                msg.append("No server for forward packet");
                break;
        }
        return msg;
    }
}
