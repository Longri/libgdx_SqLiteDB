package de.longri.gdx.sqlite;


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
        new NativeCodeGenerator().generate("src", "bin/classes/main", "jni");


    }
}
