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

import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
final class ActorsWatcher<A> {

    private final Set<A> changed = new LinkedHashSet<A>();

    @Nullable
    public A poll() {
        synchronized (changed) {
            final Iterator<A> it = changed.iterator();
            if (it.hasNext()) {
                try {
                    return it.next();
                } finally {
                    it.remove();
                }
            }
        }
        return null;
    }

    public void add(final A actor) {
        synchronized (changed) {
            changed.add(actor);
        }
    }
}
