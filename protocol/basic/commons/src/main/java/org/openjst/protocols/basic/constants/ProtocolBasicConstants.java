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
public final class ProtocolBasicConstants {

    public static final byte VERSION = 1;
    public static final int DEFAULT_CLIENTS_PORT = 5558;
    public static final int DEFAULT_SERVERS_PORT = DEFAULT_CLIENTS_PORT + 1;

    private ProtocolBasicConstants() {
    }
}
