# libgdx_SqLiteDB
[![Build Status](https://travis-ci.org/Longri/libgdx_SqLiteDB.svg?branch=master)](https://travis-ci.org/Longri/libgdx_SqLiteDB)

A SQLite wrapper library containing a SQLite version compiled especially for iOS/Android/Desktop.

The SQLite library is compiled using JinGen for Android and Desktop.
For iOS it is compiled with XCode and the project https://github.com/swiftlyfalling/SQLiteLib

All wrapper classes are compiled via JinGen!

This allows you to use the same SQLite version on all platforms.
Currently, SQLite version 3.21.0 is used here.

The set of SQLite compile flags are:
* -DSQLITE_ENABLE_API_ARMOR
* -DSQLITE_ENABLE_FTS3
* -DSQLITE_ENABLE_FTS3_PARENTHESIS
* -DSQLITE_ENABLE_RTREE
* -DSQLITE_ENABLE_UPDATE_DELETE_LIMIT
* -DSQLITE_OMIT_AUTORESET
* -DSQLITE_OMIT_BUILTIN_TEST
* -DSQLITE_OMIT_LOAD_EXTENSION
* -DSQLITE_SYSTEM_MALLOC
* -DSQLITE_THREADSAFE=2
* -DSQLITE_OS_UNIX=1

To add to your project using Gradle, add the following to the root build.gradle. Once the following are added sync the project.


allprojects
ext
```
     gdxSqliteVersion = '0.5.5-SNAPSHOT'
```

(desktop)
dependencies
```
     compile "de.longri.gdx-sqlite:gdx-sqlite-platform:$gdxSqliteVersion:natives-desktop"
```

(android)
dependencies
```
     compile "de.longri.gdx-sqlite:gdx-sqlite:$gdxSqliteVersion"
     natives "de.longri.gdx-sqlite:gdx-sqlite-platform:$gdxSqliteVersion:natives-armeabi"
     natives "de.longri.gdx-sqlite:gdx-sqlite-platform:$gdxSqliteVersion:natives-armeabi-v7a"
     natives "de.longri.gdx-sqlite:gdx-sqlite-platform:$gdxSqliteVersion:natives-arm64-v8a"
     natives "de.longri.gdx-sqlite:gdx-sqlite-platform:$gdxSqliteVersion:natives-x86"
     natives "de.longri.gdx-sqlite:gdx-sqlite-platform:$gdxSqliteVersion:natives-x86_64"
```

(ios)
dependencies
```
     compile "de.longri.gdx-sqlite:gdx-sqlite-platform:$gdxSqliteVersion:natives-ios"
```

(core)
dependencies
```
     compile "de.longri.gdx-sqlite:gdx-sqlite:$gdxSqliteVersion"
```

##Develop:
####tools required:
* Gradle
* ANT install ==> https://ant.apache.org/manual/install.html
* Maven