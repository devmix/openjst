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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openjst.commons.conversion.units.TimeUnits;
import org.openjst.commons.conversion.units.Units;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.workflow.Engine;
import org.openjst.commons.workflow.State;
import org.openjst.commons.workflow.UserData;
import org.openjst.commons.workflow.WorkflowData;
import org.openjst.commons.workflow.actions.Action;
import org.openjst.commons.workflow.exceptions.WorkflowException;
import org.openjst.commons.workflow.persistence.Persistence;
import org.openjst.commons.workflow.process.Process;
import org.openjst.commons.workflow.process.ProcessState;
import org.openjst.commons.workflow.tasks.Task;
import org.openjst.commons.workflow.tasks.Tasks;
import org.openjst.commons.workflow.timers.Timer;
import org.openjst.commons.workflow.timers.Timers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sergey Grachev
 */
final class GenericEngine implements Engine {

    private static final long serialVersionUID = 3964876157563442025L;
    private static final Logger LOG = LoggerFactory.getLogger(GenericEngine.class);

    // engine state
    private final Object lockEngineRestart = new Object();
    private final AtomicBoolean stopped = new AtomicBoolean(true);
    //
    private final Object noWorkflow = new Object();
    private final Persistence persistence;
    private final Map<String, org.openjst.commons.workflow.process.Process> processes;
    private final WorkflowQueue queue;
    private final WorkflowResources resources;
    private final int threadsCount;
    private final int keepAliveTime;
    private ThreadPoolExecutor executor;

    public GenericEngine(final int threadsCount, final int keepAliveTime, final Persistence persistence) {
        this.keepAliveTime = keepAliveTime;
        this.threadsCount = threadsCount;
        this.persistence = persistence;
        this.processes = new LinkedHashMap<String, Process>();
        this.queue = new WorkflowQueue(persistence);
        this.resources = new WorkflowResources();
    }

    public void register(final String name, final Process process) {
        synchronized (processes) {
            processes.put(name, process);
        }
    }

    @Override
    public void event(final String workflowId, final String eventName, final UserData eventData) throws WorkflowException {
        queue.addEvent(workflowId, eventName, eventData);
        synchronized (noWorkflow) {
            noWorkflow.notify();
        }
    }

    @Override
    public void abort(final String workflowId) throws WorkflowException {
        queue.abort(workflowId);
    }

    public String execute(final String name, final UserData data) throws WorkflowException {
        LOG.trace("Execute workflow {}, data {}", name, data);

        final Process handler = processes.get(name);
        if (handler == null) {
            throw WorkflowException.newHandlerNotFound(name);
        }

        try {

            final WorkflowContext workflow = new GenericWorkflowContext(name, data, persistence)
                    .setState(State.SCHEDULED).persist()
                    .scheduleTask(Tasks.newStart());

            synchronized (queue) {
                queue.add(workflow);
            }

            synchronized (noWorkflow) {
                noWorkflow.notify();
            }

            return workflow.getId();
        } catch (Exception e) {
            throw WorkflowException.newNested(e);
        }
    }

    public void start() throws WorkflowException {
        LOG.info("Start workflow engine");

        synchronized (lockEngineRestart) {
            if (!stopped.get()) {
                return;
            }

            executor = new ThreadPoolExecutor( // +1 for queue balancing
                    threadsCount + 1, threadsCount + 1, 0, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(threadsCount), new DefaultThreadFactory()
            );

            stopped.set(false);

            executor.execute(new QueueBalancingRunnable());

            for (int i = 0; i < threadsCount; i++) {
                executor.execute(new WorkflowRunnable());
            }
        }
    }

    public void shutdown() {
        LOG.info("Shutdown workflow engine");

        synchronized (lockEngineRestart) {
            if (!stopped.get()) {
                executor.shutdownNow();
            }

            queue.clear();

            stopped.set(true);
        }
    }

