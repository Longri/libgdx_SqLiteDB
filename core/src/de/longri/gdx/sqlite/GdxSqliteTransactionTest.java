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
import com.badlogic.gdx.utils.StringBuilder;
import de.longri.gdx.sqlite.tests.AfterAll;
import de.longri.gdx.sqlite.tests.BeforeAll;
import de.longri.gdx.sqlite.tests.Test;
import de.longri.gdx.sqlite.tests.TestUtils;

import static de.longri.gdx.sqlite.tests.TestMain.assertThat;

/**
 * Created by Longri on 06.01.2018.
 */
public class GdxSqliteTransactionTest {

    static {
        TestUtils.initialGdx();
    }

    public GdxSqliteTransactionTest() {
    } //constructor for core test reflection

    static FileHandle testFolder = Gdx.files.local("GdxSqlite/testResources3");

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
    void exceptionTest() {
        FileHandle dbFileHandle = testFolder.child("statementTest.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);

        boolean exceptionThrowed = false;
        try {
            db.getCompileOptions();
        } catch (SQLiteGdxException e) {
            exceptionThrowed = true;
        }
        assertThat("Access to a closed DB must throw a exception", exceptionThrowed);


        db.openOrCreateDatabase();

        String sql = "CREATE TABLE Test (\n" +
                "Id INTEGER NOT NULL PRIMARY KEY,\n" +
                "realValue  REAL,\n" +
                "Name TEXT)";

        db.execSQL(sql);


        exceptionThrowed = false;
        try {
            db.endTransaction();
        } catch (SQLiteGdxException e) {
            exceptionThrowed = true;
        }
        assertThat("End_Transaction without Begin_Transaction must throw a exception", exceptionThrowed);

        db.beginTransaction();

        try {
            db.beginTransaction();
        } catch (SQLiteGdxException e) {
            exceptionThrowed = true;
        }
        assertThat("Begin_Transaction inside a Transaction must throw a exception", exceptionThrowed);
        db.endTransaction();
        db.closeDatabase();
    }

    @Test
    void transactionPerformanceTest() {
        FileHandle dbFileHandle = testFolder.child("statementTest2.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        String sql = "CREATE TABLE Test (\n" +
                "Id INTEGER NOT NULL PRIMARY KEY,\n" +
                "realValue  REAL,\n" +
                "Name TEXT)";

        db.execSQL(sql);

        Object[][] data = createData();
        String statement = "INSERT INTO test VALUES(?,?,?)";
        GdxSqlitePreparedStatement preparedStatement = db.prepare(statement);

        long start = System.currentTimeMillis();
        for (Object[] values : data) {
            preparedStatement.bind(values).commit().reset();

            assertThat("Transaction must inactive (Autocommit>0)",db.getAutoCommit()>0);
        }
        preparedStatement.close();
        long withoutTransaction = System.currentTimeMillis() - start;
        db.closeDatabase();


        dbFileHandle = testFolder.child("statementTest3.db3");
        db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();
        db.execSQL(sql);
        preparedStatement = db.prepare(statement);
        start = System.currentTimeMillis();
        db.beginTransaction();
        for (Object[] values : data) {
            preparedStatement.bind(values).commit().reset();
            assertThat("Transaction must active (Autocommit==0)",db.getAutoCommit()==0);
        }
        db.endTransaction();
        assertThat("Transaction must inactive (Autocommit>0)",db.getAutoCommit()>0);
        preparedStatement.close();
        long withTransaction = System.currentTimeMillis() - start;
        db.closeDatabase();

        assertThat("with Transaction should be faster", withTransaction < withoutTransaction);

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("Insert without transaction took's ").append(withoutTransaction).append(" ms\n");
        sb.append("Insert with transaction took's ").append(withTransaction).append(" ms\n");


        sb.append("\n");
        sb.append("SQLite-Version:");
        sb.append(GdxSqlite.getSqliteVersion());
        System.out.println(sb.toString());
    }

    private Object[][] createData() {
        int COUNT = 100;
        Object[][] data = new Object[COUNT][];
        for (int i = 0; i < COUNT; i++) {
            String num = Integer.toString(i);
            double val = Double.valueOf(num + "." + num);
            String name = "row" + num;
            data[i] = new Object[]{i, val, name};
        }
        return data;
    }
}
