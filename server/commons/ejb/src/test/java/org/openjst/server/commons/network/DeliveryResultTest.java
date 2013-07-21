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

import org.openjst.server.commons.model.types.MessageDeliveryState;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class DeliveryResultTest {

    @Test(groups = "unit")
    public void test() {
        assertThat(DeliveryResult.ok().isSuccess()).isTrue();
        assertThat(DeliveryResult.as(MessageDeliveryState.OK, "none").isSuccess()).isTrue();
        assertThat(DeliveryResult.as(MessageDeliveryState.NO_RESPONSE, null).isSuccess()).isFalse();
        assertThat(DeliveryResult.as(MessageDeliveryState.NO_RESPONSE, "test").getMessage()).isEqualToIgnoringCase("test");
    }
}