    @Override
    public void shutdown(final long timeout, final TimeUnits unit) throws InterruptedException {
        LOG.info("Shutdown workflow engine");

        synchronized (lockEngineRestart) {
            if (!stopped.get()) {
                executor.shutdownNow();
            }

            queue.clear();

            if (executor != null) {
                executor.awaitTermination((long) unit.convert(timeout, TimeUnits.MILLISECOND), TimeUnit.MILLISECONDS);
            }

            stopped.set(true);
        }
    }

    @Override
    public boolean isStarted() {
        return !stopped.get();
    }

    @Nullable
    private ProcessState executeProcess(final Process process, final WorkflowContext workflow) {
        LOG.trace("\tProcess {}", workflow);

        // high priority
        final TaskContext nextTask = workflow.nextTask();
        if (nextTask != null) {
            return executeTask(process, workflow, nextTask);
        }

        // medium priority
        final EventContext nextEvent = workflow.nextEvent();
        if (nextEvent != null) {
            return executeEvent(process, workflow, nextEvent);
        }

        // low priority
        final TimerContext nextTimer = workflow.nextTimer();
        if (nextTimer != null) {
            return executeTimer(process, workflow, nextTimer);
        }

        return null;
    }

    @Nullable
    private ProcessState executeTimer(final Process process, final WorkflowContext workflow, final TimerContext timer) {
        final ProcessState result = process.onTimer(workflow, timer);

        if (!doActions(workflow, null, result.getActions())) { // not all resources available
            LOG.trace("\nNot all resources available for actions [{}]", result);
            return null;
        }

        final TimerContext finished = workflow.finishTimer();

        assert finished.getId().equals(timer.getId());

        // reschedule
        if (Timers.RECURRENT.equals(timer.getType())) {
            LOG.trace("\tReschedule {}", timer);
            workflow.scheduleTimer(Timers.newRecurrent(timer.getName(), timer.getInterval(), TimeUnits.MILLISECOND));
        }

        return result;
    }

    private ProcessState executeEvent(final Process process, final WorkflowContext workflow, final EventContext event) {
        final ProcessState result = process.onEvent(workflow, event);

        if (!doActions(workflow, event.getData(), result.getActions())) { // not all resources available
            return null;
        }

        final EventContext finished = workflow.finishEvent();

        assert finished.getId().equals(event.getId());

        return result;
    }

    private ProcessState executeTask(final Process process, final WorkflowContext workflow, final TaskContext task) {
        final ProcessState result;
        switch (task.getType()) {
            case START:
                result = process.onStart(workflow, task);
                break;

            case END:
                result = process.onEnd(workflow, task);
                break;

            default:
                result = process.onTask(workflow, task);
        }

        if (!doActions(workflow, null, result.getActions())) { // not all resources available
            return null;
        }

        final TaskContext finishedTask = workflow.finishTask();

        assert finishedTask.getId().equals(task.getId());

        if (Tasks.END.equals(task.getType())) {
            workflow.setState(State.FINISHED);
        }

        return result;
    }

