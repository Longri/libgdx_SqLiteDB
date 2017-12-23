
typedef struct sqlite3 sqlite3;

const char *sqlite3_libversion(void);
const char *sqlite3_errmsg(sqlite3*);

int sqlite3_open(const char *filename, sqlite3 **ppDb);