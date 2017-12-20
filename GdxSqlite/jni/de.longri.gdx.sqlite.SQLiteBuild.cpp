#include <de.longri.gdx.sqlite.SQLiteBuild.h>
JNIEXPORT void JNICALL Java_de_longri_gdx_sqlite_SQLiteBuild_add(JNIEnv* env, jobject object, jfloatArray obj_values, jint offset, jint numElements, jfloat value) {
	float* values = (float*)env->GetPrimitiveArrayCritical(obj_values, 0);


//@line:11

        for(int i = offset; i,numElements; i++){
            values[i]+= value;
        }
    
	env->ReleasePrimitiveArrayCritical(obj_values, values, 0);

}

