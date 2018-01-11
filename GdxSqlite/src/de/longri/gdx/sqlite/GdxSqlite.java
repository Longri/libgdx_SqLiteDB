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
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * SQLite native wrapper
 * <p>
 * <p>
 * Created by Longri on 22.12.2017.
 */
public class GdxSqlite {

    static {
        try {
            new SharedLibraryLoader().load("GdxSqlite");
        } catch (Exception e) {

        }
    }

    private static final String BEGIN_TRANSACTION = "BEGIN TRANSACTION";
    private static final String END_TRANSACTION = "END TRANSACTION";

    //@off
    /*JNI
        extern "C" {
		#include "sqlite3.h"
		}

		#include <vector>
		#include <cstring>

		static int callback(void *NotUsed, int argc, char **argv, char **azColName) {
		    // dummy callback!
		    return 0;
		}

		static jobject javaResult(JNIEnv* env, sqlite3 *db, long ptr, int sqliteResult, const char *errMsg) {
		    jclass objectClass = (env)->FindClass("de/longri/gdx/sqlite/GdxSqliteResult");

		    jmethodID cid = (env)->GetMethodID(objectClass, "<init>", "(JILjava/lang/String;)V");

            if( sqliteResult == 1){
                // return with error code
                int extErr = sqlite3_extended_errcode(db);
                jlong retPtr = ptr;
		        jint retResult = extErr;
		        jstring retErrMsg = (env)->NewStringUTF(errMsg);
		        return (env)->NewObject(objectClass, cid, retPtr, retResult, retErrMsg);
            }

		    jlong retPtr = ptr;
		    jint retResult = sqliteResult;
		    jstring retErrMsg = (env)->NewStringUTF(errMsg);
		    return (env)->NewObject(objectClass, cid, retPtr, retResult, retErrMsg);
		}

    */

    public static native String getSqliteVersion(); /*
            return (env)->NewStringUTF(sqlite3_libversion());
    */


    private final String path;
    Long ptr = null;


    public GdxSqlite(FileHandle fileHandle) {
        this.path = fileHandle.file().getAbsolutePath();
    }

    public GdxSqlite() {
        this.path = ":memory:";
    }


