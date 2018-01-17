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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Longri on 17.01.2018.
 */
class GdxSqliteCursorTest {

    static {
        TestUtils.initialGdx();
    }

    public GdxSqliteCursorTest() {
    } //constructor for core test reflection

    static FileHandle testFolder = Gdx.files.local("GdxSqlite/testResources5");

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
    void close() {
        FileHandle dbFileHandle = testFolder.child("createTest1.db3");
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
            GdxSqliteCursor cursor = db.rawQuery("SELECT * FROM CHRISTMAS");
        } catch (SQLiteGdxException e) {
//            e.printStackTrace();
            exceptionThrowed = true;
        }
        assertThat("Query on not exist Table must throw a exception", exceptionThrowed);


        GdxSqliteCursor cursor = db.rawQuery("SELECT * FROM COMPANY");
        assertThat("Cursor count must be 4", cursor.getCount() == 4);
        assertThat("Cursor must not after last", !cursor.isAfterLast());
        exceptionThrowed = false;
        try {
            assertThat("Cursor column 1 must be a Integer : 1", cursor.getInt(0) == 1);
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("Invalid cursor position must throw a exception", exceptionThrowed);
        cursor.moveToFirst();

        assertThat("Cursor column 1 must be a SQLite Type : SQLITE_INTEGER", cursor.getColumnType(0) == GdxSqlite.SQLITE_INTEGER);
        assertThat("Cursor column 2 must be a SQLite Type : SQLITE_TEXT", cursor.getColumnType(1) == GdxSqlite.SQLITE_TEXT);
        assertThat("Cursor column 3 must be a SQLite Type : SQLITE_INTEGER", cursor.getColumnType(2) == GdxSqlite.SQLITE_INTEGER);
        assertThat("Cursor column 4 must be a SQLite Type : SQLITE_TEXT", cursor.getColumnType(3) == GdxSqlite.SQLITE_TEXT);
        assertThat("Cursor column 5 must be a SQLite Type : SQLITE_FLOAT", cursor.getColumnType(4) == GdxSqlite.SQLITE_FLOAT);

        assertThat("Cursor column name 1 must be: ID", cursor.getColumnName(0).equals("ID"));
        assertThat("Cursor column name 2 must be: NAME", cursor.getColumnName(1).equals("NAME"));
        assertThat("Cursor column name 3 must be: AGE", cursor.getColumnName(2).equals("AGE"));
        assertThat("Cursor column name 4 must be: ADDRESS", cursor.getColumnName(3).equals("ADDRESS"));
        assertThat("Cursor column name 5 must be: SALARY", cursor.getColumnName(4).equals("SALARY"));

        assertThat("Cursor column 1 must be a Integer : 1", cursor.getInt("ID") == 1);
        assertThat("Cursor column 2 must be a String : Paul", cursor.getString("NAME").equals("Paul"));
        assertThat("Cursor column 3 must be a Integer : 32", cursor.getInt("AGE") == 32);
        assertThat("Cursor column 4 must be a String : California", cursor.getString("ADDRESS").equals("California"));
        assertThat("Cursor column 5 must be a Double : 20000.12", cursor.getDouble("SALARY") == 20000.12);

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

        exceptionThrowed = false;
        try {
            cursor.close();
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("Double call cursor.close() may not throw exception", !exceptionThrowed);

        db.closeDatabase();

        //Any access after closing must throw a 'SQLiteGdxException("Cursor is closed")'

        exceptionThrowed = false;
        try {
            cursor.moveToNext();
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.moveToNext()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.isAfterLast();
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.isAfterLast()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.moveToFirst();
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.moveToFirst()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getCount();
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getCount()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.next();
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.next()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.isNull(0);
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.isNull()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getColumnType(0);
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getColumnType()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getColumnName(0);
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getColumnName()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getString(0);
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getString()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getString("Name");
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getString()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getBoolean(0);
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getBoolean()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getBoolean("Name");
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getBoolean()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getShort(0);
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getShort()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getShort("Name");
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getShort()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getLong(0);
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getLong()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getLong("Name");
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getLong()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getInt(0);
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getInt()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getInt("Name");
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getInt()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getFloat(0);
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getFloat()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getFloat("Name");
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getFloat()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getDouble(0);
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getDouble()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getDouble("Name");
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getDouble()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getBlob(0);
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getBlob()' must throw exception", exceptionThrowed);

        exceptionThrowed = false;
        try {
            cursor.getBlob("Name");
        } catch (Exception e) {
            exceptionThrowed = true;
        }
        assertThat("After cursor.close(), 'cursor.getBlob()' must throw exception", exceptionThrowed);



    }
}
