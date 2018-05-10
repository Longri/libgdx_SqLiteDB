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

import java.io.UnsupportedEncodingException;

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

    private static final String BEGIN_TRANSACTION = "BEGIN;";
    private static final String END_TRANSACTION = "COMMIT;";


    public final static int SQLITE_INTEGER = 1;
    public final static int SQLITE_FLOAT = 2;
    public final static int SQLITE_TEXT = 3;
    public final static int SQLITE_BLOB = 4;
    public final static int SQLITE_NULL = 5;
    public final static int SQLITE_DONE = 101;

    private boolean inTransaction = false;

    public static byte[] utf8Bytes(String str) {
        if (str == null) {
            return null;
        }
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SQLiteGdxException("UTF-8 is not supported", e);
        }
    }

    public static String stringFromUtf8Bytes(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SQLiteGdxException("UTF-8 is not supported", e);
        }
    }

    public static void convertValuesByteArraysToString(Object[] values, int[] types) {
        for (int i = 0; i < types.length; i++) {
            if (types[i] == SQLITE_TEXT || types[i] == SQLITE_NULL) {

                //check if value a byte[]
                if (!(values[i] instanceof byte[])) continue;
                values[i] = GdxSqlite.stringFromUtf8Bytes((byte[]) values[i]);
            }
        }
    }

    //@off
    /*JNI
        extern "C" {
		#include "sqlite3.h"
		}

		#include <vector>
		#include <cstring>
		#include <stdlib.h>
		#include <cstdlib>

		static inline jobject javaResult(JNIEnv* env, sqlite3 *db, long ptr, int sqliteResult, const char *errMsg) {
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

		static inline void javaByteArrayConvert(JNIEnv *env, jbyteArray array, char** bytes, int* length){
		    jsize byte_length;
		    char* buf;

		    *bytes = NULL;
		    if (length) *length = 0;

		    byte_length = (env)->GetArrayLength((jarray) array);

		    buf = (char*) malloc(byte_length + 1);

		    (env)->GetByteArrayRegion(array, 0, byte_length, (jbyte*)buf);

		    buf[byte_length] = '\0';

		    *bytes = buf;
		    if (length){
			  *length = (int) byte_length;
		    }
		}

		static inline jbyteArray convertToJavaByteArray(JNIEnv *env, const char* bytes, int length){
		     jbyteArray result;

		     if (!bytes){
			 return NULL;
		     }

		     result = (env)->NewByteArray((jsize) length);
		     (env)->SetByteArrayRegion(result, (jsize) 0, (jsize) length, (const jbyte*) bytes);

		     return result;
		}


		JNIEXPORT jobject JNICALL Java_de_longri_gdx_sqlite_GdxSqlite_exec(JNIEnv* env, jobject object, jlong ptr, jbyteArray sql) {
			char *zErrMsg = 0;
			int rc;
			sqlite3* db = (sqlite3*)ptr;
			char* sql_bytes;

			javaByteArrayConvert(env, sql, &sql_bytes, NULL);

			rc = sqlite3_exec(db, sql_bytes, 0, 0, &zErrMsg);

			free(sql_bytes);

			return javaResult(env, db, reinterpret_cast <jlong> (db), rc, zErrMsg);

		}

		JNIEXPORT jobject JNICALL Java_de_longri_gdx_sqlite_GdxSqlite_query(JNIEnv* env, jobject object, jlong ptr, jbyteArray sql, jint callBackPtr) {
    sqlite3* db = (sqlite3*)ptr;
    const char *zErrMsg = 0;
    sqlite3_stmt *stmt = NULL;
    char* sql_bytes;

    javaByteArrayConvert(env, sql, &sql_bytes, NULL);
    int rc = sqlite3_prepare_v2(db, sql_bytes, -1, &stmt, NULL);
    free(sql_bytes);

    if (rc == SQLITE_OK) {
        int rowCount = 0;
        rc = sqlite3_step(stmt);


 //printf ("STEP_RESULT: %d \n", rc);



        if(rc == SQLITE_ROW){
            jclass cls = (env)->GetObjectClass( object);
            jmethodID mid = (env)->GetMethodID( cls, "nativeCallback", "(I[Ljava/lang/String;[Ljava/lang/Object;[I)V");

            std::vector<const char *> names;
            std::vector<int> types;

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

            jintArray typeArr = (env)->NewIntArray(colCount);

            int flagNameArrInit = 0;

            while (rc != SQLITE_DONE && rc != SQLITE_OK) {
                rowCount++;

                for (int colIndex = 0; colIndex < colCount; colIndex++) {
                    int type = sqlite3_column_type(stmt, colIndex);

                    if(flagNameArrInit == 0){
                        names.push_back(sqlite3_column_name(stmt, colIndex));
                        types.push_back(type);
                    }

                    if (type == SQLITE_INTEGER) {
                        jlong valInt = sqlite3_column_int64(stmt, colIndex);
                        jobject intObj = (env)->NewObject(longCls, midInitLong, valInt);
                        (env)->SetObjectArrayElement( valArr, colIndex, intObj);;
                        (env)->DeleteLocalRef(intObj);
                        } else if (type == SQLITE_FLOAT) {
                        double valDouble = sqlite3_column_double(stmt, colIndex);
                        jobject idoubleObj = (env)->NewObject(doubleCls, midInitDouble, valDouble);
                        (env)->SetObjectArrayElement( valArr, colIndex, idoubleObj);
                        (env)->DeleteLocalRef(idoubleObj);
                        } else if (type == SQLITE_TEXT) {
                        const char *bytes;
                        int length;

                        bytes = (const char*) sqlite3_column_text(stmt, colIndex);
                        length = sqlite3_column_bytes(stmt, colIndex);

                        jbyteArray strByteArr = convertToJavaByteArray(env, bytes, length);

                        (env)->SetObjectArrayElement( valArr, colIndex, strByteArr);
                        (env)->DeleteLocalRef(strByteArr);
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
                        (env)->DeleteLocalRef(jBlob);
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
                        (env)->DeleteLocalRef(jstrName);
                    }
                    env->SetIntArrayRegion( typeArr, 0, types.size(), ( jint * ) &types[0] );

                    flagNameArrInit = 1;
                }

                (env)->CallVoidMethod(object, mid, callBackPtr, nameArr, valArr, typeArr);

                rc = sqlite3_step(stmt);
            }


            //release resources
            names.clear();
            std::vector<const char *>().swap(names);
            (env)->DeleteLocalRef(nameArr);
            (env)->DeleteLocalRef(valArr);
            (env)->DeleteLocalRef(typeArr);
            (env)->DeleteLocalRef(objectClass);
            (env)->DeleteLocalRef(stringClass);
            (env)->DeleteLocalRef(longCls);
            (env)->DeleteLocalRef(doubleCls);
            rc = sqlite3_finalize(stmt);
            }else{
            zErrMsg = sqlite3_errmsg(db);
        }
        } else {
        zErrMsg = sqlite3_errmsg(db);
    }

    return javaResult(env, db, reinterpret_cast <jlong> (db), rc, zErrMsg);
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
        if (this.ptr !=null) {
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
        GdxSqliteResult result = this.exec(this.ptr, utf8Bytes(sql));
        if (result.retValue > 0) {
            throwLastErr(result);
        }
    }

    private native GdxSqliteResult exec(long ptr, byte[] sql);

    /**
     * @return Number of rows that were INSERT, UPDATE or DELETE <br>
     * by the last SQL statement
     */
    public int changes() {
        checkOpen();
        return changes(this.ptr);
    }

    private native int changes(long ptr);/*
            sqlite3* db = (sqlite3*)ptr;
            return sqlite3_changes(db);
    */



    /**
     * returns non-zero or zero if the given database connection is or is not in autocommit mode, respectively.<br>
     * Autocommit mode is on by default. Autocommit mode is disabled by a BEGIN statement.<br>
     * Autocommit mode is re-enabled by a COMMIT or ROLLBACK.
     *
     * @throws SQLiteGdxException
     */
    public int getAutoCommit() throws SQLiteGdxException {
        if (this.ptr !=null) {
            return getAutoCommit(this.ptr);
        }
        return -1;
    }

    private static native int getAutoCommit(long ptr);/*
            sqlite3* db = (sqlite3*)ptr;
            return sqlite3_get_autocommit(db);
    */


    public boolean isInTransaction() {
        return inTransaction;
    }

    public interface RowCallback {
        void newRow(String[] columnName, Object[] value, int[] types);
    }


    ObjectMap<Integer, RowCallback> callbackMap = new ObjectMap<Integer, RowCallback>();
    static int statiCallbackPtr = 0;

    public void rawQuery(String sql, RowCallback callback) throws SQLiteGdxException {
        checkOpen();
        //register callback
        int callbackPtr;
        synchronized (callbackMap) {
            callbackPtr = statiCallbackPtr++;
            callbackMap.put(callbackPtr, callback);
        }
        GdxSqliteResult result = this.query(this.ptr, utf8Bytes(sql), callbackPtr);

        //remove callBack
        synchronized (callbackMap) {
            callbackMap.remove(callbackPtr);
        }

        if (result.retValue > 0 && !(result.retValue == SQLITE_DONE)) {
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
            public void newRow(String[] columnNames, Object[] values, int[] types) {
                cursor.addRow(columnNames, values, types);
            }
        });

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        return cursor;
    }


    private native GdxSqliteResult query(long ptr, byte[] sql, int callBackPtr);

    // called from C
    private void nativeCallback(int callbackPointer, String[] collnames, Object[] values, int[] types) {
        synchronized (callbackMap) {
            RowCallback callback = callbackMap.get(callbackPointer);

            //if any value from type text we must change the byte[] to utf8 String
            GdxSqlite.convertValuesByteArraysToString(values, types);

            callback.newRow(collnames, values, types);
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
        this.inTransaction = true;
    }

    public void endTransaction() {
        checkOpen();
        execSQL(END_TRANSACTION);
        this.inTransaction = false;
    }

    public String getCompileOptions() {
        checkOpen();
        final StringBuilder sb = new StringBuilder();
        rawQuery("PRAGMA compile_options", new RowCallback() {
            @Override
            public void newRow(String[] columnName, Object[] value, int[] types) {
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
