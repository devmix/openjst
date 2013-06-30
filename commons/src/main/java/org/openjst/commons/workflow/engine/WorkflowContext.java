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

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.workflow.State;
import org.openjst.commons.workflow.Workflow;
import org.openjst.commons.workflow.events.Event;
import org.openjst.commons.workflow.tasks.Task;
import org.openjst.commons.workflow.timers.Timer;

/**
 * @author Sergey Grachev
 */
interface WorkflowContext extends Workflow {

    String getId();

    WorkflowContext setState(State state);

    WorkflowContext scheduleTask(Task task);

    WorkflowContext scheduleEvent(Event event);

    WorkflowContext scheduleTimer(Timer timer);

    void stopTimer(String timerName);

    @Nullable
    TaskContext nextTask();

    @Nullable
    EventContext nextEvent();

    @Nullable
    TimerContext nextTimer();

    @Nullable
    TimerContext nearestTimer();

    TaskContext finishTask();

    EventContext finishEvent();

    TimerContext finishTimer();

    WorkflowContext persist();
}
