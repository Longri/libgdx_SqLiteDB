package de.longri.gdx.sqlite;

import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class GdxSqliteTest {

    @BeforeAll
    static void setUp() {
        //load natives
        new SharedLibraryLoader("GdxSqlite/testNatives/GdxSqlite-platform-1.0-natives-desktop.jar").load("GdxSqlite");
    }

    @Test
    void getSqliteVersion() {
        assertEquals("3.21.0", GdxSqlite.getSqliteVersion(), "SQLite version must be correct");
    }

    @Test
    void openOrCreateDatabase() {
    }

    @Test
    void execSQL() {
    }

    @Test
    void rawQuery() {
    }

    @Test
    void rawQuery1() {
    }
}