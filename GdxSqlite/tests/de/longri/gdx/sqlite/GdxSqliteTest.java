package de.longri.gdx.sqlite;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class GdxSqliteTest {

    static FileHandle testFolder = new FileHandle("GdxSqlite/testResources");

    @BeforeAll
    static void setUp() {
        //load natives
        new SharedLibraryLoader("GdxSqlite/testNatives/GdxSqlite-platform-1.0-natives-desktop.jar").load("GdxSqlite");
        testFolder.mkdirs();
    }

    @AfterAll
    static void tearDown() {
        assertThat("Test folder must be deleted after cleanup", testFolder.deleteDirectory());
    }

    @Test
    void getSqliteVersion() {
        assertEquals("3.21.0", GdxSqlite.getSqliteVersion(), "SQLite version must be correct");
    }

    @Test
    void openOrCreateDatabase() {

        FileHandle dbFileHandle = testFolder.child("createTest.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        final String CREATE = "CREATE TABLE Test (\n" +
                "    Id          INTEGER        NOT NULL\n" +
                "                               PRIMARY KEY AUTOINCREMENT,\n" +
                "    testName NVARCHAR (255)\n" +
                ");";

        db.execSQL(CREATE);
        db.closeDatabase();

        String dbFileString = dbFileHandle.readString();
        assertThat("DB file must created", dbFileString.startsWith("SQLite format 3"));

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