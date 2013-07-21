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

package org.openjst.server.mobile.model.dto;

import org.jetbrains.annotations.Nullable;
import org.openjst.server.commons.network.Actor;

/**
 * @author Sergey Grachev
 */
public final class SimpleActorObj implements Actor<Long> {

    private final Long id;
    private final String authId;

    public SimpleActorObj(final Long id, final String authId) {
        this.id = id;
        this.authId = authId;
    }

    public SimpleActorObj(final Long id) {
        this(id, null);
    }

    public Long getId() {
        return id;
    }

    @Nullable
    public String getAuthId() {
        return authId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || !Actor.class.isAssignableFrom(o.getClass())) return false;
        final Actor that = (Actor) o;
        return !(id != null ? !id.equals(that.getId()) : that.getId() != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SimpleActorObj{" +
                "id=" + id +
                ", authId='" + authId + '\'' +
                '}';
    }
}
