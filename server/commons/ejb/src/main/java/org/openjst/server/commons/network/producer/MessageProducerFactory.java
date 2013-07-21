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

package org.openjst.server.commons.network.producer;

import org.openjst.server.commons.network.*;

/**
 * @author Sergey Grachev
 */
public final class MessageProducerFactory {

    private MessageProducerFactory() {
    }

    public static <P extends Persistence<R, M>, R extends Actor<?>, M extends Message<?>> MessageProducer<P, R, M> newProducerForClients(
            final Router<R, M> router, final P persistence, final int threadsCount
    ) {
        return new GenericProducer<P, R, M>(router, persistence, threadsCount, ActorsType.CLIENT);
    }

    public static <P extends Persistence<R, M>, R extends Actor<?>, M extends Message<?>> MessageProducer<P, R, M> newProducerForServers(
            final Router<R, M> router, final P persistence, final int threadsCount
    ) {
        return new GenericProducer<P, R, M>(router, persistence, threadsCount, ActorsType.SERVER);
    }
}
