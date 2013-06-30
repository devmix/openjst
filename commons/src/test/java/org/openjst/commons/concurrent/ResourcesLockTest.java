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

package org.openjst.commons.concurrent;

import org.testng.annotations.Test;

/**
 * @author Sergey Grachev
 */
public final class ResourcesLockTest {

    @Test(groups = "manual")
    public void test() throws InterruptedException {
        final String obj = "1";
        final ResourcesLock<String> resourcesLock = new ResourcesLock<String>();

        System.out.println("Lock 1");
        resourcesLock.lock(obj);
        System.out.println("Lock 1: success");

        final Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Wait lock 2");
                try {
                    if (resourcesLock.lock(obj, 2000)) {
                        System.out.println("Lock 2: success");
                    } else {
                        System.out.println("Lock 2: fail");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        thread2.start();

        System.out.println("Wait before unlock 1");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        resourcesLock.unlock(obj);
        System.out.println("Unlock 1: success");

        thread2.join();
    }
}
