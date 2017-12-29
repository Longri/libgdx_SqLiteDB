package de.longri.gdx.sqlite.tests;

import com.badlogic.gdx.utils.SharedLibraryLoader;

/**
 * Created by Longri on 29.12.17.
 */
public class TestUtils {
    public static void initialGdx() {
        // do nothing
    }

    public static void loadSharedLib(String gdxSqlite) {
        new SharedLibraryLoader().load(gdxSqlite);
    }
}
