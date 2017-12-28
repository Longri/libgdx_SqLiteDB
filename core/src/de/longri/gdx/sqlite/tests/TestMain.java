/*
 * Copyright (C) 2017 team-cachebox.de
 *
 * Licensed under the : GNU General  License (GPL);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.longri.gdx.sqlite.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import static com.badlogic.gdx.Application.LOG_DEBUG;

/**
 * Created by Longri on 18.12.2017.
 */
public class TestMain {

    public static class Testresult {
        String name;
        int runningTests = 0;
        int passedTests = 0;
        int failedTests = 0;
        boolean beforeError = false;
        boolean afterError = false;
    }


    public static boolean runTests(Class... testClasses) {


        int testContainer = 0;
        int tests = 0;
        int failed = 0;
        int passed = 0;
        boolean beforeFailer = false;
        boolean afterFailer = false;

        long start = System.currentTimeMillis();

        for (Class clazz : testClasses) {
            try {
                Testresult result = runTestClass(clazz);
                if (result.runningTests > 0) testContainer++;
                if (result.beforeError) beforeFailer = true;
                if (result.afterError) afterFailer = true;

                tests += result.runningTests;
                failed += result.failedTests;
                passed += result.passedTests;

            } catch (ReflectionException e) {
                e.printStackTrace();
            }
        }

        long runningTime = System.currentTimeMillis() - start;

        // log test result
        StringBuilder sb = new StringBuilder();
        sb.append("Test run finished after ").append(runningTime).append(" ms").append("\n");
        sb.append(format("[", testContainer, 10)).append("containers found      ]").append("\n");
        sb.append(format("[", tests, 10)).append("tests found           ]").append("\n");
        sb.append(format("[", passed, 10)).append("tests successful      ]").append("\n");
        sb.append(format("[", failed, 10)).append("tests failed          ]").append("\n");

        Gdx.app.setLogLevel(LOG_DEBUG);
        Gdx.app.debug("TEST", sb.toString());
        return !beforeFailer && !afterFailer && tests == passed;
    }

    private static String format(String first, Object o, int length) {
        String objString = o.toString();

        StringBuilder sb = new StringBuilder();

        sb.append(first);
        int fillLength = length - objString.length();

        for (int i = 0; i < fillLength; i++) {
            sb.append(" ");
        }
        sb.append(objString);
        sb.append(" ");

        return sb.toString();
    }

    private static Testresult runTestClass(Class clazz) throws ReflectionException {
        Testresult result = new Testresult();

        result.name = clazz.getSimpleName();

        Constructor[] constructors = ClassReflection.getConstructors(clazz);
        constructors[0].setAccessible(true);
        Object insance = constructors[0].newInstance();

        Method[] methodes = ClassReflection.getDeclaredMethods(clazz);


        //reflect BeforeAll
        for (int i = 0; i < methodes.length; i++) {
            Method method = methodes[i];
            method.setAccessible(true);
            if (method.isAnnotationPresent(BeforeAll.class)) {
                try {
                    method.invoke(insance);
                } catch (Exception e) {
                    e.printStackTrace();
                    result.beforeError = true;
                }
            }
        }

        // reflect tests
        for (int i = 0; i < methodes.length; i++) {
            Method method = methodes[i];
            if (method.isAnnotationPresent(Test.class)) {
                result.runningTests++;
                try {
                    method.invoke(insance);
                    result.passedTests++;
                } catch (Exception e) {
                    e.printStackTrace();
                    result.failedTests++;
                }
            }
        }

        // reflect tests
        for (int i = 0; i < methodes.length; i++) {
            Method method = methodes[i];
            if (method.isAnnotationPresent(AfterAll.class)) {
                try {
                    method.invoke(insance);
                } catch (Exception e) {
                    e.printStackTrace();
                    result.afterError = true;
                }
            }
        }

        return result;
    }


    public static void assertThat(String reason, boolean assertion) {
        if (!assertion) {
            throw new AssertionError(reason);
        }
    }


    public static void assertEquals(Object expected, Object actual, String message) {
        if (!objectsAreEqual(expected, actual)) {
            throw new AssertionError(message);
        }
    }

    static boolean objectsAreEqual(Object obj1, Object obj2) {
        if (obj1 == null) {
            return (obj2 == null);
        }
        return obj1.equals(obj2);
    }

}
