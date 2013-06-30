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
import org.openjst.commons.workflow.actions.ActionNew;
import org.openjst.commons.workflow.engine.EngineFactory;
import org.openjst.commons.workflow.events.Event;
import org.openjst.commons.workflow.exceptions.WorkflowException;
import org.openjst.commons.workflow.persistence.PersistenceTypes;
import org.openjst.commons.workflow.process.ProcessState;
import org.openjst.commons.workflow.process.ProcessStates;
import org.openjst.commons.workflow.tasks.Task;
import org.openjst.commons.workflow.tasks.Tasks;
import org.openjst.commons.workflow.timers.Timer;
import org.openjst.commons.workflow.timers.Timers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Random;

/**
 * @author Sergey Grachev
 */
public final class WorkflowEngineTest {

    public static final Logger LOG = LoggerFactory.getLogger(WorkflowEngineTest.class);
    public static final Random RANDOM = new Random(System.currentTimeMillis());
    public static final int THREADS = 2;
//    public static final AtomicLong FINISHED = new AtomicLong();

    private static final String JOB_WORKFLOW = "job-workflow";
    // states
    private static final String STATE_ASSIGN = "assign";
    // properties
    private static final String PROPERTY_STATE = "state";
    // events
    private static final String EVENT_CHANGE_STATE = "event-change-state";
    // startTimers
    private static final String TIMER_CONFIRM = "timer-confirm";
    private static final String TIMER_ACCEPT = "timer-accept";
    // tasks
    private static final String TASK_NEW = "task-new";
    private static final String TASK_ASSIGN = "task-assign";
    private static final String TASK_CONFIRM = "task-confirm";
    private static final String TASK_ACCEPT = "task-accept";

    @Test(groups = "manual")
    public void test() throws InterruptedException, WorkflowException {
        final Engine engine = EngineFactory.newInstance(THREADS, 1000, PersistenceTypes.newMemory());

        engine.register(JOB_WORKFLOW, new org.openjst.commons.workflow.process.Process() {

            @Override
            public ProcessState onStart(final Workflow workflow, final Task task) {
                LOG.info("\tStart: {}", task);

                return ProcessStates.newMutableState()
                        .setNext(Tasks.newNamed(TASK_NEW))
                        .addAction(ActionNew.class);
            }

            @Override
            public ProcessState onTimer(final Workflow workflow, final Timer timer) {
                LOG.info("\tTimer: {}", timer);

                if (TIMER_CONFIRM.equals(timer.getName())) {

                    return ProcessStates.newMutableState().setNext(Tasks.newNamed(TASK_CONFIRM));

                } else if (TIMER_ACCEPT.equals(timer.getName())) {

                    return ProcessStates.newMutableState().setNext(Tasks.newNamed(TASK_ACCEPT));

                } else if ("timer-finish".equals(timer.getName())) {

                    return ProcessStates.newMutableState().setNext(Tasks.end());

                }

                return ProcessStates.empty();
            }

            @Override
            public ProcessState onEvent(final Workflow workflow, final Event event) {
                LOG.info("\tEvent: {}", event);

                final UserData data = event.getData();

                if (EVENT_CHANGE_STATE.equals(event.getName()) && data != null) {

                    final String newState = (String) data.property(PROPERTY_STATE);
                    LOG.info("\t\tchange state on: {}", newState);
                    return ProcessStates.newMutableState().setNext(Tasks.newNamed("task-" + newState));

                }

                return ProcessStates.empty();
            }

            @Override
            public ProcessState onTask(final Workflow workflow, final Task task) {
                LOG.info("\tTask: name: {}, type: {}, id: {}",
                        task.getName(), task.getType(), task);

                if (TASK_NEW.equals(task.getName())) {

                    // after start

                } else if (TASK_ASSIGN.equals(task.getName())) {

                    return ProcessStates.newMutableState()
                            .setNext(Tasks.newNamed(TASK_CONFIRM))
                            /*.withTimer(Timers.newSingle(TIMER_CONFIRM, 2, TimeUnits.SECOND))*/;

                } else if (TASK_CONFIRM.equals(task.getName())) {

                    return ProcessStates.newMutableState()
                            .addTimer(Timers.newOneTime(TIMER_ACCEPT, 1, TimeUnits.SECOND))
//                            .addTimer(Timers.newRepeat("test-repeat-every-4", 4, TimeUnits.SECOND))
//                            .addTimer(Timers.newRepeat("test-repeat-every-2", 2, TimeUnits.SECOND))
                            .addTimer(Timers.newOneTime("timer-finish", 10, TimeUnits.SECOND))
                            ;

                }

                return ProcessStates.empty();
            }

            @Override
            public ProcessState onEnd(final Workflow workflow, final Task task) {
                LOG.info("\tEnd: id: {}", task);

//                final long count = FINISHED.incrementAndGet();
//                if (count % 10000 == 0) {
//                    System.out.println(count);
//                }

                return ProcessStates.empty();
            }
        });

//        final long count = 100000;
//        final long now = System.currentTimeMillis();
//        for (int i = 0; i < count; i++) {
//            final String id = engine.execute(JOB_WORKFLOW, (UserData) new UserDataHashMap().withProperty("id", now + i));
//            engine.event(id, EVENT_CHANGE_STATE, (UserData) new UserDataHashMap().withProperty(PROPERTY_STATE, STATE_ASSIGN));
//        }
//        System.out.println("Events " + count);

//        final String workflow1 = engine.execute(JOB_WORKFLOW, (UserData) new UserDataHashMap().withProperty("id", 1L));

//        final long count = 10;
//        final long now = System.currentTimeMillis();
//        for (int i = 0; i < count; i++) {
//            final String id = engine.execute(JOB_WORKFLOW, (UserData) new UserDataHashMap().withProperty("id", now + i));
//            engine.event(id, EVENT_CHANGE_STATE, (UserData) new UserDataHashMap().withProperty(PROPERTY_STATE, STATE_ASSIGN));
//        }
//        engine.shutdown(1, TimeUnits.YEAR);
        engine.start();

//        engine.event(workflow1, EVENT_CHANGE_STATE, (UserData) new UserDataHashMap()
//                .withProperty(PROPERTY_STATE, STATE_ASSIGN)
//                .withProperty("rnd", RANDOM.nextLong()));
//
//        final String workflow2 = engine.execute(JOB_WORKFLOW, (UserData) new UserDataHashMap().withProperty("id", 2L));
//        engine.event(workflow2, EVENT_CHANGE_STATE, (UserData) new UserDataHashMap()
//                .withProperty(PROPERTY_STATE, STATE_ASSIGN)
//                .withProperty("rnd", RANDOM.nextLong()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                final long count = 10;
                final long now = System.currentTimeMillis();
                for (int i = 0; i < count; i++) {
                    final String id;
                    try {
                        id = engine.execute(JOB_WORKFLOW, (UserData) new UserDataHashMap().withProperty("id", now + i));
                        engine.event(id, EVENT_CHANGE_STATE, (UserData) new UserDataHashMap()
                                .withProperty(PROPERTY_STATE, STATE_ASSIGN)
                                .withProperty("rnd", RANDOM.nextLong()));
                    } catch (WorkflowException e) {
                        e.printStackTrace();
                    }
                }
                LOG.info("Events {}", count);
            }
        }).start();

        Thread.yield();
        Thread.sleep(Integer.MAX_VALUE);
        engine.shutdown();
    }
}
