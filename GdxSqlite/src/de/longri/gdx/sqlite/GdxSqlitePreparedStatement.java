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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Longri on 04.01.2018.
 */
public class GdxSqlitePreparedStatement {

    private static final int SQLITE_DONE = 101;

    //@off
    /*JNI
        extern "C" {
		#include "sqlite3.h"
		}

		#include <vector>
		#include <cstring>

		static jobject javaResult(JNIEnv* env,long ptr, int sqliteResult, const char *errMsg) {
		    jclass objectClass = (env)->FindClass("de/longri/gdx/sqlite/GdxSqliteResult");

		    jmethodID cid = (env)->GetMethodID(objectClass, "<init>", "(JILjava/lang/String;)V");

		    jlong retPtr = ptr;
		    jint retResult = sqliteResult;
		    jstring retErrMsg = (env)->NewStringUTF(errMsg);


		    return (env)->NewObject(objectClass, cid, retPtr, retResult, retErrMsg);

		}

		JNIEXPORT jobject JNICALL Java_de_longri_gdx_sqlite_GdxSqlitePreparedStatement_bind_1blob(JNIEnv* env, jobject object, jlong stmtPtr, jlong dbPtr, jint idx, jbyteArray obj_value) {
			sqlite3* db = (sqlite3*)dbPtr;
			sqlite3_stmt* stmt = (sqlite3_stmt*)stmtPtr;
			jsize size;

			const char *zErrMsg = 0;
			size = (env)->GetArrayLength(obj_value);

			char* value = (char*)env->GetPrimitiveArrayCritical(obj_value, 0);
			int rc = sqlite3_bind_blob(stmt, idx, value, size, SQLITE_TRANSIENT);
			env->ReleasePrimitiveArrayCritical(obj_value, value, 0);

			if( rc != SQLITE_OK )
				zErrMsg = sqlite3_errmsg(db);
			return javaResult(env, (long)stmt, rc, zErrMsg);
		}

    */


    private final long ptr;
    private final GdxSqlite db;
    private final String statement;
    private final AtomicBoolean stmtClosed = new AtomicBoolean(false);

    GdxSqlitePreparedStatement(long ptr, GdxSqlite db, String statement) {
        this.ptr = ptr;
        this.db = db;
        this.statement = statement;

        // initial
        this.reset();
    }

    private void chkClosed() {
        if (stmtClosed.get()) {
            GdxSqliteResult error = new GdxSqliteResult(-1, -1, "GdxSqlitePreparedStatement is closed");
            db.throwLastErr(error);
        }
    }

    public GdxSqlitePreparedStatement bind(Object... values) {
        chkClosed();
        int idx = 1;
        for (Object value : values) {
            bind(idx++, value);
        }
        return this;
    }

    public GdxSqlitePreparedStatement bind(int idx, Object value) {
        GdxSqliteResult result = null;
        if (value instanceof String) {
            result = bind_text(this.ptr, this.db.ptr, idx, (String) value);
        } else if (value instanceof Integer || value instanceof Short) {
            result = bind_int(this.ptr, this.db.ptr, idx, (int) value);
        } else if (value instanceof Long) {
            result = bind_long(this.ptr, this.db.ptr, idx, (long) value);
        } else if (value instanceof Double || value instanceof Float) {
            result = bind_double(this.ptr, this.db.ptr, idx, (double) value);
        } else if (value instanceof Byte[]) {
            result = bind_blob(this.ptr, this.db.ptr, idx, toPrimitives(((Byte[]) value)));
        } else if (value instanceof byte[]) {
            result = bind_blob(this.ptr, this.db.ptr, idx, (byte[]) value);
        } else {
            String error = "Bind value for class '" + value.getClass().getSimpleName() + "' not implemented";
            db.throwLastErr(new GdxSqliteResult(-1, -1, error));
        }

        if (result != null && result.retValue > 0)
            db.throwLastErr(result);

        return this;
    }


    private static byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];
        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }
        return bytes;

    }

