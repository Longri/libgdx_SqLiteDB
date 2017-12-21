package de.longri.gdx.sqlite;


import com.badlogic.gdx.jnigen.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Longri on 18.12.2017.
 */
public class SQLiteBuild {


    //@off
    /*JNI
    #include<sqlite3.h>


     */


    public native void add(float[] values, int offset, int numElements, float value); /*
        for(int i = offset; i < numElements; i++){
            values[i]+= value;
        }
    */

//    public native String getSqliteVersion(); /*
//        return (env)->NewStringUTF(sqlite3_libversion());
//    */


    public native String getSqliteVersion(); /*
        return (env)->NewStringUTF("Return String Test");
    */

    public static void main(String[] args) throws Exception {


        String[] headers = new String[]{"sqlite_src"};
        String[] sources = new String[]{"sqlite_src/sqlite3.c", "sqlite_src/shell.c"};

        File jniPath = new FileDescriptor("jni").file().getAbsoluteFile();
        String jniPathString = jniPath.getAbsolutePath();

        if (true) {
            // generate native code
            new NativeCodeGenerator().generate("src", "build/classes/main", "jni");


            //generate build scripts
            BuildTarget win64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, true);
            win64.compilerPrefix = "";
            win64.compilerSuffix = "";
            win64.headerDirs = headers;
            win64.cIncludes = sources;

            BuildTarget win32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, false);
            win32.compilerPrefix = "";
            win32.compilerSuffix = "";
            win32.headerDirs = headers;
            win32.cIncludes = sources;

            BuildTarget mac64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, true);
            mac64.compilerPrefix = "";
            mac64.compilerSuffix = "";
            mac64.headerDirs = headers;
            mac64.cIncludes = sources;

            BuildTarget mac32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, false);
            mac32.compilerPrefix = "";
            mac32.compilerSuffix = "";
            mac32.headerDirs = headers;
            mac32.cIncludes = sources;


            BuildTarget ios32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.IOS, false);
            ios32.compilerPrefix = "";
            ios32.compilerSuffix = "";
            ios32.headerDirs = headers;
            ios32.cIncludes = sources;

            BuildTarget linux32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, false);
            linux32.compilerPrefix = "";
            linux32.compilerSuffix = "";
            linux32.headerDirs = headers;
            linux32.cIncludes = sources;

            BuildTarget linux64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, true);
            linux64.compilerPrefix = "";
            linux64.compilerSuffix = "";
            linux64.headerDirs = headers;
            linux64.cIncludes = sources;
            linux64.linkerFlags = "-shared -m64 -Wl, " + jniPathString + "/memcpy_wrap.c";


            BuildConfig config = new BuildConfig("GdxSqlite");
            new AntScriptGenerator().generate(config, /*win32, win64,*/ mac64, ios32, mac32, linux32, linux64);

        }


        BuildExecutor.executeAnt("build-linux32.xml", "-v", jniPath);
        BuildExecutor.executeAnt("build-linux64.xml", "-v", jniPath);
//        BuildExecutor.executeAnt("build-windows32.xml", "-v", jniPath);
//        BuildExecutor.executeAnt("build-windows64.xml", "-v", jniPath);
        BuildExecutor.executeAnt("build-macosx64.xml", "-v", jniPath);
        BuildExecutor.executeAnt("build-macosx32.xml", "-v", jniPath);
        BuildExecutor.executeAnt("build-ios32.xml", "-v", jniPath);
        BuildExecutor.executeAnt("build-macosx64.xml", "-v", jniPath);
        BuildExecutor.executeAnt("build.xml", "-v", jniPath);


        new JniGenSharedLibraryLoader("libs/GdxSqlite-natives.jar").load("GdxSqlite");

        float[] values = new float[]{1, 2, 3, 4, 5};
        SQLiteBuild build = new SQLiteBuild();

        build.add(values, 0, 5, 5);
        System.out.println(Arrays.toString(values));

        System.out.println(build.getSqliteVersion());

    }


}
