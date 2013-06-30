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

package org.openjst.commons.workflow.process;

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.workflow.actions.Action;
import org.openjst.commons.workflow.tasks.Task;
import org.openjst.commons.workflow.timers.Timer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class MutableState implements ProcessState {

    private static final long serialVersionUID = 8304622008962954190L;

    private List<Class<? extends Action>> actions;
    private Task task;
    private final List<Timer> startTimers = new ArrayList<Timer>(0);
    private final List<Timer> stopTimers = new ArrayList<Timer>(0);

    @Override
    public List<Class<? extends Action>> getActions() {
        return actions;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public List<Timer> getStartTimers() {
        return startTimers;
    }

    @Nullable
    @Override
    public List<Timer> getStopTimers() {
        return stopTimers;
    }

    public MutableState setNext(final Task nextTask) {
        this.task = nextTask;
        return this;
    }

    public MutableState addTimer(final Timer timer) {
        startTimers.add(timer);
        return this;
    }

    public ProcessState addAction(final Class<? extends Action> actionClass) {
        if (actions == null) {
            actions = new LinkedList<Class<? extends Action>>();
        }
        actions.add(actionClass);
        return this;
    }

    @Override
    public String toString() {
        return "State{" +
                "actions=" + actions +
                ", task=" + task +
                ", startTimers=" + startTimers +
                ", stopTimers=" + stopTimers +
                '}';
    }
}
