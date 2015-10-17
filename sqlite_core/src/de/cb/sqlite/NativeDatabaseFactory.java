package de.cb.sqlite;

import com.badlogic.gdx.files.FileHandle;

/**
 * Created by Longri on 17.10.15.
 */
public class NativeDatabaseFactory extends DatabaseFactory {

    public NativeDatabaseFactory()
    {
        super();
    }

    @Override
    protected SQLite createInstance(FileHandle Path, AlternateDatabase alter)
    {
        try
        {
            return new NativeDB(Path, alter);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return null;
    }

}
