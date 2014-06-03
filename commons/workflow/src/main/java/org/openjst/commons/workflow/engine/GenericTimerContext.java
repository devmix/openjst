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

import org.openjst.commons.workflow.timers.Timers;

import java.util.Date;

/**
 * @author Sergey Grachev
 */
final class GenericTimerContext implements TimerContext {

    private static final long serialVersionUID = 7409285472316205396L;

    private final String id;
    private final String name;
    private final Timers type;
    private final long startTime;
    private final long interval;
    private final long plannedTime;
    private final long order;

    public GenericTimerContext(final String id, final String name, final Timers type, final long startTime, final long interval) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.startTime = startTime;
        this.interval = interval;
        this.plannedTime = startTime + interval;
        this.order = System.nanoTime();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getPlannedTime() {
        return plannedTime;
    }

    @Override
    public long getOrder() {
        return order;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Timers getType() {
        return type;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return "TimerContext{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", startTime=" + new Date(startTime) +
                ", interval=" + interval +
                ", plannedTime=" + new Date(plannedTime) +
                ", order=" + order +
                '}';
    }
}
