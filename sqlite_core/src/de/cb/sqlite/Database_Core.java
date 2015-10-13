package de.cb.sqlite;

import org.slf4j.LoggerFactory;

public abstract class Database_Core {
    final static org.slf4j.Logger log = LoggerFactory.getLogger(Database_Core.class);

    public long DatabaseId = 0;
    public long MasterDatabaseId = 0;

    public final SQLite db;

    public Database_Core(SQLite DB) {
        this.db = DB;
    }

    public boolean StartUp() {
        boolean result = this.db.StartUp();
        if (!result) return false;
        return result;
    }

    public void Initialize() {
        this.db.Initialize();
    }

    public void Reset() {
        this.db.Reset();
    }

    public void Close() {
        this.db.Close();
    }
}
