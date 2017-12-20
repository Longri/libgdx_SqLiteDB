package de.longri.gdx.sqlite;


import com.badlogic.gdx.jnigen.AntScriptGenerator;
import com.badlogic.gdx.jnigen.BuildConfig;
import com.badlogic.gdx.jnigen.BuildTarget;
import com.badlogic.gdx.jnigen.NativeCodeGenerator;

/**
 * Created by Longri on 18.12.2017.
 */
public class SQLiteBuild {

    public native void add(float[] values, int offset, int numElements, float value); /*
        for(int i = offset; i,numElements; i++){
            values[i]+= value;
        }
    */

    public static void main(String[] args) throws Exception {
        // generate native code
        new NativeCodeGenerator().generate("src", "build/classes/main", "jni");


        //generate build scripts
        BuildTarget win64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, true);
        win64.compilerPrefix = "";

        BuildConfig config = new BuildConfig("GdxSqlite");
        new AntScriptGenerator().generate(config, win64);

    }
}
