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

package org.openjst.protocols.basic.test.simulator;

import org.openjst.protocols.basic.constants.ProtocolBasicConstants;

/**
 * @author Sergey Grachev
 */
public final class Simulator {

    public static final String HOST = "localhost";
    public static final String ACCOUNT = "account1";
    public static final byte[] ACCOUNT_KEY = new byte[]{1, 2, 3, 4, 5};
    public static final String CLIENT = "client1";
    public static final String CLIENT_PASSWORD = "client1_pass";

    public static void main(final String[] args) throws InterruptedException {
        final ClientSimulator clientSimulator = new ClientSimulator(
                ACCOUNT, CLIENT, CLIENT_PASSWORD, HOST, ProtocolBasicConstants.DEFAULT_CLIENTS_PORT);

        final ClientServerSimulator clientServerSimulator = new ClientServerSimulator(
                ACCOUNT, CLIENT, CLIENT_PASSWORD, ACCOUNT_KEY, HOST, ProtocolBasicConstants.DEFAULT_SERVERS_PORT);

        final RouterSimulator routerSimulator = new RouterSimulator(
                HOST, ProtocolBasicConstants.DEFAULT_CLIENTS_PORT, ProtocolBasicConstants.DEFAULT_SERVERS_PORT);
        routerSimulator.addAccountClient(ACCOUNT, CLIENT, CLIENT_PASSWORD);
        routerSimulator.addAccountApiKey(ACCOUNT, ACCOUNT_KEY);

        routerSimulator.start();

        clientServerSimulator.connect();

        Thread.sleep(200); // wait while clientServerSimulator connected

        clientSimulator.connect();
    }
}
