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

package org.openjst.commons.workflow.exceptions;

import org.openjst.commons.workflow.actions.Resource;

import java.util.Set;

/**
 * @author Sergey Grachev
 */
public class WorkflowException extends Exception {
    private static final long serialVersionUID = 8646339660896444166L;

    public WorkflowException(final String message) {
        super(message);
    }

    public WorkflowException(final Throwable cause) {
        super(cause);
    }

    public static WorkflowException newHandlerNotFound(final String workflowName) {
        return new WorkflowException(String.format("Handler for workflow '%s' not found", workflowName));
    }

    public static WorkflowException newNested(final Exception e) {
        return new WorkflowException(e);
    }

    public static WorkflowException newWorkflowNotFound(final String workflowId) {
        return new WorkflowException(String.format("Workflow '%s' not found", workflowId));
    }

    public static WorkflowException newAlreadyWaitResourcesLock(final String workflowId, final Set<Resource> resources) {
        return new WorkflowException(String.format("Workflow '%s' already wait resources [%s] lock", workflowId, resources));
    }

    public static WorkflowException newEngineAlreadyStarted() {
        return new WorkflowException(String.format("Workflow engine already started"));
    }
}
