package de.longri.gdx.sqlite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Longri on 11.01.2018.
 */
public class CB_DB_Tests {

    static {
        TestUtils.initialGdx();
    }

    public CB_DB_Tests() {
    } //constructor for core test reflection

    static FileHandle testFolder = Gdx.files.local("GdxSqlite/testResources");

    @BeforeAll
    static void setUp() {
        //load natives
        TestUtils.loadSharedLib("GdxSqlite");
        testFolder.mkdirs();

    }

    @AfterAll
    static void tearDown() {

    }


    @Test
    void readDB() {
        FileHandle dbFileHandle = testFolder.child("cachebox.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        GdxSqliteCursor cursor = db.rawQuery("SELECT * FROM CacheCoreInfo ");

        assertThat("Cursor count must be 5925", cursor.getCount() == 5925);


    }

}
