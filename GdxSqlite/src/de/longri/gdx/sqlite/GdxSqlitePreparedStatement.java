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

		static jobject javaResult(JNIEnv* env,long ptr, int sqliteResult, const char *errMsg) {
		    jclass objectClass = (env)->FindClass("de/longri/gdx/sqlite/GdxSqliteResult");

		    jmethodID cid = (env)->GetMethodID(objectClass, "<init>", "(JILjava/lang/String;)V");

		    jlong retPtr = ptr;
		    jint retResult = sqliteResult;
		    jstring retErrMsg = (env)->NewStringUTF(errMsg);


		    return (env)->NewObject(objectClass, cid, retPtr, retResult, retErrMsg);

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
        try {
            this.commit();
        } catch (Exception e) {
            // this will throw a 'SQL logic error' exception but is required for initialization
        }
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
        GdxSqliteResult result = null;
        for (Object value : values) {
            if (value instanceof String) {
                result = bindNativeString(this.ptr, this.db.ptr, idx, (String) value);
            } else {
                String error = "Bind value for class " + value.getClass().getSimpleName() + "not implemented";
                db.throwLastErr(new GdxSqliteResult(-1, -1, error));
            }

            if (result != null && result.retValue > 0)
                db.throwLastErr(result);

            idx++;
        }
        return this;
    }

    private native GdxSqliteResult bindNativeString(long stmtPtr, long dbPtr, int idx, String value); /*
            sqlite3* db = (sqlite3*)dbPtr;
            sqlite3_stmt* stmt = (sqlite3_stmt*)stmtPtr;
            const char *zErrMsg = 0;

            int rc = sqlite3_bind_text( stmt, idx, value, -1, 0);
            if( rc != SQLITE_OK ){
                zErrMsg = sqlite3_errmsg(db);
            }

            return javaResult(env, (long)stmt, rc, zErrMsg);
    */


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
