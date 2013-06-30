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

import java.util.Collections;
import java.util.List;

/**
 * @author Sergey Grachev
 */
final class EmptyState implements ProcessState {

    private static final long serialVersionUID = -3133779981888454804L;

    @Override
    public List<Class<? extends Action>> getActions() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public Task getTask() {
        return null;
    }

    @Override
    public List<Timer> getStartTimers() {
        return Collections.emptyList();
    }

    @Override
    public List<Timer> getStopTimers() {
        return Collections.emptyList();
    }
}