//    Binding Values To Prepared Statements
//    https://sqlite.org/c3ref/bind_blob.html
//
//    sqlite3_bind_text
//    sqlite3_bind_int
//    sqlite3_bind_blob
//    sqlite3_bind_double
//    sqlite3_bind_null

    private native GdxSqliteResult bind_text(long stmtPtr, long dbPtr, int idx, String value); /*
            sqlite3* db = (sqlite3*)dbPtr;
            sqlite3_stmt* stmt = (sqlite3_stmt*)stmtPtr;
            const char *zErrMsg = 0;
            int rc = sqlite3_bind_text( stmt, idx, value, strlen(value), SQLITE_TRANSIENT);
            if( rc != SQLITE_OK )
                zErrMsg = sqlite3_errmsg(db);
            return javaResult(env, (long)stmt, rc, zErrMsg);
    */

    private native GdxSqliteResult bind_int(long stmtPtr, long dbPtr, int idx, int value); /*
            sqlite3* db = (sqlite3*)dbPtr;
            sqlite3_stmt* stmt = (sqlite3_stmt*)stmtPtr;
            const char *zErrMsg = 0;
            int rc = sqlite3_bind_int( stmt, idx, value);
            if( rc != SQLITE_OK )
                zErrMsg = sqlite3_errmsg(db);
            return javaResult(env, (long)stmt, rc, zErrMsg);
    */

    private native GdxSqliteResult bind_double(long stmtPtr, long dbPtr, int idx, double value); /*
            sqlite3* db = (sqlite3*)dbPtr;
            sqlite3_stmt* stmt = (sqlite3_stmt*)stmtPtr;
            const char *zErrMsg = 0;
            int rc = sqlite3_bind_double( stmt, idx, value);
            if( rc != SQLITE_OK )
                zErrMsg = sqlite3_errmsg(db);
            return javaResult(env, (long)stmt, rc, zErrMsg);
    */

    private native GdxSqliteResult bind_long(long stmtPtr, long dbPtr, int idx, long value); /*
            sqlite3* db = (sqlite3*)dbPtr;
            sqlite3_stmt* stmt = (sqlite3_stmt*)stmtPtr;
            const char *zErrMsg = 0;
            int rc = sqlite3_bind_int64( stmt, idx, value);
            if( rc != SQLITE_OK )
                zErrMsg = sqlite3_errmsg(db);
            return javaResult(env, (long)stmt, rc, zErrMsg);
    */

    private native GdxSqliteResult bind_blob(long stmtPtr, long dbPtr, int idx, byte[] value);


    public GdxSqlitePreparedStatement commit() {
        chkClosed();
        GdxSqliteResult result = nativeCommit(this.ptr, this.db.ptr);

        if (result.retValue != SQLITE_DONE)
            db.throwLastErr(result);

        return this;
    }

    private native GdxSqliteResult nativeCommit(long stmtPtr, long dbPtr); /*
            sqlite3* db = (sqlite3*)dbPtr;
            sqlite3_stmt* stmt = (sqlite3_stmt*)stmtPtr;
            const char *zErrMsg = 0;

            int rc = sqlite3_step(stmt);

            if (rc != SQLITE_DONE) {
                zErrMsg = sqlite3_errmsg(db);
            }

            return javaResult(env, (long)stmt, rc, zErrMsg);

    */

    public void reset() {
        chkClosed();
        nativeReset(this.ptr);
    }

    private native void nativeReset(long stmtPtr); /*
         sqlite3_stmt* stmt = (sqlite3_stmt*)stmtPtr;
         sqlite3_reset(stmt);
         sqlite3_clear_bindings( stmt );
    */

    public void close() {
        nativeClose(this.ptr);
        stmtClosed.set(true);
    }

    private native void nativeClose(long stmtPtr); /*
        sqlite3_stmt* stmt = (sqlite3_stmt*)stmtPtr;
        sqlite3_finalize(stmt);
    */
}
