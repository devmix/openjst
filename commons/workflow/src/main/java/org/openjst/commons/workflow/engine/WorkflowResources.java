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

import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.dto.tuples.Tuples;
import org.openjst.commons.workflow.actions.Action;
import org.openjst.commons.workflow.actions.Resource;
import org.openjst.commons.workflow.exceptions.WorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Sergey Grachev
 */
final class WorkflowResources {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowResources.class);

    private final Map<String, Lock> context = new LinkedHashMap<String, Lock>();

    public Pair<Boolean, List<Action>> lock(final String workflowId, final List<Class<? extends Action>> actions) throws WorkflowException {
        synchronized (context) {

            // check that there was an attempt to block the resources

            Lock lock = context.get(workflowId);
            if (lock == null) {
                final Set<Resource> resourceList = new HashSet<Resource>();
                final List<Action> actionList = new LinkedList<Action>();
                for (final Class<? extends Action> actionClass : actions) {
                    try {
                        final Action action = actionClass.newInstance();
                        actionList.add(action);
                        resourceList.addAll(action.getRequiredResources());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (resourceList.isEmpty()) {
                    LOG.trace("\tNo resources for locking: {}", workflowId);
                    return Tuples.newPair(Boolean.TRUE, actionList);
                }

                LOG.trace("\tStore resources for future locking: workflow {}, resources {}", workflowId, resourceList);
                context.put(workflowId, lock = new Lock(actionList, resourceList));
            }

            if (lock.locked) {
                return Tuples.newPair(Boolean.FALSE, lock.actions);
            }

            // check that no one is blocking the required resources

            boolean beforeRequired = true;
            for (final Map.Entry<String, Lock> existsLock : context.entrySet()) {
                if (workflowId.equals(existsLock.getKey())) {
                    beforeRequired = false;
                } else {
                    if (beforeRequired) {
                        if (!Collections.disjoint(existsLock.getValue().resources, lock.resources)) {
                            LOG.trace("\tResources wait: {}, resources {}", workflowId, lock.resources);
                            return Tuples.newPair(Boolean.FALSE, lock.actions);
                        }
                    } else {
                        break;
                    }
                }
            }

            LOG.trace("\tResources locked: {}, resources {}", workflowId, lock.resources);

            lock.locked = true;

            return Tuples.newPair(Boolean.TRUE, lock.actions);
        }
    }

    public void unlock(final String workflowId) {
        synchronized (context) {
            context.remove(workflowId);
        }
        LOG.trace("\tUnlock resources of workflow {}", workflowId);
    }

    private static final class Lock {
        public final List<Action> actions;
        public final Set<Resource> resources;
        public boolean locked;

        private Lock(final List<Action> actions, final Set<Resource> resources) {
            this.actions = actions;
            this.resources = resources;
        }
    }
}
