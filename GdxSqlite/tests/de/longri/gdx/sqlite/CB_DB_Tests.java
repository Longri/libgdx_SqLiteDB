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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Longri on 11.01.2018.
 */
public class CB_DB_Tests {

    static {
        TestUtils.initialGdx();
    }

    public CB_DB_Tests() {
    } //constructor for core test reflection

    static FileHandle testFolder = Gdx.files.local("GdxSqlite/testResources4");

    @BeforeAll
    public static void setUp() {
        //load natives
        TestUtils.loadSharedLib("GdxSqlite");
        testFolder.mkdirs();

        //delete old tests
        testFolder.deleteDirectory();
        testFolder.mkdirs();

        // copy test DB
        String localPath = Gdx.files.local("").file().getAbsolutePath();
        String dbPath;
        if (localPath.endsWith("GdxSqlite")) {
            dbPath = localPath + "/testResources/cachebox.db3";
        } else {
            dbPath = localPath + "/GdxSqlite/testResources/cachebox.db3";
        }

        FileHandle dbFileHandle = Gdx.files.absolute(dbPath);

        if (!dbFileHandle.exists()) {
            dbFileHandle = Gdx.files.internal("GdxSqlite/testResources/cachebox.db3");
        }

        // copy to test folder
        dbFileHandle.copyTo(testFolder);


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @AfterAll
    public static void tearDown() {
        assertThat("Test folder must be deleted after cleanup", testFolder.deleteDirectory());
    }


    @Test
    public void readDB() {
        FileHandle dbFileHandle = testFolder.child("cachebox.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        GdxSqliteCursor cursor = db.rawQuery("SELECT * FROM CacheCoreInfo ");
        assertThat("Cursor count must be 5925", cursor.getCount() == 5925);

        db.closeDatabase();
    }

    @Test
    public void storeConfig() {
        FileHandle dbFileHandle = testFolder.child("config.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        final String CREATE = "CREATE TABLE Config (\n" +
                "    [Key]      NVARCHAR (30) PRIMARY KEY UNIQUE NOT NULL,\n" +
                "    Value      NVARCHAR (255),\n" +
                "    LongString NTEXT,\n" +
                "    desired    NTEXT\n" +
                ");";

        db.execSQL(CREATE);

        GdxSqlitePreparedStatement statement = db.prepare("REPLACE INTO Config VALUES(?,?,?,?) ;");
        db.beginTransaction();
        statement.bind("Key1", "Value1", "LongString1", "-1").commit().reset();
        statement.bind("Key2", "Value2", "LongString2", "-2").commit().reset();
        statement.bind("Key3", "Value3", "LongString3", "-3").commit().reset();
        statement.bind("Key4", "Value4", "LongString4", "-4").commit().reset();
        statement.bind("Key5", "Value5", "LongString5", "-5").commit().reset();
        statement.bind("Key6", "Value6", "LongString6", "-6").commit().reset();
        db.endTransaction();

        db.rawQuery("SELECT * FROM Config", new GdxSqlite.RowCallback() {

            AtomicInteger idx = new AtomicInteger(0);

            @Override
            public void newRow(String[] columnName, Object[] value, int[] types) {
                switch (idx.getAndIncrement()) {
                    case 0:
                        assertThat("", value[0].equals("Key1"));
                        assertThat("", value[1].equals("Value1"));
                        assertThat("", value[2].equals("LongString1"));
                        assertThat("", value[3].equals("-1"));
                        break;
                    case 1:
                        assertThat("", value[0].equals("Key2"));
                        assertThat("", value[1].equals("Value2"));
                        assertThat("", value[2].equals("LongString2"));
                        assertThat("", value[3].equals("-2"));
                        break;
                    case 2:
                        assertThat("", value[0].equals("Key3"));
                        assertThat("", value[1].equals("Value3"));
                        assertThat("", value[2].equals("LongString3"));
                        assertThat("", value[3].equals("-3"));
                        break;
                    case 3:
                        assertThat("", value[0].equals("Key4"));
                        assertThat("", value[1].equals("Value4"));
                        assertThat("", value[2].equals("LongString4"));
                        assertThat("", value[3].equals("-4"));
                        break;
                    case 4:
                        assertThat("", value[0].equals("Key5"));
                        assertThat("", value[1].equals("Value5"));
                        assertThat("", value[2].equals("LongString5"));
                        assertThat("", value[3].equals("-5"));
                        break;
                    case 5:
                        assertThat("", value[0].equals("Key6"));
                        assertThat("", value[1].equals("Value6"));
                        assertThat("", value[2].equals("LongString6"));
                        assertThat("", value[3].equals("-6"));
                        break;
                    default:
                        assertThat("To match entries", false);
                }
            }
        });


        GdxSqlitePreparedStatement deleteStatement = db.prepare("DELETE FROM Config WHERE [Key] = ?;");

        db.beginTransaction();
        deleteStatement.bind("Key2").commit().reset();
        deleteStatement.bind("Key3").commit().reset();
        deleteStatement.bind("Key5").commit().reset();
        db.endTransaction();

        db.rawQuery("SELECT * FROM Config", new GdxSqlite.RowCallback() {

            AtomicInteger idx = new AtomicInteger(0);

            @Override
            public void newRow(String[] columnName, Object[] value, int[] types) {
                switch (idx.getAndIncrement()) {
                    case 0:
                        assertThat("", value[0].equals("Key1"));
                        assertThat("", value[1].equals("Value1"));
                        assertThat("", value[2].equals("LongString1"));
                        assertThat("", value[3].equals("-1"));
                        break;
                    case 1:
                        assertThat("", value[0].equals("Key4"));
                        assertThat("", value[1].equals("Value4"));
                        assertThat("", value[2].equals("LongString4"));
                        assertThat("", value[3].equals("-4"));
                        break;
                    case 2:
                        assertThat("", value[0].equals("Key6"));
                        assertThat("", value[1].equals("Value6"));
                        assertThat("", value[2].equals("LongString6"));
                        assertThat("", value[3].equals("-6"));
                        break;
                    default:
                        assertThat("To match entries", false);
                }
            }
        });


        db.beginTransaction();
        statement.bind("Key4", "changedValue4", "changedLongString4", "-4").commit().reset();
        db.endTransaction();

        db.rawQuery("SELECT * FROM Config", new GdxSqlite.RowCallback() {

            AtomicInteger idx = new AtomicInteger(0);

            @Override
            public void newRow(String[] columnName, Object[] value, int[] types) {
                switch (idx.getAndIncrement()) {
                    case 0:
                        assertThat("", value[0].equals("Key1"));
                        assertThat("", value[1].equals("Value1"));
                        assertThat("", value[2].equals("LongString1"));
                        assertThat("", value[3].equals("-1"));
                        break;
                    case 2:
                        assertThat("", value[0].equals("Key4"));
                        assertThat("", value[1].equals("changedValue4"));
                        assertThat("", value[2].equals("changedLongString4"));
                        assertThat("", value[3].equals("-4"));
                        break;
                    case 1:
                        assertThat("", value[0].equals("Key6"));
                        assertThat("", value[1].equals("Value6"));
                        assertThat("", value[2].equals("LongString6"));
                        assertThat("", value[3].equals("-6"));
                        break;
                    default:
                        assertThat("To match entries", false);
                }
            }
        });


        deleteStatement.close();
        statement.close();
        db.closeDatabase();
        assertThat("Test Db must deleted", dbFileHandle.delete());
    }

}
