package de.longri.gdx.sqlite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.utils.SharedLibraryLoader;

/**
 * Created by Longri on 29.12.17.
 */
public class TestUtils {
    public static void initialGdx() {
        if (Gdx.files != null) return;
        Gdx.files = new LwjglFiles();
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
