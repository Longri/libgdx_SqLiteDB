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
import de.longri.gdx.sqlite.tests.AfterAll;
import de.longri.gdx.sqlite.tests.BeforeAll;
import de.longri.gdx.sqlite.tests.Test;
import de.longri.gdx.sqlite.tests.TestUtils;

import java.util.concurrent.atomic.AtomicInteger;

import static de.longri.gdx.sqlite.tests.TestMain.assertThat;

/**
 * Created by Longri on 05.01.2018.
 */
class GdxSqlitePreparedStatementTest {

    static {
        TestUtils.initialGdx();
    }

    public GdxSqlitePreparedStatementTest() {
    } //constructor for core test reflection

    static FileHandle testFolder = Gdx.files.local("GdxSqlite/testResources2");

    @BeforeAll
    static void setUp() {
        //load natives
        TestUtils.loadSharedLib("GdxSqlite");
        testFolder.mkdirs();

        //delete old tests
        testFolder.deleteDirectory();
        testFolder.mkdirs();
    }

    @AfterAll
    static void tearDown() {
        assertThat("Test folder must be deleted after cleanup", testFolder.deleteDirectory());
    }


    @Test
    void statement() {
        FileHandle dbFileHandle = testFolder.child("statementTest.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        String sql = "CREATE TABLE Test (\n" +
                "Id INTEGER NOT NULL PRIMARY KEY,\n" +
                "realValue  REAL,\n" +
                "Name TEXT)";

        db.execSQL(sql);

        Object[][] values = new Object[10][];
        values[0] = new Object[]{0, 0.0, "row0"};
        values[1] = new Object[]{1, 1.1, "row1"};
        values[2] = new Object[]{2, 2.2, "row2"};
        values[3] = new Object[]{3, 3.3, "row3"};
        values[4] = new Object[]{4, 4.4, "row4"};
        values[5] = new Object[]{5, 5.5, "row5"};
        values[6] = new Object[]{6, 6.6, "row6"};
        values[7] = new Object[]{7, 7.7, "row7"};
        values[8] = new Object[]{8, 8.8, "row8"};
        values[9] = new Object[]{9, 9.9, "row9"};

        String statement = "INSERT INTO test VALUES(?,?,?)";
        GdxSqlitePreparedStatement preparedStatement = db.prepare(statement);

        for (Object[] rowValues : values) {
            if ((Integer) rowValues[0] == 9) break;
            preparedStatement.bind(rowValues);
            try {
                preparedStatement.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            preparedStatement.reset();
        }

        // bind separately
        preparedStatement.bind(1, values[9][0]);
        preparedStatement.bind(2, values[9][1]);
        preparedStatement.bind(3, values[9][2]);

        preparedStatement.commit().reset();
        preparedStatement.close();

        final AtomicInteger cnt = new AtomicInteger(-1);
        db.rawQuery("SELECT * FROM test", new GdxSqlite.RowCallback() {
            @Override
            public void newRow(String[] columnName, Object[] value) {
                int id = cnt.incrementAndGet();
                String num = Integer.toString(id);
                double val = Double.valueOf(num + "." + num);
                String name = "row" + num;

                assertThat("Id of row ? must be ?".replace("?", num), ((Long) value[0]).intValue() == id);
                assertThat("Value of row ? must be #".replace("?", num).replace("#", Double.toString(val)), (Double) value[1] == val);
                assertThat("Name of row ? must be #".replace("?", num).replace("#", name), value[2].equals(name));

            }
        });
    }

    @Test
    void bindLong() {
        FileHandle dbFileHandle = testFolder.child("statementTest2.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        String sql = "CREATE TABLE Test (\n" +
                "Id INTEGER NOT NULL PRIMARY KEY,\n" +
                "longValue  INTEGER,\n" +
                "Name TEXT)";

        db.execSQL(sql);

        Object[][] values = new Object[10][];
        values[0] = new Object[]{0, Long.MAX_VALUE, "row0"};
        values[1] = new Object[]{1, Long.MAX_VALUE - 1, "row1"};
        values[2] = new Object[]{2, Long.MAX_VALUE - 2, "row2"};
        values[3] = new Object[]{3, Long.MAX_VALUE - 3, "row3"};
        values[4] = new Object[]{4, Long.MAX_VALUE - 4, "row4"};
        values[5] = new Object[]{5, Long.MAX_VALUE - 5, "row5"};
        values[6] = new Object[]{6, Long.MAX_VALUE - 6, "row6"};
        values[7] = new Object[]{7, Long.MAX_VALUE - 7, "row7"};
        values[8] = new Object[]{8, Long.MAX_VALUE - 8, "row8"};
        values[9] = new Object[]{9, Long.MAX_VALUE - 9, "row9"};

        String statement = "INSERT INTO test VALUES(?,?,?)";
        GdxSqlitePreparedStatement preparedStatement = db.prepare(statement);

        for (Object[] rowValues : values) {
            if ((Integer) rowValues[0] == 9) break;
            preparedStatement.bind(rowValues);
            try {
                preparedStatement.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            preparedStatement.reset();
        }

        // bind separately
        preparedStatement.bind(1, values[9][0]);
        preparedStatement.bind(2, values[9][1]);
        preparedStatement.bind(3, values[9][2]);

        preparedStatement.commit().reset();
        preparedStatement.close();

        final AtomicInteger cnt = new AtomicInteger(-1);
        db.rawQuery("SELECT * FROM test", new GdxSqlite.RowCallback() {
            @Override
            public void newRow(String[] columnName, Object[] value) {
                int id = cnt.incrementAndGet();
                String num = Integer.toString(id);
                long val = Long.MAX_VALUE - id;
                String name = "row" + num;

                assertThat("Id of row ? must be ?".replace("?", num), ((Long) value[0]).intValue() == id);
                assertThat("Value of row ? must be #".replace("?", num).replace("#", Long.toString(val)), ((Long) value[1]) == val);
                assertThat("Name of row ? must be #".replace("?", num).replace("#", name), value[2].equals(name));

            }
        });

        SQLiteGdxDatabaseCursor cursor = db.rawQuery("SELECT * FROM test WHERE ID=5");

        cursor.moveToFirst();
        long value = cursor.getLong(1);
        assertThat("Value must be " + Long.toString(Long.MAX_VALUE - 5), value == (Long.MAX_VALUE - 5));

        cursor.close();
        db.closeDatabase();
    }


    @Test
    void bindBlob() {
        FileHandle dbFileHandle = testFolder.child("statementTest3.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        String sql = "CREATE TABLE Test (\n" +
                "Id INTEGER NOT NULL PRIMARY KEY,\n" +
                "blobValue  BLOB,\n" +
                "Name TEXT)";

        db.execSQL(sql);

        Object[][] values = new Object[10][];
        values[0] = new Object[]{0, new Byte[]{0, 0, 0, 0}, "row0"};
        values[1] = new Object[]{1, new Byte[]{1, 1, 1, 1}, "row1"};
        values[2] = new Object[]{2, new Byte[]{2, 2, 2, 2}, "row2"};
        values[3] = new Object[]{3, new Byte[]{3, 3, 3, 3}, "row3"};
        values[4] = new Object[]{4, new Byte[]{4, 4, 4, 4}, "row4"};
        values[5] = new Object[]{5, new Byte[]{5, 5, 5, 5}, "row5"};
        values[6] = new Object[]{6, new Byte[]{6, 6, 6, 6}, "row6"};
        values[7] = new Object[]{7, new Byte[]{7, 7, 7, 7}, "row7"};
        values[8] = new Object[]{8, new Byte[]{8, 8, 8, 8}, "row8"};
        values[9] = new Object[]{9, new Byte[]{9, 9, 9, 9}, "row9"};

        String statement = "INSERT INTO test VALUES(?,?,?)";
        GdxSqlitePreparedStatement preparedStatement = db.prepare(statement);

        for (Object[] rowValues : values) {
            if ((Integer) rowValues[0] == 9) break;
            preparedStatement.bind(rowValues);
            try {
                preparedStatement.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            preparedStatement.reset();
        }

        // bind separately
        preparedStatement.bind(1, values[9][0]);
        preparedStatement.bind(2, values[9][1]);
        preparedStatement.bind(3, values[9][2]);

        preparedStatement.commit().reset();
        preparedStatement.close();

        final AtomicInteger cnt = new AtomicInteger(-1);
        db.rawQuery("SELECT * FROM test", new GdxSqlite.RowCallback() {
            @Override
            public void newRow(String[] columnName, Object[] value) {
                int id = cnt.incrementAndGet();
                String num = Integer.toString(id);
                byte[] val = new byte[]{(byte) id, (byte) id, (byte) id, (byte) id};
                String name = "row" + num;

                assertThat("Id of row ? must be ?".replace("?", num), ((Long) value[0]).intValue() == id);
                assertThat("Value of row ? must be #".replace("?", num).replace("#", "ARRAY"), TestUtils.arrayEquals((byte[]) value[1], val));
                assertThat("Name of row ? must be #".replace("?", num).replace("#", name), value[2].equals(name));

            }
        });

        SQLiteGdxDatabaseCursor cursor = db.rawQuery("SELECT * FROM test WHERE ID=5");

        cursor.moveToFirst();
        byte[] value = cursor.getBlob(1);
        assertThat("Value must be [5,5,5,5]", TestUtils.arrayEquals((byte[]) value, new byte[]{5, 5, 5, 5}));

        cursor.close();
        db.closeDatabase();
    }

}