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

package org.openjst.server.commons.network.producer;

import org.openjst.server.commons.network.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sergey Grachev
 */
final class GenericProducer<P extends Persistence<A, M>, A extends Actor<?>, M extends Message<?>> implements MessageProducer<P, A, M> {

    private final P persistence;
    private final Router<A, M> router;
    private final int threadsCount;
    private final ActorsType actorsType;
    private final Object lockStartStop = new Object();
    private final ProducerThreadFactory threadFactory;
    private final Worker<P, A, M>[] workers;
    private final ActorsLock<A> actorsLock = new ActorsLock<A>();
    private final ActorsWatcher<A> actorsWatcher = new ActorsWatcher<A>();
    private volatile boolean stopped = true;
    private ThreadPoolExecutor executor;

    @SuppressWarnings("unchecked")
    public GenericProducer(final Router<A, M> router, final P persistence, final int threadsCount, final ActorsType actorsType) {
        this.router = router;
        this.persistence = persistence;
        this.threadsCount = threadsCount;
        this.actorsType = actorsType;
        this.workers = new Worker[threadsCount];
        this.threadFactory = new ProducerThreadFactory(this.getClass().getSimpleName());
    }

    @Override
    public void start() {
        stop();
        synchronized (lockStartStop) {
            if (!stopped) {
                return;
            }

            executor = new ThreadPoolExecutor(threadsCount, threadsCount, 0, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(threadsCount), threadFactory);

            stopped = false;
            for (int i = 0; i < threadsCount; i++) {
                workers[i] = new GenericWorker<P, A, M>(
                        persistence, router, actorsLock, actorsWatcher, actorsType);
                executor.execute(workers[i]);
            }
        }
    }

    @Override
    public void stop() {
        synchronized (lockStartStop) {
            if (!stopped) {
                stopped = true;
                executor.shutdownNow();
                executor = null;
            }
        }
    }

    @Override
    public void checkMessagesFor(final A actor) {
        actorsWatcher.add(actor);
        awake();
    }

    private void awake() {
        if (!stopped) {
            for (final Worker worker : workers) {
                worker.awake();
            }
        }
    }

    private static final class ProducerThreadFactory implements ThreadFactory {

        private static final AtomicInteger GLOBAL_ID = new AtomicInteger(1);
        private final AtomicInteger localId = new AtomicInteger(1);
        private final ThreadGroup group;
        private final String namePrefix;
        private final String className;

        public ProducerThreadFactory(final String className) {
            this.className = className;
            final SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "message-producer-" + GLOBAL_ID.getAndIncrement() + "-";
        }

        @Override
        public Thread newThread(final Runnable runnable) {
            final Thread t = new Thread(group, runnable, namePrefix + className + "-" + localId.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY - 1) {
                t.setPriority(Thread.NORM_PRIORITY - 1);
            }
            return t;
        }
    }
}
