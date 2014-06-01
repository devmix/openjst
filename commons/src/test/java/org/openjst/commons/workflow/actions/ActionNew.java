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

import org.openjst.commons.workflow.Engine;
import org.openjst.commons.workflow.UserData;
import org.openjst.commons.workflow.WorkflowEngineTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sergey Grachev
 */
public class ActionNew implements Action {

    private static final long serialVersionUID = 126932718574953802L;
    private static final Logger LOG = LoggerFactory.getLogger(ActionNew.class);
    private static final AtomicLong ID = new AtomicLong(1);

    @Override
    public Set<Resource> getRequiredResources() {
        final Set<Resource> result = new HashSet<Resource>(3);
//        result.add(new ResourceGeneric(String.valueOf(WorkflowEngineTest.RANDOM.nextInt(10))));
//        result.add(new ResourceGeneric(String.valueOf(WorkflowEngineTest.RANDOM.nextInt(10))));
//        result.add(new ResourceGeneric(String.valueOf(WorkflowEngineTest.RANDOM.nextInt(10))));
        result.add(new ResourceGeneric(String.valueOf(1)));
        result.add(new ResourceGeneric(String.valueOf(WorkflowEngineTest.RANDOM.nextInt(10))));
        return result;
    }

    @Override
    public void onExecute(final Engine engine, final UserData workflowData, @Nullable final UserData eventData) {
        LOG.trace("\tExecute ActionNew {}: workflow {}, event {}", ID.getAndIncrement(), workflowData, eventData);
    }
}
