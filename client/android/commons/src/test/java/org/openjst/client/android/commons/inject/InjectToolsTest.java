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

import org.openjst.client.android.commons.inject.annotations.Inject;
import org.openjst.client.android.commons.inject.annotations.OnCreate;
import org.openjst.client.android.commons.inject.annotations.Singleton;
import org.openjst.client.android.commons.inject.annotations.android.ApplicationConfig;
import org.testng.annotations.Test;

/**
 * @author Sergey Grachev
 */
public final class InjectToolsTest {

    @Test(groups = "manual")
    public void testInject() {
        final Target target = new Target();

//        final ApplicationConfig annotation = Target.class.getAnnotation(ApplicationConfig.class);
//        final DefaultApplicationInjector injector = new DefaultApplicationInjector(null);
//        GlobalContext.assignInjector(new DefaultApplicationInjector(null));
//        GlobalContext.registerImplementations(annotation.implementations());
//        injector.apply(target);

        target.invokeManagers();

        System.out.println(target);
    }

    @ApplicationConfig(
            implementations = {
                    TestManager1Impl.class,
                    TestManager2Impl.class
            })
    public static final class Target {

        @Inject
        private TestManager1 manager;

        @Inject
        private TestManager3Impl testManager3;

        @Inject
        private TestManager4Impl testManager4;

        @OnCreate
        private void onCreate() {
            System.out.println("create: Target");
        }

        public void invokeManagers() {
            manager.invoke();
            testManager3.invoke();
            testManager4.invoke();
        }
    }

    public static interface TestManager1 {

        void invoke();
    }

    @Singleton
    public static final class TestManager1Impl implements TestManager1 {

        @Inject
        private TestManager2 testManager2;

        public TestManager1Impl() {
            System.out.println("~TestManager1");
        }

        @OnCreate
        private void onCreate() {
            System.out.println("create: TestManager1Impl");
        }

        public void invoke() {
            System.out.println("invoke: TestManager1");
            testManager2.invoke();
        }
    }

    public static interface TestManager2 {

        void invoke();
    }

    @Singleton
    public static final class TestManager2Impl implements TestManager2 {

        @Inject
        private TestManager1 testManager1; // cyclic reference

        public TestManager2Impl() {
            System.out.println("~TestManager2");
        }

        @OnCreate
        private void onCreate() {
            System.out.println("create: TestManager2Impl");
        }

        public void invoke() {
            System.out.println("invoke: TestManager2");
        }
    }

    // without interface
    public static final class TestManager3Impl {

        @Inject
        private TestManager1 testManager1;

        @Inject
        private TestManager2 testManager2;

        @Inject
        private TestManager4Impl testManager4;

        public TestManager3Impl() {
            System.out.println("~TestManager3");
        }

        @OnCreate
        private void onCreate() {
            System.out.println("create: TestManager3Impl");
        }

        public void invoke() {
            System.out.println("invoke: TestManager3");
            testManager1.invoke();
            testManager2.invoke();
        }
    }

    // without interface
    public static final class TestManager4Impl {

        @Inject
        private TestManager1 testManager1;

        public TestManager4Impl() {
            System.out.println("~TestManager4");
        }

        @OnCreate
        private void onCreate() {
            System.out.println("create: TestManager4Impl");
        }

        public void invoke() {
            System.out.println("invoke: TestManager4");
            testManager1.invoke();
        }
    }
}
