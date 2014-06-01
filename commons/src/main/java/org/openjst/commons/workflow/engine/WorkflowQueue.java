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

import org.openjst.commons.concurrent.ResourcesLock;
import org.openjst.commons.workflow.State;
import org.openjst.commons.workflow.UserData;
import org.openjst.commons.workflow.WorkflowData;
import org.openjst.commons.workflow.events.Events;
import org.openjst.commons.workflow.exceptions.WorkflowException;
import org.openjst.commons.workflow.persistence.Persistence;
import org.openjst.commons.workflow.timers.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Sergey Grachev
 */
final class WorkflowQueue {

    private static final Logger LOG = LoggerFactory.getLogger(GenericEngine.class);

    private final ResourcesLock<String> lock = new ResourcesLock<String>();
    private final Map<String, WorkflowContext> queue = new LinkedHashMap<String, WorkflowContext>();
    private final Map<String, WorkflowContext> inProcess = new HashMap<String, WorkflowContext>();
    private final Persistence persistence;

    public WorkflowQueue(final Persistence persistence) {
        this.persistence = persistence;
    }

    public synchronized void add(final WorkflowContext workflow) {
        if (inProcess.containsKey(workflow.getId())) {
            return;
        }
        queue.put(workflow.getId(), workflow);
    }

    public synchronized void addEvent(final String workflowId, final String eventName, final UserData eventData) throws WorkflowException {
        LOG.debug("Event [{}], workflow [{}], data [{}]", eventName, workflowId, eventData);

        lock.lock(workflowId);
        try {
            final WorkflowContext workflowContext = loadWorkflow(workflowId);
            workflowContext.scheduleEvent(Events.newNamed(eventName, eventData));
        } finally {
            lock.unlock(workflowId);
        }
    }

    public synchronized void abort(final String workflowId) throws WorkflowException {
        LOG.debug("Abort workflow [{}]", workflowId);

        final WorkflowContext workflow = loadWorkflow(workflowId);

        lock.lock(workflowId);
        try {
            workflow.setState(State.ABORTED).persist();
        } finally {
            lock.unlock(workflowId);
        }

        if (queue.remove(workflowId) == null) {
            inProcess.remove(workflowId);
        }
    }

    private synchronized WorkflowContext loadWorkflow(final String workflowId) throws WorkflowException {
        WorkflowContext workflowContext;
        workflowContext = queue.get(workflowId);
        if (workflowContext == null) {
            workflowContext = inProcess.get(workflowId);
        }

        if (workflowContext == null) {
            final WorkflowData data = persistence.loadWorkflow(workflowId);
            if (data != null) {
                LOG.trace("Load non exists {}", data);
                workflowContext = new GenericWorkflowContext(
                        workflowId, data.workflow(), data.tasks(), data.events(), data.timers(), persistence);
                queue.put(workflowId, workflowContext);
                return workflowContext;
            }
            throw WorkflowException.newWorkflowNotFound(workflowId);
        }

        return workflowContext;
    }

    @Nullable
    public synchronized WorkflowContext acquireNext(final int keepAliveTime) {
        LOG.trace("Try acquire next workflow");

        final Iterator<WorkflowContext> iterator = queue.values().iterator();
        while (iterator.hasNext()) {
            final WorkflowContext workflow = iterator.next();
            if (workflow.isFinished() || workflow.isAborted()) {
                continue;
            }

            final String workflowId = workflow.getId();
            lock.lock(workflowId);
            try {
                if (!workflow.hasTasks() && !workflow.hasEvents() && !workflow.hasOverdueTimers()) {
                    final TimerContext timer = workflow.nearestTimer();
                    if (timer != null && timer.getPlannedTime() > System.currentTimeMillis() + keepAliveTime * 2) {
                        LOG.trace("Unload {}, timer {}", workflow, timer);
                        iterator.remove();
                    }
                    continue;
                }

                iterator.remove();
                inProcess.put(workflowId, workflow);

                LOG.trace("Run {}", workflow);

                return workflow.setState(State.RUNNING);
            } finally {
                lock.unlock(workflowId);
            }
        }
        return null;
    }

    public synchronized long delayToNearestTimer() {
        long minTime = Long.MAX_VALUE;
        for (final WorkflowContext workflow : queue.values()) {
            final Timer timer = workflow.nearestTimer();
            if (timer != null) {
                minTime = Math.min(minTime, timer.getStartTime() + timer.getInterval());
            }
        }
        return minTime - System.currentTimeMillis();
    }

    public synchronized void release(final WorkflowContext workflow) {
        LOG.trace("\tRelease {}", workflow);

        final String workflowId = workflow.getId();
        lock.lock(workflowId);
        try {
            workflow.persist();

            inProcess.remove(workflowId);

            if (workflow.isCompleted()) {
                LOG.trace("\tRemove completed {}", workflow);
                queue.remove(workflowId);
            } else {
                queue.put(workflowId, workflow);
                workflow.setState(State.WAITING);
            }

        } finally {
            lock.unlock(workflowId);
        }
    }

    public synchronized void unloadCompleted() {
        LOG.trace("Unload completed workflows");

        final List<WorkflowContext> workflows = new LinkedList<WorkflowContext>();
        for (final WorkflowContext workflow : queue.values()) {
            if (workflow.isCompleted()) {
                workflows.add(workflow);
            }
        }

        for (final WorkflowContext workflow : workflows) {
            LOG.trace("Unload {}", workflow);
            final String workflowId = workflow.getId();
            if (queue.remove(workflowId) == null) {
                inProcess.remove(workflowId);
            }
        }
    }

    public synchronized Set<String> getWorkflowsList() {
        final Set<String> result = new HashSet<String>();
        result.addAll(queue.keySet());
        result.addAll(inProcess.keySet());
        return result;
    }

    public synchronized void clear() {
        LOG.debug("Clear queue");
        queue.clear();
        inProcess.clear();
        lock.unlockAll();
    }
}
