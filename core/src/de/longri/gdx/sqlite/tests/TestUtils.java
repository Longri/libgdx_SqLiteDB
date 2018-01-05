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
