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

package org.openjst.commons.workflow.actions;

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.workflow.Engine;
import org.openjst.commons.workflow.UserData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public final class MockActionWithResources3And4 implements Action {

    private static final long serialVersionUID = -498547204418610679L;

    private final Set<Resource> resources;

    public MockActionWithResources3And4() {
        this.resources = new HashSet<Resource>(Arrays.asList(new MockResource(3), new MockResource(4)));
    }

    @Override
    public Set<Resource> getRequiredResources() {
        return resources;
    }

    @Override
    public void onExecute(final Engine engine, final UserData workflowData, @Nullable final UserData eventData) {
    }

    @Override
    public String toString() {
        return "Action{" +
                "resources=" + resources +
                '}';
    }
}
