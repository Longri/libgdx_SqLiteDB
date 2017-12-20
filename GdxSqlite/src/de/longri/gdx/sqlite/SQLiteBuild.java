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

    public native void add(float[] values, int offset, int numElements, float value); /*
        for(int i = offset; i < numElements; i++){
            values[i]+= value + value;
        }
    */

    public static void main(String[] args) throws Exception {

        if (true) {
            // generate native code
            new NativeCodeGenerator().generate("src", "build/classes/main", "jni");


            //generate build scripts
            BuildTarget win64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, true);
            win64.compilerPrefix = "";
            win64.compilerSuffix = "";

            BuildTarget win32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, false);
            win32.compilerPrefix = "";
            win32.compilerSuffix = "";

            BuildTarget mac64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, true);
            mac64.compilerPrefix = "";
            mac64.compilerSuffix = "";

            BuildTarget ios32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.IOS, false);
            ios32.compilerPrefix = "";
            ios32.compilerSuffix = "";


            BuildConfig config = new BuildConfig("GdxSqlite");
            new AntScriptGenerator().generate(config, mac64, ios32);

            String path = new FileDescriptor("").file().getAbsolutePath();

            BuildExecutor.executeAnt("jni/build.xml", "-v");
        }

        File path = new FileDescriptor("jni").file().getAbsoluteFile();


        Process exec = new ProcessBuilder("ant", "-f", "build-macosx64.xml", "-v").directory(path).start();
        try (BufferedReader out = new BufferedReader(new InputStreamReader(exec.getInputStream()))) {
            out.lines().collect(Collectors.toList()).forEach(System.out::println);
        }

        exec = new ProcessBuilder("ant", "-f", "build-ios32.xml", "-v").directory(path).start();
        try (BufferedReader out = new BufferedReader(new InputStreamReader(exec.getInputStream()))) {
            out.lines().collect(Collectors.toList()).forEach(System.out::println);
        }


        exec = new ProcessBuilder("ant", "-v").directory(path).start();
        try (BufferedReader out = new BufferedReader(new InputStreamReader(exec.getInputStream()))) {
            out.lines().collect(Collectors.toList()).forEach(System.out::println);
        }


        new JniGenSharedLibraryLoader("libs/GdxSqlite-natives.jar").load("GdxSqlite");

        float[] values = new float[]{1, 2, 3, 4, 5};
        new SQLiteBuild().add(values, 0, 5, 5);
        System.out.println(Arrays.toString(values));


    }
}
