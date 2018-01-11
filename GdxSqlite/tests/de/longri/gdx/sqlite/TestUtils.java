/*
 * Copyright (C) 2018 team-cachebox.de
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
package de.longri.gdx.sqlite;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.SharedLibraryLoader;

/**
 * Created by Longri on 29.12.17.
 */
public class TestUtils {
    public static void initialGdx() {
        if (Gdx.files != null) return;
        Gdx.files = new LwjglFiles();
        Gdx.app = new Application() {
            @Override
            public ApplicationListener getApplicationListener() {
                return null;
            }

            @Override
            public Graphics getGraphics() {
                return null;
            }

            @Override
            public Audio getAudio() {
                return null;
            }

            @Override
            public Input getInput() {
                return null;
            }

            @Override
            public Files getFiles() {
                return null;
            }

            @Override
            public Net getNet() {
                return null;
            }

            @Override
            public void log(String s, String s1) {

            }

            @Override
            public void log(String s, String s1, Throwable throwable) {

            }

            @Override
            public void error(String s, String s1) {

            }

            @Override
            public void error(String s, String s1, Throwable throwable) {

            }

            @Override
            public void debug(String s, String s1) {

            }

            @Override
            public void debug(String s, String s1, Throwable throwable) {

            }

            @Override
            public void setLogLevel(int i) {

            }

            @Override
            public int getLogLevel() {
                return 0;
            }

            @Override
            public void setApplicationLogger(ApplicationLogger applicationLogger) {

            }

            @Override
            public ApplicationLogger getApplicationLogger() {
                return null;
            }

            @Override
            public ApplicationType getType() {
                return ApplicationType.HeadlessDesktop;
            }

            @Override
            public int getVersion() {
                return 0;
            }

            @Override
            public long getJavaHeap() {
                return 0;
            }

            @Override
            public long getNativeHeap() {
                return 0;
            }

            @Override
            public Preferences getPreferences(String s) {
                return null;
            }

            @Override
            public Clipboard getClipboard() {
                return null;
            }

            @Override
            public void postRunnable(Runnable runnable) {

            }

            @Override
            public void exit() {

            }

            @Override
            public void addLifecycleListener(LifecycleListener lifecycleListener) {

            }

            @Override
            public void removeLifecycleListener(LifecycleListener lifecycleListener) {

            }
        };
    }

    public static void loadSharedLib(String gdxSqlite) {

        String localPath = Gdx.files.local("").file().getAbsolutePath();
        String nativePath;
        if (localPath.endsWith("GdxSqlite")) {
            nativePath = localPath + "/testNatives/GdxSqlite-platform-1.0-natives-desktop.jar";
        } else {
            nativePath = localPath + "/GdxSqlite/testNatives/GdxSqlite-platform-1.0-natives-desktop.jar";
        }

        System.out.println("Native path = " + nativePath);
        new SharedLibraryLoader(nativePath).load(gdxSqlite);
    }

    public static String arrayToString(Object[] items) {
        if (items.length == 0) return "[]";

        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append(items[0]);
        for (int i = 1; i < items.length; i++) {
            buffer.append(", ");
            buffer.append(items[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    public static boolean arrayEquals(Object[] items1, Object[] items2) {
        if (items1.length != items2.length) return false;
        for (int i = 0; i < items1.length; i++) {
            Object o1 = items1[i];
            Object o2 = items2[i];
            if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
        }
        return true;
    }

    public static boolean arrayEquals(byte[] items1, byte[] items2) {
        if (items1.length != items2.length) return false;
        for (int i = 0; i < items1.length; i++) {
            Object o1 = items1[i];
            Object o2 = items2[i];
            if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
        }
        return true;
    }


}
