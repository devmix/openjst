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
import org.openjst.commons.workflow.UserData;
import org.openjst.commons.workflow.UserDataHashMap;
import org.openjst.commons.workflow.Workflow;
import org.openjst.commons.workflow.events.Event;
import org.openjst.commons.workflow.persistence.Persistence;
import org.openjst.commons.workflow.tasks.Task;
import org.openjst.commons.workflow.timers.Timer;

import java.util.*;

/**
 * @author Sergey Grachev
 */
final class GenericWorkflowContext implements WorkflowContext {

    private static final long serialVersionUID = 7688051737360716217L;

    private static final Comparator<TimerContext> TIMER_COMPARATOR = new Comparator<TimerContext>() {
        @Override
        public int compare(final TimerContext o1, final TimerContext o2) {
            return o1.getPlannedTime() != o2.getPlannedTime()
                    ? (int) ((o1.getPlannedTime()) - (o2.getPlannedTime()))
                    : (int) ((o1.getOrder()) - (o2.getOrder()));
        }
    };

    private final List<TimerContext> timers = new LinkedList<TimerContext>();
    private final Queue<EventContext> events = new LinkedList<EventContext>();
    private final Queue<TaskContext> tasks = new LinkedList<TaskContext>();
    private final UserData userData;
    private final Persistence persistence;
    private String id;
    private final String name;
    private State state;

    public GenericWorkflowContext(final String name, @Nullable final UserData data, final Persistence persistence) {
        this.name = name;
        this.userData = data == null ? new UserDataHashMap() : data;
        this.persistence = persistence;
    }

    public GenericWorkflowContext(final String id, final Workflow workflow, final Map<String, Task> tasks,
                                  final Map<String, Event> events, final Map<String, Timer> timers,
                                  final Persistence persistence) {
        this.id = id;
        this.name = workflow.getName();
        this.userData = workflow.getData();
        this.persistence = persistence;
        this.state = workflow.getState();

        if (!tasks.isEmpty()) {
            for (final Map.Entry<String, Task> entry : tasks.entrySet()) {
                final Task task = entry.getValue();
                this.tasks.add(new GenericTaskContext(entry.getKey(), task.getType(), task.getName()));
            }
        }

        if (!events.isEmpty()) {
            for (final Map.Entry<String, Event> entry : events.entrySet()) {
                final Event event = entry.getValue();
                this.events.add(new GenericEventContext(entry.getKey(), event.getName(), event.getData()));
            }
        }

        if (!timers.isEmpty()) {
            for (final Map.Entry<String, Timer> entry : timers.entrySet()) {
                final Timer timer = entry.getValue();
                this.timers.add(new GenericTimerContext(entry.getKey(), timer.getName(), timer.getType(), timer.getStartTime(), timer.getInterval()));
            }
            Collections.sort(this.timers, TIMER_COMPARATOR);
        }
    }

    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public WorkflowContext setState(final State state) {
        this.state = state;
        return this;
    }

    @Nullable
    @Override
    public UserData getData() {
        return userData;
    }

    @Override
    public boolean isFinished() {
        return State.FINISHED.equals(state);
    }

    @Override
    public boolean isAborted() {
        return State.ABORTED.equals(state);
    }

    @Override
    public boolean isFailed() {
        return State.FAILED.equals(state);
    }

    @Override
    public boolean isCompleted() {
        return State.FINISHED.equals(state) || State.ABORTED.equals(state) || State.FAILED.equals(state);
    }

    @Override
    public WorkflowContext scheduleTask(final Task task) {
        final String newId = persistence.persistTask(id, task);
        synchronized (tasks) {
            tasks.add(new GenericTaskContext(newId, task.getType(), task.getName()));
        }
        return this;
    }

    @Override
    public WorkflowContext scheduleEvent(final Event event) {
        final String newId = persistence.persistEvent(id, event);
        synchronized (events) {
            events.add(new GenericEventContext(newId, event.getName(), event.getData()));
        }
        return this;
    }

    @Override
    public WorkflowContext scheduleTimer(final Timer timer) {
        final String newId = persistence.persistTimer(id, timer);
        synchronized (timers) {
            timers.add(new GenericTimerContext(newId, timer.getName(), timer.getType(), timer.getStartTime(), timer.getInterval()));
            Collections.sort(timers, TIMER_COMPARATOR);
        }
        return this;
    }

    @Override
    public void stopTimer(final String timerName) {
        String timerId = null;
        synchronized (timers) {
            final Iterator<TimerContext> iterator = timers.iterator();
            while (iterator.hasNext()) {
                final TimerContext existsTimer = iterator.next();
                if (existsTimer.getName().equals(timerName)) {
                    timerId = existsTimer.getId();
                    iterator.remove();
                    break;
                }
            }
        }
        if (timerId != null) {
            persistence.removeTimer(id, timerId);
        }
    }

    @Override
    public WorkflowContext persist() {
        if (this.id == null) {
            this.id = persistence.persistWorkflow(this);
        } else {
            persistence.updateWorkflow(this.id, this);
        }
        return this;
    }

    @Nullable
    @Override
    public TaskContext nextTask() {
        synchronized (tasks) {
            return tasks.peek();
        }
    }

    @Nullable
    @Override
    public EventContext nextEvent() {
        synchronized (events) {
            return events.peek();
        }
    }

    @Nullable
    @Override
    public TimerContext nextTimer() {
        synchronized (timers) {
            for (final TimerContext timer : timers) {
                if (System.currentTimeMillis() >= timer.getPlannedTime()) {
                    return timer;
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public TimerContext nearestTimer() {
        synchronized (timers) {
            return timers.isEmpty() ? null : timers.get(0);
        }
    }

    @Override
    public TaskContext finishTask() {
        final TaskContext task = tasks.remove();
        persistence.removeTask(this.id, task.getId());
        return task;
    }

    @Override
    public EventContext finishEvent() {
        final EventContext event = events.remove();
        persistence.removeEvent(this.id, event.getId());
        return event;
    }

    @Override
    public TimerContext finishTimer() {
        final TimerContext timer;
        synchronized (timers) {
            timer = timers.remove(0);
        }
        persistence.removeTimer(this.id, timer.getId());
        return timer;

    }

    @Override
    public boolean hasTasks() {
        synchronized (tasks) {
            return !tasks.isEmpty();
        }
    }

    @Override
    public boolean hasEvents() {
        synchronized (events) {
            return !events.isEmpty();
        }
    }

    @Override
    public boolean hasOverdueTimers() {
        return nextTimer() != null;
    }

    public State getState() {
        return state;
    }

    @Override
    public String toString() {
        return "Workflow{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", userData=" + userData +
                '}';
    }
}
