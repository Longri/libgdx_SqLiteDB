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

import static de.longri.gdx.sqlite.tests.TestMain.assertThat;

/**
 * Created by Longri on 21.01.2018.
 */
public class MalformedExceptionTest {

    static {
        TestUtils.initialGdx();
    }

    public MalformedExceptionTest() {
    } //constructor for core test reflection

    static FileHandle testFolder = Gdx.files.local("GdxSqlite/testResources9");

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
            dbPath = localPath + "/testResources/malformed.db3";
        } else {
            dbPath = localPath + "/GdxSqlite/testResources/malformed.db3";
        }

        FileHandle dbFileHandle = Gdx.files.absolute(dbPath);

        if (!dbFileHandle.exists()) {
            dbFileHandle = Gdx.files.internal("GdxSqlite/testResources/malformed.db3");
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
       // assertThat("Test folder must be deleted after cleanup", testFolder.deleteDirectory());
    }


    @Test
    public void readMalformedDB() {
        FileHandle dbFileHandle = testFolder.child("malformed.db3");
        GdxSqlite db = new GdxSqlite(dbFileHandle);
        db.openOrCreateDatabase();

        GdxSqliteCursor cursor = null;
        boolean exceptionThrowed = false;
        try {
            cursor = db.rawQuery("SELECT * FROM CacheCoreInfo ");
        } catch (SQLiteGdxException e) {
            if (e.getMessage().equals("database disk image is malformed")) exceptionThrowed = true;
        }
        assertThat("Must throw a 'SQLiteGdxException: database disk image is malformed", exceptionThrowed);

        db.closeDatabase();
    }


}
