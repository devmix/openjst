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

package org.openjst.commons.workflow;

import org.openjst.commons.conversion.units.TimeUnits;
import org.openjst.commons.workflow.exceptions.WorkflowException;

import java.io.Serializable;

/**
 * @author Sergey Grachev
 */
public interface Engine extends Serializable {

    void start() throws WorkflowException;

    void shutdown();

    void shutdown(long timeout, TimeUnits unit) throws InterruptedException;

    boolean isStarted();

    void register(String name, org.openjst.commons.workflow.process.Process process);

    String execute(String name, UserData data) throws WorkflowException;

    void event(String workflowId, String eventName, UserData eventData) throws WorkflowException;

    void abort(String workflowId) throws WorkflowException;
}
