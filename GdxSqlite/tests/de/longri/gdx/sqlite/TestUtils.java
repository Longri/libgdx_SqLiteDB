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


//
}
