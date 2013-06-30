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

package org.openjst.commons.rpc;

import org.openjst.commons.rpc.exceptions.RPCException;

/**
 * @author Sergey Grachev
 */
public interface RPCFormat {

    <T extends RPCMessage> T read(byte[] data) throws RPCException;

    byte[] write(RPCRequest request) throws RPCException;

    byte[] write(RPCResponse response) throws RPCException;
}
