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


import com.badlogic.gdx.jnigen.*;

import java.io.File;

/**
 * Created by Longri on 18.12.2017.
 */
public class SQLiteBuild {

        static final boolean takeDummy = false;
//    static final boolean takeDummy = true;
    

    public static void main(String[] args) throws Exception {


        FileDescriptor targetDescriptor = new FileDescriptor("libs");
        FileDescriptor jniDescriptor = new FileDescriptor("jni");
        FileDescriptor dummyDescriptor = new FileDescriptor("sqlite_dummy_src");
        FileDescriptor sqliteDescriptor = new FileDescriptor("sqlite_src");

        File jniPath = jniDescriptor.file().getAbsoluteFile();
        String jniPathString = jniPath.getAbsolutePath();

        File dummyPath = dummyDescriptor.file().getAbsoluteFile();
        String dummyPathString = dummyPath.getAbsolutePath();

        File sqlitePath = sqliteDescriptor.file().getAbsoluteFile();
        String sqlitePathString = sqlitePath.getAbsolutePath();


        //cleanup
        jniDescriptor.deleteDirectory();
        targetDescriptor.deleteDirectory();


        String[] headers;
        if (takeDummy) {
            headers = new String[]{dummyPathString};
        } else {
            headers = new String[]{sqlitePathString};
        }


        // generate native code
        new NativeCodeGenerator().generate("src", "build/classes/main", "jni");


        //generate build scripts
        BuildTarget win64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, true);
        win64.compilerPrefix = "";
        win64.compilerSuffix = "";
        win64.headerDirs = headers;

        BuildTarget win32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Windows, false);
        win32.compilerPrefix = "";
        win32.compilerSuffix = "";
        win32.headerDirs = headers;

        BuildTarget mac64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, true);
        mac64.compilerPrefix = "";
        mac64.compilerSuffix = "";
        mac64.headerDirs = headers;


        BuildTarget mac32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.MacOsX, false);
        mac32.compilerPrefix = "";
        mac32.compilerSuffix = "";
        mac32.headerDirs = headers;


        BuildTarget ios32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.IOS, false);
        ios32.compilerPrefix = "";
        ios32.compilerSuffix = "";
        ios32.headerDirs = headers;

        BuildTarget linux32 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, false);
        linux32.compilerPrefix = "";
        linux32.compilerSuffix = "";
        linux32.headerDirs = headers;

        BuildTarget linux64 = BuildTarget.newDefaultTarget(BuildTarget.TargetOs.Linux, true);
        linux64.compilerPrefix = "";
        linux64.compilerSuffix = "";
        linux64.headerDirs = headers;
        linux64.linkerFlags = "-shared -m64 -Wl, " + jniPathString + "/memcpy_wrap.c";


        BuildConfig config = new BuildConfig("GdxSqlite");
        new AntScriptGenerator().generate(config, /*win32, win64, ios32, mac32, linux32, linux64,*/ mac64);


        //copy c/c++ src to 'jni' folder
        for (String headerPath : headers) {
            FileDescriptor fd = new FileDescriptor(headerPath);
            FileDescriptor[] list = fd.list();
            for (FileDescriptor descriptor : list) {
                descriptor.copyTo(jniDescriptor.child(descriptor.name()));
            }
        }


//        BuildExecutor.executeAnt("build-linux32.xml", "-v -Dhas-compiler=true", jniPath);
//        BuildExecutor.executeAnt("build-linux64.xml", "-v -Dhas-compiler=true", jniPath);
//        BuildExecutor.executeAnt("build-windows32.xml", "-v", jniPath);
//        BuildExecutor.executeAnt("build-windows64.xml", "-v", jniPath);
//        BuildExecutor.executeAnt("build-macosx64.xml", "-v -Dhas-compiler=true", jniPath);
//        BuildExecutor.executeAnt("build-macosx32.xml", "-v -Dhas-compiler=true", jniPath);
//        BuildExecutor.executeAnt("build-ios32.xml", "-v -Dhas-compiler=true", jniPath);
        BuildExecutor.executeAnt("build-macosx64.xml", "-v", jniPath);


        BuildExecutor.executeAnt("build.xml", "-v", jniPath);


        new JniGenSharedLibraryLoader("libs/GdxSqlite-natives.jar").load("GdxSqlite");

        System.out.println(GdxSqlite.getSqliteVersion());


    }


}
