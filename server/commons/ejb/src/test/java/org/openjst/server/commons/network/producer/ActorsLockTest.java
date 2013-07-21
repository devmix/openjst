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

import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public final class ActorsLockTest {

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    @Test(groups = "unit", timeOut = 5000)
    public void test() throws InterruptedException {
        final ActorsLock<Long> lock = new ActorsLock<Long>();
        final Object barrierThreadLock = new Object();
        final Object barrierThreadStop = new Object();

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                assertThat(lock.tryLock(1L)).isTrue();
                assertThat(lock.tryLock(1L)).isTrue();
                synchronized (barrierThreadLock) {
                    barrierThreadLock.notify();
                }
                synchronized (barrierThreadStop) {
                    try {
                        barrierThreadStop.wait();
                    } catch (InterruptedException ignore) {
                    }
                }
            }
        });
        thread.start();

        synchronized (barrierThreadLock) {
            barrierThreadLock.wait();
        }

        assertThat(lock.tryLock(1L)).isFalse();

        synchronized (barrierThreadLock) {
            barrierThreadLock.notify();
        }
        thread.interrupt();

        while (thread.isAlive()) {
            Thread.yield();
        }

        assertThat(lock.tryLock(1L)).isTrue();
        assertThat(lock.unlock(1L)).isTrue();
        assertThat(lock.tryLock(1L)).isTrue();
    }
}
