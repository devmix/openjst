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

import org.openjst.commons.workflow.Workflow;
import org.openjst.commons.workflow.WorkflowData;
import org.openjst.commons.workflow.events.Event;
import org.openjst.commons.workflow.tasks.Task;
import org.openjst.commons.workflow.timers.Timer;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * The interface provides storage workflow between restarts.
 *
 * @author Sergey Grachev
 */
public interface Persistence extends Serializable {

    PersistenceTypes getType();

    /**
     * Create a new workflow. Order of creation is not matter.
     *
     * @param workflow data of workflow
     * @return id of created workflow
     */
    String persistWorkflow(Workflow workflow);

    /**
     * Create a new task.
     * <p>
     * <b>Tasks MUST be stored in the same order in which they are created.</b>
     *
     * @param task data of task
     * @return id of created task
     */
    String persistTask(String workflowId, Task task);

    /**
     * Create a new event.
     * <p>
     * <b>Event MUST be stored in the same order in which they are created.</b>
     *
     * @param event data of event
     * @return id of created event
     */
    String persistEvent(String workflowId, Event event);

    /**
     * Create a new timer. Order of creation is not matter.
     *
     * @param workflowId id of timer workflow
     * @param timer      data of timer
     * @return id of created timer
     */
    String persistTimer(String workflowId, Timer timer);

    /**
     * Update a exists workflow
     *
     * @param workflow data of workflow
     */
    void updateWorkflow(String workflowId, Workflow workflow);

    /**
     * Remove finished task
     */
    void removeTask(String workflowId, String taskId);

    /**
     * Remove finished event
     */
    void removeEvent(String workflowId, String eventId);

    /**
     * Remove finished timer. It does not matter a recurrent timer or not. After doing an old timer will be removed
     * and a new created.
     */
    void removeTimer(String workflowId, String timerId);

    /**
     * Get a list of not yet loaded workflow.
     *
     * @param timerLimit if there were only timers, then choose only those to which the remaining specified time interval
     * @param exclude    id workflow that must be excluded, for example already downloaded
     * @return list of loaded workflow
     */
    Map<String, WorkflowData> loadWorkflows(int timerLimit, Set<String> exclude);

    /**
     * Get single workflow
     */
    @Nullable
    WorkflowData loadWorkflow(String workflowId);

    /**
     * Get list of workflow tasks
     * <p>
     * <b>Tasks MUST be loaded in the same order in which they are created.</b>
     */
    @Nullable
    Map<String, Task> loadTasks(String workflowId);

    /**
     * Get list of workflow events
     * <p>
     * <b>Events MUST be loaded in the same order in which they are created.</b>
     */
    @Nullable
    Map<String, Event> loadEvents(String workflowId);

    /**
     * Get list of workflow timers
     */
    @Nullable
    Map<String, Timer> loadTimers(String workflowId);

    /**
     * Delay in milliseconds before the call to the nearest timer
     *
     * @return delay in milliseconds
     */
    long delayToNearestTimer();
}
