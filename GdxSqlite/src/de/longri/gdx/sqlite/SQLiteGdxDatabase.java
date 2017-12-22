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



/**
 * This  interface contains the necessary methods to setup and execute queries on a database. The factory method
 * {@link SQLiteGdxDatabaseFactory#getNewDatabase(com.badlogic.gdx.files.FileHandle)} will return a database object that implements this
 * interface. The typical sequence of method calls should be as follows:
 * <ul>
 * <li>{@link SQLiteGdxDatabase#setupDatabase()}</li>
 * <li>{@link SQLiteGdxDatabase#openOrCreateDatabase()}</li>
 * <li>{@link SQLiteGdxDatabase#execSQL(String)} OR</li>
 * <li>{@link SQLiteGdxDatabase#rawQuery(String, String[])} OR</li>
 * <li>{@link SQLiteGdxDatabase#closeDatabase()}</li>
 * </ul>
 *
 * Created by Longri on 22.12.2017.
 */
 public interface SQLiteGdxDatabase {

    /**
     * This method is needed to be called only once before any database related activity can be performed. The method performs the
     * necessary procedures for the database. However, a database will not be opened/created until
     * {@link SQLiteGdxDatabase#openOrCreateDatabase()} is called.
     */
     void setupDatabase();

    /**
     * Opens an already existing database or creates a new database if it doesn't already exist.
     *
     * @throws SQLiteGdxException
     */
     void openOrCreateDatabase() throws SQLiteGdxException;

    /**
     * Closes the opened database and releases all the resources related to this database.
     *
     * @throws SQLiteGdxException
     */
     void closeDatabase() throws SQLiteGdxException;

    /**
     * execute a single SQL statement that is NOT a SELECT or any other SQL statement that returns data.
     *
     * @param sql the SQL statement to be executed. Multiple statements separated by semicolons are not supported.
     * @throws SQLiteGdxException
     */
     void execSQL(String sql) throws SQLiteGdxException;

    /**
     * Runs the provided SQL and returns a {@link SQLiteGdxDatabaseCursor} over the result set.
     *
     * @param sql  the SQL query. The SQL string must not be ; terminated
     * @param args
     * @return {@link SQLiteGdxDatabaseCursor}
     * @throws SQLiteGdxException
     */
     SQLiteGdxDatabaseCursor rawQuery(String sql, String[] args) throws SQLiteGdxException;

     void beginTransaction();

     void endTransaction();

     long insert(String tablename, Parameters val);

     long update(String tablename, Parameters val, String whereClause, String[] whereArgs);

     long delete(String tablename, String whereClause, String[] whereArgs);

     long insertWithConflictReplace(String tablename, Parameters val);

     long insertWithConflictIgnore(String tablename, Parameters val);

     boolean isOpen();
}
