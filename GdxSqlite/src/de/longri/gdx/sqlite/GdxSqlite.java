package de.longri.gdx.sqlite;

public class GdxSqlite {

    //@off
    /*JNI

        extern "C" {
            #include "sqlite3.h"
        }

     */

    public static native String getSqliteVersion(); /*
        return (env)->NewStringUTF(sqlite3_libversion());
    */

//    public static native String getSqliteVersion(); /*
//        return (env)->NewStringUTF("Return dummy String Test");
//    */

}
