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

package org.openjst.commons.workflow.engine;

import org.openjst.commons.workflow.actions.Action;
import org.openjst.commons.workflow.actions.MockActionWithResources1And2;
import org.openjst.commons.workflow.actions.MockActionWithResources2And4;
import org.openjst.commons.workflow.actions.MockActionWithResources3And4;
import org.openjst.commons.workflow.exceptions.WorkflowException;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class WorkflowResourcesTest {

    @SuppressWarnings("unchecked")
    @Test(groups = "unit")
    public void lockTest() throws WorkflowException {
        final WorkflowResources resources = new WorkflowResources();

        assertThat(resources.lock("1", Arrays.<Class<? extends Action>>asList(MockActionWithResources1And2.class)).first())
                .isTrue();

        assertThat(resources.lock("1", Arrays.<Class<? extends Action>>asList(MockActionWithResources1And2.class)).first())
                .isFalse();

        assertThat(resources.lock("2", Arrays.<Class<? extends Action>>asList(MockActionWithResources3And4.class)).first())
                .isTrue();

        assertThat(resources.lock("2", Arrays.<Class<? extends Action>>asList(MockActionWithResources3And4.class)).first())
                .isFalse();

        assertThat(resources.lock("3", Arrays.<Class<? extends Action>>asList(MockActionWithResources2And4.class)).first())
                .isFalse();

        resources.unlock("1");

        assertThat(resources.lock("1", Arrays.<Class<? extends Action>>asList(MockActionWithResources1And2.class)).first())
                .isFalse();

        resources.unlock("3");

        assertThat(resources.lock("1", Arrays.<Class<? extends Action>>asList(MockActionWithResources1And2.class)).first())
                .isTrue();
    }
}
