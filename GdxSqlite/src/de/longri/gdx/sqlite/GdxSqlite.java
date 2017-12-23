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
 * SQLite native wrapper
 * <p>
 * <p>
 * Created by Longri on 22.12.2017.
 */
public class GdxSqlite {

    //@off
    /*JNI

        extern "C" {
            #include "sqlite3.h"
        }

//        static int callback(void *NotUsed, int argc, char **argv, char **azColName) {
//            // dummy callback!
//            return 0;
//        }

        static int callback(void *data, int argc, char **argv, char **azColName){
            int i;
            fprintf(stderr, "%s: ", (const char*)data);

            for(i = 0; i<argc; i++) {
                printf("%s = %s\n", azColName[i], argv[i] ? argv[i] : "NULL");
            }
            printf("\n");
            return 0;
        }

     */

    public static native String getSqliteVersion(); /*
        return (env)->NewStringUTF(sqlite3_libversion());
    */


    private final FileHandle fileHandle;
    long ptr = -1;


    public GdxSqlite(FileHandle fileHandle) {
        this.fileHandle = fileHandle;
    }


    /**
     * Opens an already existing database or creates a new database if it doesn't already exist.
     *
     * @throws SQLiteGdxException
     */
    public void openOrCreateDatabase() throws SQLiteGdxException {
        this.ptr = open(this.fileHandle.file().getAbsolutePath());
    }

    private native long open(String path) throws SQLiteGdxException; /*
        sqlite3 *db;
        char *zErrMsg = 0;
        int rc;

        fprintf(stderr, "Opened database \n");
        fprintf(stderr, path);
        fprintf(stderr, "\n");
        rc = sqlite3_open(path, &db);

        fprintf(stderr, "Open result:%d", rc);
        fprintf(stderr, "\n");

        fprintf(stderr, "SQLITE_OK value:%d", SQLITE_OK);
        fprintf(stderr, "\n");

        if( rc != SQLITE_OK ){
            fprintf(stderr, "Can't open database: %s\n", sqlite3_errmsg(db));
            return -1;
        } else {
            fprintf(stderr, "Opened database successfully\n");
            return (long)db;
        }
    */


    /**
     * Closes the opened database and releases all the resources related to this database.
     *
     * @throws SQLiteGdxException
     */
    void closeDatabase() throws SQLiteGdxException {
        if (this.ptr >= 0) {
            close(this.ptr);
            this.ptr = -1;
        }
    }

    private native void close(long ptr) throws SQLiteGdxException; /*
        sqlite3* db = (sqlite3*)ptr;
        sqlite3_close(db);
        fprintf(stderr, "Close database successfully\n");
    */


    /**
     * execute a single SQL statement that is NOT a SELECT or any other SQL statement that returns data.
     *
     * @param sql the SQL statement to be executed. Multiple statements separated by semicolons are not supported.
     * @throws SQLiteGdxException
     */
    public void execSQL(String sql) throws SQLiteGdxException {
        int resultCode = this.exec(this.ptr, sql);
        System.out.println("Execute result code: " + resultCode);
    }

    private native int exec(long ptr, String sql); /*
        char *zErrMsg = 0;
        int rc;
        sqlite3* db = (sqlite3*)ptr;

        // Execute SQL statement
        rc = sqlite3_exec(db, sql, callback, 0, &zErrMsg);

        if( rc != SQLITE_OK ){
            fprintf(stderr, "SQL error: %s\n", zErrMsg);
            sqlite3_free(zErrMsg);
        } else {
            fprintf(stdout, "Execute sql successfully\n");
        }
        return rc;
    */

    public interface RowCallback{
        void newRow(String columnName,String value);
    }


    public void rawQuery(String sql, String[] args, RowCallback callback ) throws SQLiteGdxException {

        //replace ? with args
        if (args != null) {
            for (String arg : args)
                sql = sql.replaceFirst("\\?", "'" + arg + "'");
        }
        int resultCode = this.query(this.ptr, sql);
        System.out.println("Query result code: " + resultCode);
    }

    private native int query(long ptr, String sql); /*
		sqlite3* db = (sqlite3*)ptr;


		sqlite3_stmt *stmt = NULL;
		int rc = sqlite3_prepare_v2(db, sql, -1, &stmt, NULL);
		if (rc != SQLITE_OK)
		    return rc;

		int rowCount = 0;
		rc = sqlite3_step(stmt);
		while (rc != SQLITE_DONE && rc != SQLITE_OK){
			rowCount++;
			int colCount = sqlite3_column_count(stmt);
			for (int colIndex = 0; colIndex < colCount; colIndex++){
				int type = sqlite3_column_type(stmt, colIndex);
				const char * columnName = sqlite3_column_name(stmt, colIndex);
				if (type == SQLITE_INTEGER){
					int valInt = sqlite3_column_int(stmt, colIndex);
					printf("columnName = %s, Integer val = %d\n" , columnName, valInt);
				}else if (type == SQLITE_FLOAT){
					double valDouble = sqlite3_column_double(stmt, colIndex);
					printf("columnName = %s,Double val = %f\n", columnName, valDouble);
				}else if (type == SQLITE_TEXT){
					const unsigned char * valChar = sqlite3_column_text(stmt, colIndex);
					printf("columnName = %s,Text val = %s\n", columnName, valChar);
					//free(valChar);
				}else if (type == SQLITE_BLOB){
					printf("columnName = %s,BLOB\n", columnName);
				}else if (type == SQLITE_NULL){
					printf("columnName = %s,NULL\n", columnName);
				}
			}

			printf("Line %d, rowCount = %d\n\n", rowCount, colCount);

			rc = sqlite3_step(stmt);
		}

		rc = sqlite3_finalize(stmt);
		return rc;

    */

}
