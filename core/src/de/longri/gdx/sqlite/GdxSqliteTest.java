/*
 * Copyright (C) 2017 team-cachebox.de
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
import com.badlogic.gdx.utils.SharedLibraryLoader;
import de.longri.gdx.sqlite.tests.AfterAll;
import de.longri.gdx.sqlite.tests.BeforeAll;
import de.longri.gdx.sqlite.tests.Test;
import de.longri.gdx.sqlite.tests.TestUtils;

import java.util.concurrent.atomic.AtomicInteger;

import static de.longri.gdx.sqlite.tests.TestMain.assertEquals;
import static de.longri.gdx.sqlite.tests.TestMain.assertThat;

/**
 * Created by Longri on 18.12.2017.
 */
public class GdxSqliteTest {

    static {
        TestUtils.initialGdx();
    }

    public GdxSqliteTest() {
    } //constructor for core test reflection

    static FileHandle testFolder = Gdx.files.local("GdxSqlite/testResources");

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
    void getSqliteVersion() {
        assertEquals("3.16.0", GdxSqlite.getSqliteVersion(), "SQLite version must be correct");
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


        // try if invalid path trow's a exception
        boolean exceptionThrowed = false;
        FileHandle excFileHandle = testFolder.child("/xxxxx/xxxx/createTest.db3");
        GdxSqlite dbexc = new GdxSqlite(excFileHandle);
        try {
            dbexc.openOrCreateDatabase();
        } catch (SQLiteGdxException e) {
            exceptionThrowed = true;
        }

        assertThat("Invalid DB path must throw a exception", exceptionThrowed);
    }

