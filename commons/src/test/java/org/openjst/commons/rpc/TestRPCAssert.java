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

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class TestRPCAssert {
    private TestRPCAssert() {
    }

    public static void assertRequestEquals(final RPCRequest request, final RPCRequest expectedRequest) {
        assertThat(request.getId()).isEqualTo(expectedRequest.getId());
        assertThat(request.getObject()).isEqualTo(expectedRequest.getObject());
        assertThat(request.getMethod()).isEqualTo(expectedRequest.getMethod());

        final Object[] parameters = request.getParameters().getValues();
        final Object[] expectedParameters = expectedRequest.getParameters().getValues();
        assertThat(parameters.length).isEqualTo(expectedParameters.length);
    }
}