    /**
     * Opens an already existing database or creates a new database if it doesn't already exist.
     *
     * @throws SQLiteGdxException
     */
    public void openOrCreateDatabase() throws SQLiteGdxException {
        if (isOpen()) return;
        GdxSqliteResult result = open(this.path);
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
            return javaResult(env, db, reinterpret_cast <jlong> (db), rc, zErrMsg);
    */


    /**
     * Closes the opened database and releases all the resources related to this database.
     *
     * @throws SQLiteGdxException
     */
    public void closeDatabase() throws SQLiteGdxException {
        if (this.ptr >= 0) {
            close(this.ptr);
            this.ptr = null;
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
        checkOpen();
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

            return javaResult(env, db, reinterpret_cast <jlong> (db), rc, zErrMsg);
    */

    public interface RowCallback {
        void newRow(String[] columnName, Object[] value);
    }


    ObjectMap<Integer, RowCallback> callbackMap = new ObjectMap<>();
    static int statiCallbackPtr = 0;

    public void rawQuery(String sql, RowCallback callback) throws SQLiteGdxException {
        checkOpen();
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
     * Runs the provided SQL and returns a {@link GdxSqliteCursor} over the result set,
     * or NULL if the result are empty!
     *
     * @param sql the SQL query. The SQL string must not be ; terminated
     * @return {@link GdxSqliteCursor}
     * @throws SQLiteGdxException
     */
    public GdxSqliteCursor rawQuery(String sql) throws SQLiteGdxException {
        final GdxSqliteCursor cursor = new GdxSqliteCursor();

        //fill cursor over callback
        rawQuery(sql, new RowCallback() {
            @Override
            public void newRow(String[] columnNames, Object[] values) {
                cursor.addRow(columnNames, values);
            }
        });

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
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

		    // Create a Object[colCount]
		    jclass objectClass = (env)->FindClass("java/lang/Object");
		    jobjectArray valArr = (env)->NewObjectArray( colCount, objectClass, NULL);

            // Create a String[colCount]
			jclass stringClass = (env)->FindClass("java/lang/String");
			jobjectArray nameArr = (env)->NewObjectArray( colCount, stringClass, NULL);

			jclass longCls = (env)->FindClass("java/lang/Long");
			jmethodID midInitLong = (env)->GetMethodID(longCls, "<init>", "(J)V");

			jclass doubleCls = (env)->FindClass("java/lang/Double");
		    jmethodID midInitDouble = (env)->GetMethodID(doubleCls, "<init>", "(D)V");


            int flagNameArrInit = 0;

		    while (rc != SQLITE_DONE && rc != SQLITE_OK) {
	            rowCount++;

				for (int colIndex = 0; colIndex < colCount; colIndex++) {
					int type = sqlite3_column_type(stmt, colIndex);

					if(flagNameArrInit == 0){
					    names.push_back(sqlite3_column_name(stmt, colIndex));
					}

					if (type == SQLITE_INTEGER) {
					    (env)->SetObjectArrayElement( valArr, colIndex, (env)->NewObject(longCls, midInitLong, sqlite3_column_int64(stmt, colIndex)));
					} else if (type == SQLITE_FLOAT) {
					    (env)->SetObjectArrayElement( valArr, colIndex, (env)->NewObject(doubleCls, midInitDouble, sqlite3_column_double(stmt, colIndex)));
					} else if (type == SQLITE_TEXT) {
					    (env)->SetObjectArrayElement( valArr, colIndex, (env)->NewStringUTF(reinterpret_cast < const char* >( sqlite3_column_text(stmt, colIndex) )));
					} else if (type == SQLITE_BLOB) {
				        int length;
				        jbyteArray jBlob;
				        const void *blob;

						blob = sqlite3_column_blob(stmt, colIndex);

						if (!blob) {
							// The return value from sqlite3_column_blob() for a zero-length BLOB is a NULL pointer.
							jBlob = (env)->NewByteArray(0);
						}else{
							length = sqlite3_column_bytes(stmt, colIndex);
							jBlob = (env)->NewByteArray(length);
							if (!jBlob) {
								jBlob = NULL;
							}else{
								(env)->SetByteArrayRegion(jBlob, (jsize) 0, (jsize) length, (const jbyte*) blob);
							}
						}
                        (env)->SetObjectArrayElement( valArr, colIndex, jBlob);

					} else if (type == SQLITE_NULL) {
					    (env)->SetObjectArrayElement( valArr, colIndex, NULL);
					}
				}

				// callback to Java


                if(flagNameArrInit == 0){
                    // Add name items
				    for (int colIndex = 0; colIndex < colCount; colIndex++) {
				        jstring jstrName = (env)->NewStringUTF( names[colIndex]);
				        (env)->SetObjectArrayElement( nameArr, colIndex, jstrName);
				    }
				    flagNameArrInit = 1;
                }


				(env)->CallVoidMethod(object, mid, callBackPtr, nameArr, valArr);

				rc = sqlite3_step(stmt);
		    }


		    //release resources
		    names.clear();
		    std::vector<const char *>().swap(names);
            (env)->DeleteLocalRef(nameArr);
            (env)->DeleteLocalRef(valArr);
            (env)->DeleteLocalRef(objectClass);
            (env)->DeleteLocalRef(stringClass);
            (env)->DeleteLocalRef(longCls);
            (env)->DeleteLocalRef(doubleCls);
            rc = sqlite3_finalize(stmt);
		} else {
		    zErrMsg = sqlite3_errmsg(db);
		}

		return javaResult(env, db, reinterpret_cast <jlong> (db), rc, zErrMsg);
    */

    // called from C
    private void nativeCallback(int callbackPointer, String[] collnames, Object[] values) {
        synchronized (callbackMap) {
            RowCallback callback = callbackMap.get(callbackPointer);
            callback.newRow(collnames, values);
        }
    }


    public GdxSqlitePreparedStatement prepare(String sql) {
        checkOpen();
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

        return javaResult(env, db, reinterpret_cast <jlong> (stmt), rc, zErrMsg);
    */

    public void beginTransaction() {
        checkOpen();
        execSQL(BEGIN_TRANSACTION);
    }

    public void endTransaction() {
        checkOpen();
        execSQL(END_TRANSACTION);
    }

    public String getCompileOptions() {
        checkOpen();
        StringBuilder sb = new StringBuilder();
        rawQuery("PRAGMA compile_options", new RowCallback() {
            @Override
            public void newRow(String[] columnName, Object[] value) {
                sb.append(value[0]).append("\n");
            }
        });

        return sb.toString();
    }

    public boolean isOpen() {
        return this.ptr != null;
    }

    private void checkOpen() {
        if (!isOpen()) {
            throw new SQLiteGdxException("Database connection is closed");
        }
    }
}
