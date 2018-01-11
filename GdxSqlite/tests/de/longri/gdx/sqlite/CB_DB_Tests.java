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

import com.badlogic.gdx.Application;
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

        FileHandle asset = Gdx.files.internal("GdxSqlite/testResources/cachebox.db3");
        if (asset.exists()&& (Gdx.app.getType()== Application.ApplicationType.Android || Gdx.app.getType()== Application.ApplicationType.iOS)) {

            //delete old tests
            testFolder.deleteDirectory();
            testFolder.mkdirs();

            // copy to test folder
            asset.copyTo(testFolder);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
