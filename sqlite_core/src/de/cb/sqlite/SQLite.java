/* 
 * Copyright (C) 2015 team-cachebox.de
 *
 * Licensed under the : GNU General Public License (GPL);
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
package de.cb.sqlite;

import com.badlogic.gdx.files.FileHandle;
import org.slf4j.LoggerFactory;

/**
 * TODO document
 *
 * @author Hoepfner 2015
 */
public abstract class SQLite {

    final static org.slf4j.Logger log = LoggerFactory.getLogger(SQLite.class);

    final protected FileHandle databaseFileHandle;
    protected boolean newDB = false;
    protected boolean isStarted = false;

    private final AlternateDatabase alterNate;

    public SQLite(FileHandle databasePath, AlternateDatabase alter) {
        super();
        this.databaseFileHandle = databasePath;
        this.alterNate = alter;
    }

    public abstract void Initialize();

    public abstract void Close();

    public abstract void Reset();

    public abstract CoreCursor rawQuery(String sql, String[] args);

    public abstract void execSQL(String sql);

    public abstract long update(String tablename, Parameters val, String whereClause, String[] whereArgs);

    public abstract long insert(String tablename, Parameters val);

    public abstract long delete(String tablename, String whereClause, String[] whereArgs);

    public abstract void beginTransaction();

    public abstract void setTransactionSuccessful();

    public abstract void endTransaction();

    public abstract long insertWithConflictReplace(String tablename, Parameters val);

    public abstract long insertWithConflictIgnore(String tablename, Parameters val);

    public abstract boolean isClosed();

    public boolean isDbNew() {
        return newDB;
    }

    public FileHandle getDatabaseFileHandle() {
        return databaseFileHandle;
    }

    public boolean StartUp() {
        try {
            log.debug("DB Startup : " + databaseFileHandle);
        } catch (Exception e) {
            // gibt beim splash - Start: NPE in Translation.readMissingStringsFile
            // Nachfolgende Starts sollten aber protokolliert werden
        }

        Initialize();

        int databaseSchemeVersion = -1;
        try {
            databaseSchemeVersion = GetDatabaseSchemeVersion();
        } catch (Exception e) {
        }

        if (databaseSchemeVersion == -1) {
            createConfigTable();
            databaseSchemeVersion = 0;
        }

        if (databaseSchemeVersion < alterNate.databaseSchemeVersion()) {
            alterNate.alternateDatabase(this, databaseSchemeVersion);
            SetDatabaseSchemeVersion();
        }
        isStarted = true;
        return true;
    }

    private void createConfigTable() {
        // First Initialization of the Database

        this.beginTransaction();

        // Config Table used for save dataBaseSchemeVersion
        this.execSQL("CREATE TABLE [Config] ([Key] nvarchar (30) NOT NULL, [Value] nvarchar (255) NULL);");
        this.execSQL("CREATE INDEX [Key_idx] ON [Config] ([Key] ASC);");


        this.setTransactionSuccessful();
        this.endTransaction();


        this.SetDatabaseSchemeVersion(0);
    }


    public int GetDatabaseSchemeVersion() {

        //first, check if config table exist
        if(!isTableExists("Config")){
            return -1;
        }

        int result = -1;
        CoreCursor c = null;
        try {
            c = rawQuery("select Value from Config where [Key] like ?", new String[]
                    {"DatabaseSchemeVersionWin"});
        } catch (Exception exc) {
            return -1;
        }
        try {
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                String databaseSchemeVersion = c.getString(0);
                result = Integer.parseInt(databaseSchemeVersion);
                c.moveToNext();
            }
        } catch (Exception exc) {
            result = -1;
        }
        if (c != null) {
            c.close();
        }

        return result;
    }

    boolean isTableExists( String tableName)
    {
        if (tableName == null)
        {
            return false;
        }
        CoreCursor cursor = rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
        if (!cursor.moveToFirst())
        {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }


    private void SetDatabaseSchemeVersion() {
        SetDatabaseSchemeVersion(alterNate.databaseSchemeVersion());
    }

    private void SetDatabaseSchemeVersion(int version) {
        Parameters val = new Parameters();
        val.put("Value", version);
        long anz = update("Config", val, "[Key] like 'DatabaseSchemeVersionWin'", null);
        if (anz <= 0) {
            // Update not possible because Key does not exist
            val.put("Key", "DatabaseSchemeVersionWin");
            insert("Config", val);
        }
        // for Compatibility with WinCB
        val.put("Value", version);
        anz = update("Config", val, "[Key] like 'DatabaseSchemeVersion'", null);
        if (anz <= 0) {
            // Update not possible because Key does not exist
            val.put("Key", "DatabaseSchemeVersion");
            insert("Config", val);
        }
    }

    public void WriteConfigString(String key, String value) {
        Parameters val = new Parameters();
        val.put("Value", value);
        long anz = update("Config", val, "[Key] like '" + key + "'", null);
        if (anz <= 0) {
            // Update not possible because Key does not exist
            val.put("Key", key);
            insert("Config", val);
        }
    }

    public void WriteConfigLongString(String key, String value) {
        Parameters val = new Parameters();
        val.put("LongString", value);
        long anz = update("Config", val, "[Key] like '" + key + "'", null);
        if (anz <= 0) {
            // Update not possible because Key does not exist
            val.put("Key", key);
            insert("Config", val);
        }
    }

    public String ReadConfigString(String key) throws Exception {
        String result = "";
        CoreCursor c = null;
        boolean found = false;
        try {
            c = rawQuery("select Value from Config where [Key] like ?", new String[]
                    {key});
        } catch (Exception exc) {
            throw new Exception("not in DB");
        }
        try {
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                result = c.getString(0);
                found = true;
                c.moveToNext();
            }
        } catch (Exception exc) {
            throw new Exception("not in DB");
        } finally {
            c.close();
        }

        if (!found) throw new Exception("not in DB");

        return result;
    }

    public String ReadConfigLongString(String key) throws Exception {
        String result = "";
        CoreCursor c = null;
        boolean found = false;
        try {
            c = rawQuery("select LongString from Config where [Key] like ?", new String[]
                    {key});
        } catch (Exception exc) {
            throw new Exception("not in DB");
        }
        try {
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                result = c.getString(0);
                found = true;
                c.moveToNext();
            }
        } catch (Exception exc) {
            throw new Exception("not in DB");
        }
        c.close();

        if (!found) throw new Exception("not in DB");

        return result;
    }

    public void WriteConfigLong(String key, long value) {
        WriteConfigString(key, String.valueOf(value));
    }

    public long ReadConfigLong(String key) {
        try {
            String value = ReadConfigString(key);
            return Long.valueOf(value);
        } catch (Exception ex) {
            return 0;
        }
    }

    public boolean isStarted() {
        return isStarted;
    }

    public String toString() {
        return "SQLite DB [" + (isStarted ? "is started" : "not started") + "]:" + databaseFileHandle;
    }
}