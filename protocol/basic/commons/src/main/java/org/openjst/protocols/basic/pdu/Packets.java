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

package org.openjst.protocols.basic.pdu;

/**
 * @author Sergey Grachev
 */
public final class Packets {

    public static final byte TYPE_DATETIME_SYNC = 1;
    public static final byte TYPE_PING = 2;
    public static final byte TYPE_AUTH_BASIC = 3;
    public static final byte TYPE_AUTH_RESPONSE = 6;
    public static final byte TYPE_PRESENCE_STATE = 7;
    public static final byte TYPE_RPC_METHOD = 8;

    private Packets() {
    }
}
