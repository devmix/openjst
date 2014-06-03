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

package org.openjst.server.mobile.listeners;

import org.openjst.commons.dto.ApplicationVersion;
import org.openjst.commons.rpc.RPCParameters;
import org.openjst.commons.rpc.RPCRequest;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.server.mobile.dao.UpdatesDAO;
import org.openjst.server.mobile.events.UpdateChangeEvent;
import org.openjst.server.mobile.network.NetworkService;
import org.openjst.server.mobile.rpc.RPCNames;

import javax.annotation.security.PermitAll;
import javax.ejb.*;
import javax.enterprise.event.Observes;

import static javax.enterprise.event.TransactionPhase.AFTER_COMPLETION;
import static org.openjst.commons.rpc.RPCMessageFormat.BINARY;
import static org.openjst.commons.rpc.objects.RPCObjectsFactory.newParameters;
import static org.openjst.commons.rpc.objects.RPCObjectsFactory.newRequest;
import static org.openjst.commons.rpc.utils.RPCUtils.uniqueRequestId;

/**
 * @author Sergey Grachev
 */
@Singleton
@Asynchronous
@Lock(LockType.READ)
@PermitAll
public class ClientsEventsListener {

    @EJB
    private NetworkService networkService;

    @EJB
    private UpdatesDAO updatesDAO;

    public void afterUpdateChanged(@Observes(during = AFTER_COMPLETION) final UpdateChangeEvent event) throws RPCException {
        final ApplicationVersion version = new ApplicationVersion(event.getUploadDate().getTime(),
                event.getMajor(), event.getMinor(), event.getBuild(), event.getDescription());

        final RPCParameters parameters = newParameters().add(event.getOS()).add(version);

        final RPCRequest request = newRequest(uniqueRequestId(),
                RPCNames.OBJECT_CORE_UPDATES, event.getAction().name().toLowerCase(), parameters);

        networkService.clientRPCForwardBroadcast(event.getAccountId(), BINARY,
                BINARY.newFormatter(true).write(request), true);
    }
}
