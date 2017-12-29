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
        new SharedLibraryLoader("GdxSqlite/testNatives/GdxSqlite-platform-1.0-natives-desktop.jar").load(gdxSqlite);
    }


//
}
