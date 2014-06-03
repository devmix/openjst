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

import org.openjst.commons.conversion.units.TimeUnits;
import org.openjst.commons.conversion.units.Units;
import org.openjst.commons.workflow.Workflow;
import org.openjst.commons.workflow.WorkflowData;
import org.openjst.commons.workflow.events.Event;
import org.openjst.commons.workflow.tasks.Task;
import org.openjst.commons.workflow.timers.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sergey Grachev
 */
final class MemoryPersistence implements Persistence {

    private static final long serialVersionUID = -2030832324654281171L;
    private static final Logger LOG = LoggerFactory.getLogger(MemoryPersistence.class);
    private static final AtomicLong ID = new AtomicLong(1);

    private final Map<String, Workflow> workflowStore = new HashMap<String, Workflow>();
    private final Map<String, LinkedHashMap<String, Task>> tasksStore = new HashMap<String, LinkedHashMap<String, Task>>();
    private final Map<String, LinkedHashMap<String, Event>> eventsStore = new HashMap<String, LinkedHashMap<String, Event>>();
    private final Map<String, LinkedHashMap<String, Timer>> timersStore = new HashMap<String, LinkedHashMap<String, Timer>>();
    private long nearestTimer = Long.MAX_VALUE;

    @Override
    public PersistenceTypes getType() {
        return PersistenceTypes.MEMORY;
    }

    @Override
    public String persistWorkflow(final Workflow workflow) {
        final String id = String.valueOf(ID.getAndIncrement())/*UUID.randomUUID().toString()*/;
        synchronized (workflowStore) {
            workflowStore.put(id, workflow);
        }
        return id;
    }

    @Override
    public String persistTask(final String workflowId, final Task task) {
        final String id = String.valueOf(ID.getAndIncrement())/*UUID.randomUUID().toString()*/;
        synchronized (tasksStore) {
            LinkedHashMap<String, Task> list = tasksStore.get(workflowId);
            if (list == null) {
                tasksStore.put(workflowId, list = new LinkedHashMap<String, Task>(1));
            }
            list.put(id, task);
        }
        return id;
    }

    @Override
    public String persistEvent(final String workflowId, final Event event) {
        final String id = String.valueOf(ID.getAndIncrement())/*UUID.randomUUID().toString()*/;
        synchronized (eventsStore) {
            LinkedHashMap<String, Event> list = eventsStore.get(workflowId);
            if (list == null) {
                eventsStore.put(workflowId, list = new LinkedHashMap<String, Event>(1));
            }
            list.put(id, event);
        }
        return id;
    }

    @Override
    public String persistTimer(final String workflowId, final Timer timer) {
        final String id = String.valueOf(ID.getAndIncrement())/*UUID.randomUUID().toString()*/;
        synchronized (timersStore) {
            LinkedHashMap<String, Timer> list = timersStore.get(workflowId);
            if (list == null) {
                timersStore.put(workflowId, list = new LinkedHashMap<String, Timer>());
            }
            list.put(id, timer);

            nearestTimer = Math.min(nearestTimer, timer.getStartTime() + timer.getInterval());
        }
        return id;
    }

    @Override
    public void updateWorkflow(final String workflowId, final Workflow workflow) {
        synchronized (workflowStore) {
            workflowStore.put(workflowId, workflow);
        }
    }

    @Override
    public void removeTask(final String workflowId, final String taskId) {
        synchronized (tasksStore) {
            final Map<String, Task> list = tasksStore.get(workflowId);
            if (list == null) {
                return;
            }
            list.remove(taskId);
        }
    }

    @Override
    public void removeEvent(final String workflowId, final String eventId) {
        synchronized (eventsStore) {
            final Map<String, Event> list = eventsStore.get(workflowId);
            if (list == null) {
                return;
            }
            list.remove(eventId);
        }
    }

    @Override
    public void removeTimer(final String workflowId, final String timerId) {
        synchronized (timersStore) {
            final Map<String, Timer> list = timersStore.get(workflowId);
            if (list == null) {
                return;
            }
            list.remove(timerId);

            nearestTimer = getNearestTimerTime();
        }
    }

    private long getNearestTimerTime() {
        long result = Long.MAX_VALUE;
        for (final Map<String, Timer> timers : timersStore.values()) {
            if (timers == null || timers.isEmpty()) {
                continue;
            }
            final List<Timer> sorted = new LinkedList<Timer>(timers.values());
            Collections.sort(sorted, new Comparator<Timer>() {
                @Override
                public int compare(final Timer o1, final Timer o2) {
                    return (int) (o1.getStartTime() - o2.getStartTime());
                }
            });
            final Timer timer = sorted.get(0);
            result = Math.min(result, timer.getStartTime() + timer.getInterval());
        }
        return result;
    }

    @Override
    public Map<String, WorkflowData> loadWorkflows(final int timerLimit, final Set<String> exclude) {
        LOG.debug("\tPre-loading next workflows: horizon {} seconds, exclude {}",
                Units.convert(timerLimit, TimeUnits.MILLISECOND, TimeUnits.SECOND),
                exclude);

        final Map<String, WorkflowData> result = new HashMap<String, WorkflowData>();
        synchronized (workflowStore) {
            for (final Map.Entry<String, Workflow> entry : workflowStore.entrySet()) {
                final String workflowId = entry.getKey();
                final Workflow workflow = entry.getValue();
                if (exclude.contains(workflowId) || workflow.isCompleted()) {
                    continue;
                }
                if (!workflow.hasTasks() && !workflow.hasEvents()) {
                    final Map<String, Timer> timers = timersStore.get(workflowId);
                    if (timers == null || timers.isEmpty()) {
                        continue;
                    }
                    boolean founded = false;
                    for (final Timer timer : timers.values()) {
                        if (timer.getStartTime() + timer.getInterval() < System.currentTimeMillis() + timerLimit) {
                            founded = true;
                            break;
                        }
                    }
                    if (!founded) {
                        continue;
                    }
                }
                result.put(workflowId, new GenericWorkflowData(
                        entry.getValue(), loadTasks(workflowId), loadEvents(workflowId), loadTimers(workflowId)));
            }
        }

        return result;
    }

    @Nullable
    @Override
    public WorkflowData loadWorkflow(final String workflowId) {
        synchronized (workflowStore) {
            final Workflow workflow = workflowStore.get(workflowId);
            return workflow != null
                    ? new GenericWorkflowData(workflow, loadTasks(workflowId), loadEvents(workflowId), loadTimers(workflowId))
                    : null;
        }
    }

    @Nullable
    @Override
    public Map<String, Task> loadTasks(final String workflowId) {
        synchronized (tasksStore) {
            return tasksStore.get(workflowId);
        }
    }

    @Nullable
    @Override
    public Map<String, Timer> loadTimers(final String workflowId) {
        synchronized (timersStore) {
            return timersStore.get(workflowId);
        }
    }

    @Override
    public long delayToNearestTimer() {
        return nearestTimer == Long.MAX_VALUE ? -1 : nearestTimer - System.currentTimeMillis();
    }

    @Nullable
    @Override
    public Map<String, Event> loadEvents(final String workflowId) {
        synchronized (eventsStore) {
            return eventsStore.get(workflowId);
        }
    }

    @Override
    public String toString() {
        return "MemoryPersistence{" +
                "workflowStore=" + workflowStore +
                ", tasksStore=" + tasksStore +
                ", eventsStore=" + eventsStore +
                ", timersStore=" + timersStore +
                '}';
    }
}
