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

package org.openjst.client.android.dao;

import org.jetbrains.annotations.Nullable;
import org.openjst.client.android.commons.database.DataAccessObject;
import org.openjst.client.android.dto.TrafficSummary;
import org.openjst.commons.dto.ApplicationVersion;
import org.openjst.commons.rpc.RPCMessageFormat;

/**
 * @author Sergey Grachev
 */
public interface SessionDAO extends DataAccessObject {

    void outPersist(String uuid, RPCMessageFormat format, byte[] data);

    void outStatus(String uuid, PacketStatus sent);

    void inPersist(String uuid, RPCMessageFormat format, byte[] data);

    void inStatus(String uuid, PacketStatus status);

    void inResponse(String uuid);

    void persistVersion(ApplicationVersion version);

    @Nullable
    Long findVersionId(int major, int minor, int build);

    ApplicationVersion getLatestVersion();

    TrafficSummary getTrafficSummary();

    enum PacketStatus {
        IDLE, ERROR, SENT, RECEIVED, PRECESSED
    }
}
