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

package org.openjst.server.mobile.dao;

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.server.commons.network.Actor;
import org.openjst.server.commons.network.Message;
import org.openjst.server.commons.network.Persistence;

/**
 * @author Sergey Grachev
 */
public interface RPCMessagesDAO<R extends Actor<?>, M extends Message<?>> extends Persistence<R, M> {

    void createForwardToClient(Long accountId, Long clientId, RPCMessageFormat format, byte[] data);

    void createForwardToServer(Long accountId, Long clientId, RPCMessageFormat format, byte[] data);
}
