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

package org.openjst.client.android.commons.inject;

import org.openjst.client.android.commons.ApplicationContext;
import org.openjst.client.android.commons.inject.annotations.JSTApplicationContext;
import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.testng.annotations.Test;

/**
 * @author Sergey Grachev
 */
public final class InjectTest {

    @Test(groups = "manual")
    public void testInject() {
        final Target target = new Target();

        target.invokeManagers();
        System.out.println(target);
    }

    @JSTApplicationContext(
            injector = GenericInjector.class,
            managers = {
                    TestManagerImpl.class,
                    TestManager2Impl.class
            })
    public static final class Target {

        @JSTInject
        private TestManager manager;

        public Target() {
            ApplicationContext.applyApplicationContextAnnotation(this);
        }

        public void invokeManagers() {
            manager.invoke();
        }
    }

    public static interface TestManager {

        void invoke();
    }

    @JSTInject(TestManager.class)
    public static final class TestManagerImpl implements TestManager {

        @JSTInject
        private TestManager2 testManager2;

        public TestManagerImpl() {
            System.out.println("TestManagerImpl");
        }

        public void invoke() {
            System.out.println("invoke: TestManager");
            testManager2.invoke();
        }
    }

    public static interface TestManager2 {

        void invoke();
    }

    @JSTInject(TestManager2.class)
    public static final class TestManager2Impl implements TestManager2 {

        @JSTInject
        private TestManager testManager;

        public TestManager2Impl() {
            System.out.println("TestManager2Impl");
        }

        public void invoke() {
            System.out.println("invoke: TestManager2");
        }
    }
}
