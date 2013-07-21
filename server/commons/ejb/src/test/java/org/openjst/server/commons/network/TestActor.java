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

package org.openjst.server.commons.network;

/**
 * @author Sergey Grachev
 */
public final class TestActor implements Actor<Long> {

    private final Long id;
    private final String accountId;
    private final String clientId;

    public TestActor(final Long id, final String accountId, final String clientId) {
        this.id = id;
        this.accountId = accountId;
        this.clientId = clientId;
    }

    public TestActor(final Long id) {
        this(id, null, null);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TestActor testActor = (TestActor) o;
        return !(id != null ? !id.equals(testActor.id) : testActor.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TestActor{" +
                "id=" + id +
                ", accountId='" + accountId + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}