    private boolean doActions(final WorkflowContext workflow, @Nullable final UserData eventData, @Nullable final List<Class<? extends Action>> actions) {
        if (actions == null) {
            return true;
        }

        final String workflowId = workflow.getId();

        final Pair<Boolean, List<Action>> lock;
        try {
            lock = resources.lock(workflowId, actions);
            if (!lock.first()) {
                LOG.trace("\tCan't lock workflow: #{}", workflowId);
                return false;
            }

            try {
                for (final Action actionInstance : lock.second()) {
                    try {
                        actionInstance.onExecute(this, workflow.getData(), eventData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                resources.unlock(workflowId);
            }

            return true;
        } catch (WorkflowException e) {
            LOG.error("\tError while execute actions", e);
        }

        return false;
    }

    private void doPostProcess(final WorkflowContext workflow, @Nullable final Task nextTask,
                               final List<Timer> startTimers, final List<Timer> stopTimers) {

        if (nextTask != null) {
            workflow.scheduleTask(nextTask);
        }

        if (startTimers != null) {
            for (final Timer timer : startTimers) {
                workflow.scheduleTimer(timer);
            }
        }

        if (stopTimers != null) {
            for (final Timer timer : stopTimers) {
                workflow.stopTimer(timer.getName());
            }
        }
    }

    private final class WorkflowRunnable implements Runnable {

        private final Thread currentThread;

        public WorkflowRunnable() {
            this.currentThread = Thread.currentThread();
        }

        @Override
        public void run() {
            LOG.trace("Start WorkflowRunnable");

            while (!currentThread.isInterrupted() && currentThread.isAlive() && !stopped.get()) {

                final WorkflowContext workflow = queue.acquireNext(keepAliveTime);
                if (workflow == null) {
                    try {
                        final long delay = queue.delayToNearestTimer();
                        if (delay > 0) {
                            LOG.trace("\tNo available workflow, wait {} milliseconds", delay);
                            synchronized (noWorkflow) {
                                noWorkflow.wait(delay);
                            }
                        }
                    } catch (InterruptedException ignore) {
                        LOG.warn("Thread interrupted");
                    }
                    continue;
                }

                final Process process;
                synchronized (processes) {
                    process = processes.get(workflow.getName());
                }

                assert process != null;

                try {
                    final ProcessState processState = executeProcess(process, workflow);
                    if (processState != null) {
                        doPostProcess(workflow, processState.getTask(), processState.getStartTimers(), processState.getStopTimers());
                    }
                    Thread.yield();
                } finally {
                    queue.release(workflow);
                }
            }
        }
    }

    private final class QueueBalancingRunnable implements Runnable {

        private final Thread currentThread;

        public QueueBalancingRunnable() {
            this.currentThread = Thread.currentThread();
        }

        @Override
        public void run() {
            LOG.trace("Start QueueBalancingRunnable");

            while (!currentThread.isInterrupted() && currentThread.isAlive() && !stopped.get()) {
                try {

                    final Map<String, WorkflowData> workflows = persistence.loadWorkflows(keepAliveTime * 2, queue.getWorkflowsList());

                    for (final Map.Entry<String, WorkflowData> entry : workflows.entrySet()) {
                        final WorkflowData data = entry.getValue();
                        final WorkflowContext workflowContext = new GenericWorkflowContext(
                                entry.getKey(), data.workflow(), data.tasks(), data.events(), data.timers(), persistence);
                        queue.add(workflowContext);
                        LOG.trace("\tLoaded {}", workflowContext);
                    }

                    queue.unloadCompleted();

                    if (workflows.isEmpty()) {
                        final long nearestTimer = persistence.delayToNearestTimer();
                        if (nearestTimer > -1) {
                            final long delay = Math.max(keepAliveTime, persistence.delayToNearestTimer() - keepAliveTime);
                            LOG.trace("\tWait {} seconds before next pre-loading", Units.convert(delay, TimeUnits.MILLISECOND, TimeUnits.SECOND));
                            Thread.sleep(delay);
                            continue;
                        }

                        LOG.trace("\tNo workflows for pre-loading, wait notification");

                        synchronized (noWorkflow) {
                            noWorkflow.wait();
                        }
                    } else {
                        synchronized (noWorkflow) {
                            if (workflows.size() == 1) {
                                noWorkflow.notify();
                            } else {
                                noWorkflow.notifyAll();
                            }
                        }
                        Thread.sleep(keepAliveTime);
                    }

                } catch (InterruptedException e) {
                    LOG.warn("\tThread interrupted");
                }
            }
        }
    }

    private static final class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger ENGINE_ID = new AtomicInteger(1);
        private final AtomicInteger threadId = new AtomicInteger(1);
        private final ThreadGroup group;
        private final String namePrefix;

        public DefaultThreadFactory() {
            final SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "engine-" + ENGINE_ID.getAndIncrement() + "-";
        }

        @NotNull
        @Override
        public Thread newThread(@NotNull final Runnable runnable) {
            final Thread t = new Thread(group, runnable, namePrefix + threadId.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