    @Test
    void execSQL() {

        FileHandle dbFileHandle = testFolder.child("createTest2.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        final String CREATE = "CREATE TABLE Test (\n" +
                "    Id          INTEGER        NOT NULL\n" +
                "                               PRIMARY KEY AUTOINCREMENT,\n" +
                "    testName NVARCHAR (255)\n" +
                ");";

        db.execSQL(CREATE);

        boolean exceptionThrowed = false;
        try {
            db.execSQL(CREATE);
        } catch (SQLiteGdxException e) {
//            e.printStackTrace();
            exceptionThrowed = true;
        }

        assertThat("Create a exist Table must throw a exception", exceptionThrowed);

        db.closeDatabase();
    }

    @Test
    void insertTest() {
        FileHandle dbFileHandle = testFolder.child("createTest3.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        final String CREATE = "CREATE TABLE COMPANY(" +
                "ID INT PRIMARY KEY     NOT NULL," +
                "NAME           TEXT    NOT NULL," +
                "AGE            INT     NOT NULL," +
                "ADDRESS        CHAR(50)," +
                "SALARY         REAL );";

        final String INSERT = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (1, 'Paul', 32, 'California', 20000.00 ); " +
                "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (2, 'Allen', 25, 'Texas', 15000.00 ); " +
                "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY)" +
                "VALUES (3, 'Teddy', 23, 'Norway', 20000.00 );" +
                "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY)" +
                "VALUES (4, 'Mark', 25, 'Rich-Mond ', 65000.00 );";

        final String WRONG_INSERT = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (1, 'Paul', 32, 'California', 20000.00 ); ";

        final String WRONG_INSERT2 = "INSERT INTO COMPANY ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES 5, 'Paul', 32, 'California', 20000.00 ); ";

        db.execSQL(CREATE);
        db.execSQL(INSERT);


        boolean exceptionThrowed = false;
        try {
            db.execSQL(WRONG_INSERT);
        } catch (SQLiteGdxException e) {
//            e.printStackTrace();
            exceptionThrowed = true;
        }
        assertThat("Insert a exist entry must throw a exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            db.execSQL(WRONG_INSERT2);
        } catch (SQLiteGdxException e) {
//            e.printStackTrace();
            exceptionThrowed = true;
        }
        assertThat("Execute a invalid SQL statement must throw a exception", exceptionThrowed);

        db.closeDatabase();

    }

    @Test
    void rawQuery() {
        FileHandle dbFileHandle = testFolder.child("createTest4.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        final String CREATE = "CREATE TABLE COMPANY(" +
                "ID INT PRIMARY KEY     NOT NULL," +
                "NAME           TEXT    NOT NULL," +
                "AGE            INT     NOT NULL," +
                "ADDRESS        CHAR(50)," +
                "SALARY         REAL );";

        final String INSERT = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (1, 'Paul', 32, 'California', 20000.12 ); " +
                "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (2, 'Allen', 25, 'Texas', 15000.00 ); " +
                "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY)" +
                "VALUES (3, 'Teddy', 23, 'Norway', NULL );" +
                "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY)" +
                "VALUES (4, 'Mark', 25, 'Rich-Mond ', 65000.00 );";

        db.execSQL(CREATE);
        db.execSQL(INSERT);

        boolean exceptionThrowed = false;
        try {
            SQLiteGdxDatabaseCursor cursor = db.rawQuery("SELECT * FROM CHRISTMAS", null);
        } catch (SQLiteGdxException e) {
//            e.printStackTrace();
            exceptionThrowed = true;
        }
        assertThat("Query on not exist Table must throw a exception", exceptionThrowed);


        SQLiteGdxDatabaseCursor cursor = db.rawQuery("SELECT * FROM COMPANY", null);
        assertThat("Cursor count must be 4", cursor.getCount() == 4);
        assertThat("Cursor must not after last", !cursor.isAfterLast());
        exceptionThrowed = false;
        try {
            assertThat("Cursor column 1 must be a Integer : 1", cursor.getInt(0) == 1);
        } catch (Exception e) {
//            e.printStackTrace();
            exceptionThrowed = true;
        }
        assertThat("Invalid cursor position must throw a exception", exceptionThrowed);
        cursor.moveToFirst();

        assertThat("Cursor column 1 must be a Integer : 1", cursor.getInt(0) == 1);
        assertThat("Cursor column 2 must be a String : Paul", cursor.getString(1).equals("Paul"));
        assertThat("Cursor column 3 must be a Integer : 32", cursor.getInt(2) == 32);
        assertThat("Cursor column 4 must be a String : California", cursor.getString(3).equals("California"));
        assertThat("Cursor column 5 must be a Double : 20000.12", cursor.getDouble(4) == 20000.12);

        cursor.next();
        assertThat("Cursor column 1 must be a Integer : 2", cursor.getInt(0) == 2);
        assertThat("Cursor column 2 must be a String : Allen", cursor.getString(1).equals("Allen"));
        assertThat("Cursor column 3 must be a Integer : 25", cursor.getInt(2) == 25);
        assertThat("Cursor column 4 must be a String : Texas", cursor.getString(3).equals("Texas"));
        assertThat("Cursor column 5 must be a Double : 15000.00", cursor.getDouble(4) == 15000.00);

        cursor.next();
        assertThat("Cursor column 1 must be a Integer : 3", cursor.getInt(0) == 3);
        assertThat("Cursor column 2 must be a String : Teddy", cursor.getString(1).equals("Teddy"));
        assertThat("Cursor column 3 must be a Integer : 23", cursor.getInt(2) == 23);
        assertThat("Cursor column 4 must be a String : California", cursor.getString(3).equals("Norway"));
        assertThat("Cursor column 5 must be a Double : NuLL", cursor.isNull(4));

        cursor.next();
        assertThat("Cursor column 1 must be a Integer : 4", cursor.getInt(0) == 4);
        assertThat("Cursor column 2 must be a String : Mark", cursor.getString(1).equals("Mark"));
        assertThat("Cursor column 3 must be a Integer : 25", cursor.getInt(2) == 25);
        assertThat("Cursor column 4 must be a String : Rich-Mond ", cursor.getString(3).equals("Rich-Mond "));
        assertThat("Cursor column 5 must be a Double : 65000.00", cursor.getDouble(4) == 65000.00);

        cursor.next();
        assertThat("Cursor must after Last", cursor.isAfterLast());

        cursor.close();
        db.closeDatabase();

    }

    @Test
    void callBackTest() {
        FileHandle dbFileHandle = testFolder.child("createTest5.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        final String CREATE = "CREATE TABLE COMPANY(" +
                "ID INT PRIMARY KEY     NOT NULL," +
                "NAME           TEXT    NOT NULL," +
                "AGE            INT     NOT NULL," +
                "ADDRESS        CHAR(50)," +
                "SALARY         REAL );";

        final String INSERT = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (1, 'Paul', 32, 'California', 20000.12 ); " +
                "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) " +
                "VALUES (2, 'Allen', 25, 'Texas', 15000.00 ); " +
                "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY)" +
                "VALUES (3, 'Teddy', 23, 'Norway', NULL );" +
                "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY)" +
                "VALUES (4, 'Mark', 25, 'Rich-Mond ', 65000.00 );";

        db.execSQL(CREATE);
        db.execSQL(INSERT);

        final AtomicInteger rowCount = new AtomicInteger(-1);
        db.rawQuery("SELECT * FROM COMPANY", null, new GdxSqlite.RowCallback() {
            @Override
            public void newRow(String[] columnName, Object[] value) {

                switch (rowCount.incrementAndGet()) {
                    case 0:
                        assertThat("ColumnName must be ID", columnName[0].equals("ID"));
                        assertThat("ColumnName must be NAME", columnName[1].equals("NAME"));
                        assertThat("ColumnName must be AGE", columnName[2].equals("AGE"));
                        assertThat("ColumnName must be ADDRESS", columnName[3].equals("ADDRESS"));
                        assertThat("ColumnName must be SALARY", columnName[4].equals("SALARY"));

                        assertThat("Value 0 must Instance of Integer and 1", value[0] instanceof Integer && (Integer) value[0] == 1);
                        assertThat("Value 1 must Instance of String and Paul", value[1] instanceof String && value[1].equals("Paul"));
                        assertThat("Value 2 must Instance of Integer and 32", value[2] instanceof Integer && (Integer) value[2] == 32);
                        assertThat("Value 3 must Instance of String and California", value[3] instanceof String && value[3].equals("California"));
                        assertThat("Value 4 must Instance of Double and 20000.12", value[4] instanceof Double && (Double) value[4] == 20000.12);
                        break;
                    case 1:
                        assertThat("ColumnName must be ID", columnName[0].equals("ID"));
                        assertThat("ColumnName must be NAME", columnName[1].equals("NAME"));
                        assertThat("ColumnName must be AGE", columnName[2].equals("AGE"));
                        assertThat("ColumnName must be ADDRESS", columnName[3].equals("ADDRESS"));
                        assertThat("ColumnName must be SALARY", columnName[4].equals("SALARY"));

                        assertThat("Value 0 must Instance of Integer and 2", value[0] instanceof Integer && (Integer) value[0] == 2);
                        assertThat("Value 1 must Instance of String and Allen", value[1] instanceof String && value[1].equals("Allen"));
                        assertThat("Value 2 must Instance of Integer and 25", value[2] instanceof Integer && (Integer) value[2] == 25);
                        assertThat("Value 3 must Instance of String and Texas", value[3] instanceof String && value[3].equals("Texas"));
                        assertThat("Value 4 must Instance of Double and 15000.00", value[4] instanceof Double && (Double) value[4] == 15000.00);
                        break;
                    case 2:
                        assertThat("ColumnName must be ID", columnName[0].equals("ID"));
                        assertThat("ColumnName must be NAME", columnName[1].equals("NAME"));
                        assertThat("ColumnName must be AGE", columnName[2].equals("AGE"));
                        assertThat("ColumnName must be ADDRESS", columnName[3].equals("ADDRESS"));
                        assertThat("ColumnName must be SALARY", columnName[4].equals("SALARY"));

                        assertThat("Value 0 must Instance of Integer and 3", value[0] instanceof Integer && (Integer) value[0] == 3);
                        assertThat("Value 1 must Instance of String and Teddy", value[1] instanceof String && value[1].equals("Teddy"));
                        assertThat("Value 2 must Instance of Integer and 23", value[2] instanceof Integer && (Integer) value[2] == 23);
                        assertThat("Value 3 must Instance of String and Norway", value[3] instanceof String && value[3].equals("Norway"));
                        assertThat("Value 4 must be NULL", value[4] == null);
                        break;
                    case 3:
                        assertThat("ColumnName must be ID", columnName[0].equals("ID"));
                        assertThat("ColumnName must be NAME", columnName[1].equals("NAME"));
                        assertThat("ColumnName must be AGE", columnName[2].equals("AGE"));
                        assertThat("ColumnName must be ADDRESS", columnName[3].equals("ADDRESS"));
                        assertThat("ColumnName must be SALARY", columnName[4].equals("SALARY"));

                        assertThat("Value 0 must Instance of Integer and 4", value[0] instanceof Integer && (Integer) value[0] == 4);
                        assertThat("Value 1 must Instance of String and Mark", value[1] instanceof String && value[1].equals("Mark"));
                        assertThat("Value 2 must Instance of Integer and 25", value[2] instanceof Integer && (Integer) value[2] == 25);
                        assertThat("Value 3 must Instance of String and Rich-Mond ", value[3] instanceof String && value[3].equals("Rich-Mond "));
                        assertThat("Value 4 must Instance of Double and 65000.00", value[4] instanceof Double && (Double) value[4] == 65000.00);
                        break;
                    default:
                        assertThat("Unknown result", false);
                }
            }
        });
        db.closeDatabase();

    }
}