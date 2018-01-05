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
import com.badlogic.gdx.utils.ObjectMap;

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

		#include <vector>

		static int callback(void *NotUsed, int argc, char **argv, char **azColName) {
		    // dummy callback!
		    return 0;
		}

		static jobject javaResult(JNIEnv* env,long ptr, int sqliteResult, const char *errMsg) {
		    jclass objectClass = (env)->FindClass("de/longri/gdx/sqlite/GdxSqliteResult");

		    jmethodID cid = (env)->GetMethodID(objectClass, "<init>", "(JILjava/lang/String;)V");

		    jlong retPtr = ptr;
		    jint retResult = sqliteResult;
		    jstring retErrMsg = (env)->NewStringUTF(errMsg);


		    return (env)->NewObject(objectClass, cid, retPtr, retResult, retErrMsg);

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
        GdxSqliteResult result = open(this.fileHandle.file().getAbsolutePath());
        this.ptr = result.ptr;
        if (result.retValue > 0) {
            throwLastErr(result);
        }
    }

    void throwLastErr(GdxSqliteResult result) {
        String errMsg = result.errorMsg;
        throw new SQLiteGdxException(errMsg);
    }

    private native String getErrorMsg(long ptr); /*
            sqlite3* db = (sqlite3*)ptr;
            return (env)->NewStringUTF(sqlite3_errmsg(db));
    */


    private native GdxSqliteResult open(String path) throws SQLiteGdxException; /*
            sqlite3 *db;
            const char *zErrMsg = 0;
            int rc;
            rc = sqlite3_open(path, &db);

            if( rc != SQLITE_OK ){
                zErrMsg = sqlite3_errmsg(db);
            }
            return javaResult(env, (long)db, rc, zErrMsg);
    */


    /**
     * Closes the opened database and releases all the resources related to this database.
     *
     * @throws SQLiteGdxException
     */
    public void closeDatabase() throws SQLiteGdxException {
        if (this.ptr >= 0) {
            close(this.ptr);
            this.ptr = -1;
        }
    }

    private native void close(long ptr) throws SQLiteGdxException; /*
            sqlite3* db = (sqlite3*)ptr;
            sqlite3_close(db);
    */


    /**
     * execute a single SQL statement that is NOT a SELECT or any other SQL statement that returns data.
     *
     * @param sql the SQL statement to be executed. Multiple statements separated by semicolons are not supported.
     * @throws SQLiteGdxException
     */
    public void execSQL(String sql) throws SQLiteGdxException {
        GdxSqliteResult result = this.exec(this.ptr, sql);
        if (result.retValue > 0) {
            throwLastErr(result);
        }
    }

    private native GdxSqliteResult exec(long ptr, String sql); /*
            char *zErrMsg = 0;
            int rc;
            sqlite3* db = (sqlite3*)ptr;

            rc = sqlite3_exec(db, sql, callback, 0, &zErrMsg);

            return javaResult(env, (long)db, rc, zErrMsg);
    */

    public interface RowCallback {
        void newRow(String[] columnName, Object[] value);
    }


    ObjectMap<Integer, RowCallback> callbackMap = new ObjectMap<>();
    static int statiCallbackPtr = 0;

    public void rawQuery(String sql, RowCallback callback) throws SQLiteGdxException {

        //register callback
        int callbackPtr;
        synchronized (callbackMap) {
            callbackPtr = statiCallbackPtr++;
            callbackMap.put(callbackPtr, callback);
        }
        GdxSqliteResult result = this.query(this.ptr, sql, callbackPtr);

        //remove callBack
        synchronized (callbackMap) {
            callbackMap.remove(callbackPtr);
        }

        if (result.retValue > 0) {
            throwLastErr(result);
        }
    }

    /**
     * Runs the provided SQL and returns a {@link SQLiteGdxDatabaseCursor} over the result set.
     *
     * @param sql  the SQL query. The SQL string must not be ; terminated
     * @return {@link SQLiteGdxDatabaseCursor}
     * @throws SQLiteGdxException
     */
    SQLiteGdxDatabaseCursor rawQuery(String sql) throws SQLiteGdxException {
        final GdxSqliteCursor cursor = new GdxSqliteCursor();

        //fill cursor over callback
        rawQuery(sql, new RowCallback() {
            @Override
            public void newRow(String[] columnNames, Object[] values) {
                cursor.addRow(columnNames, values);
            }
        });
        return cursor;
    }


    private native GdxSqliteResult query(long ptr, String sql, int callBackPtr); /*
        sqlite3* db = (sqlite3*)ptr;
		const char *zErrMsg = 0;


		sqlite3_stmt *stmt = NULL;
		int rc = sqlite3_prepare_v2(db, sql, -1, &stmt, NULL);
		if (rc == SQLITE_OK) {

		    int rowCount = 0;
		    rc = sqlite3_step(stmt);


		    jclass cls = (env)->GetObjectClass( object);
		    jmethodID mid = (env)->GetMethodID( cls, "nativeCallback", "(I[Ljava/lang/String;[Ljava/lang/Object;)V");

		    std::vector<const char *> names;
		    int colCount = sqlite3_column_count(stmt);

		    // Create a String[colCount]
		    jclass objectClass = (env)->FindClass("java/lang/Object");
		    jobjectArray valArr = (env)->NewObjectArray( colCount, objectClass, NULL);


		    while (rc != SQLITE_DONE && rc != SQLITE_OK) {
	            rowCount++;

				for (int colIndex = 0; colIndex < colCount; colIndex++) {
					int type = sqlite3_column_type(stmt, colIndex);
					const char * columnName = sqlite3_column_name(stmt, colIndex);

					names.push_back(columnName);

					if (type == SQLITE_INTEGER) {
					    jlong valInt = sqlite3_column_int64(stmt, colIndex);

					    jclass cls = (env)->FindClass("java/lang/Long");
					    jmethodID midInit = (env)->GetMethodID(cls, "<init>", "(J)V");
					    jobject intObj = (env)->NewObject(cls, midInit, valInt);
					    (env)->SetObjectArrayElement( valArr, colIndex, intObj);
					} else if (type == SQLITE_FLOAT) {
					    double valDouble = sqlite3_column_double(stmt, colIndex);

					    jclass cls = (env)->FindClass("java/lang/Double");
					    jmethodID midDouble = (env)->GetMethodID(cls, "<init>", "(D)V");
					    jobject idoubleObj = (env)->NewObject(cls, midDouble, valDouble);
					    (env)->SetObjectArrayElement( valArr, colIndex, idoubleObj);
					} else if (type == SQLITE_TEXT) {
					    const unsigned char * valChar = sqlite3_column_text(stmt, colIndex);
					    const char * val = reinterpret_cast < const char* >( valChar );
					    jstring jstrValue = (env)->NewStringUTF(val);
					    (env)->SetObjectArrayElement( valArr, colIndex, jstrValue);
					    //free(valChar);
					} else if (type == SQLITE_BLOB) {
					    printf("columnName = %s,BLOB\n", columnName);
					} else if (type == SQLITE_NULL) {
					    (env)->SetObjectArrayElement( valArr, colIndex, NULL);
					}
				}

				// callback to Java

				// Create a String[colCount]
				jclass stringClass = (env)->FindClass("java/lang/String");
				jobjectArray arr = (env)->NewObjectArray( colCount, stringClass, NULL);

				// Add name items
				for (int colIndex = 0; colIndex < colCount; colIndex++) {
				    jstring jstrName = (env)->NewStringUTF( names[colIndex]);
				    (env)->SetObjectArrayElement( arr, colIndex, jstrName);
				}

				(env)->CallVoidMethod(object, mid, callBackPtr, arr, valArr);

				names.clear();
				rc = sqlite3_step(stmt);
		    }
		    rc = sqlite3_finalize(stmt);
		} else {
		    zErrMsg = sqlite3_errmsg(db);
		}

		return javaResult(env, (long)db, rc, zErrMsg);
    */

    // called from C
    private void nativeCallback(int callbackPointer, String[] collnames, Object[] values) {
        synchronized (callbackMap) {
            RowCallback callback = callbackMap.get(callbackPointer);
            callback.newRow(collnames, values);
        }
    }


    public GdxSqlitePreparedStatement prepare(String sql) {
        GdxSqliteResult result = prepareNative(this.ptr, sql);
        if (result.retValue > 0)
            throwLastErr(result);

        return new GdxSqlitePreparedStatement(result.ptr, this, sql);
    }

    private native GdxSqliteResult prepareNative(long ptr, String sql); /*
        sqlite3* db = (sqlite3*)ptr;
		const char *zErrMsg = 0;
		const char *pzTest;
        sqlite3_stmt* stmt;

        int rc = sqlite3_prepare(db, sql, strlen(sql), &stmt, &pzTest);

        if( rc != SQLITE_OK ){
            zErrMsg = sqlite3_errmsg(db);
        }

        return javaResult(env, (long)stmt, rc, zErrMsg);
    */


}
