
#include <stdio.h>
#include <stdlib.h>



    #define SQLITE_VERSION        "7.21.0"

    typedef struct sqlite3 sqlite3;


    const char sqlite3_version[] = SQLITE_VERSION;
    const char *sqlite3_libversion(void){ return sqlite3_version; }

    const char *sqlite3_errmsg(sqlite3 *db){ return "Dummy Error Msg"; }

    int sqlite3_open(const char *filename, sqlite3 **ppDb){

        FILE * fp;

           fp = fopen (filename, "w+");
           fprintf(fp, "%s %s %s %d", "We", "are", "in", 2017);

           fclose(fp);




        return 1;
    }

