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

package org.openjst.commons.workflow.persistence;

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.workflow.Workflow;
import org.openjst.commons.workflow.WorkflowData;
import org.openjst.commons.workflow.events.Event;
import org.openjst.commons.workflow.tasks.Task;
import org.openjst.commons.workflow.timers.Timer;

import java.util.Collections;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
final class GenericWorkflowData implements WorkflowData {

    private final Workflow workflow;
    private final Map<String, Task> tasks;
    private final Map<String, Event> events;
    private final Map<String, Timer> timers;

    public GenericWorkflowData(final Workflow workflow,
                               @Nullable final Map<String, Task> tasks,
                               @Nullable final Map<String, Event> events,
                               @Nullable final Map<String, Timer> timers) {
        this.workflow = workflow;
        this.tasks = tasks == null ? Collections.<String, Task>emptyMap() : tasks;
        this.events = events == null ? Collections.<String, Event>emptyMap() : events;
        this.timers = timers == null ? Collections.<String, Timer>emptyMap() : timers;
    }

    @Override
    public Workflow workflow() {
        return workflow;
    }

    @Override
    public Map<String, Task> tasks() {
        return tasks;
    }

    @Override
    public Map<String, Event> events() {
        return events;
    }

    @Override
    public Map<String, Timer> timers() {
        return timers;
    }

    @Override
    public String toString() {
        return "WorkflowData{" +
                "workflow=" + workflow +
                ", tasks=" + tasks +
                ", events=" + events +
                ", timers=" + timers +
                '}';
    }
}
