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

import com.badlogic.gdx.files.FileHandle;

/**
 * Created by Longri on 22.12.2017.
 */
public interface SQLiteGdxDatabaseManager {

    /**
     * This method will return a reference to an existing or a not-yet-created database. You will need to manually call methods on
     * the {@link SQLiteGdxDatabase} object to setup, open/create or close the database. See {@link SQLiteGdxDatabase} for more details. <b> Note:
     * </b> dbOnUpgradeQuery will only work on an Android device. It will be executed when you increment your database version
     * number. First, dbOnUpgradeQuery will be executed (Where you will generally perform activities such as dropping the tables,
     * etc.). Then dbOnCreateQuery will be executed. However, dbOnUpgradeQuery won't be executed on downgrading the database
     * version.
     *
     * @param dbFileHandle     The name of the database.
     * @return Returns a {@link SQLiteGdxDatabase} object pointing to an existing or not-yet-created database.
     */
    public SQLiteGdxDatabase getNewDatabase(FileHandle dbFileHandle);

}
