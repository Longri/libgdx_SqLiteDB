package de.cb.sqlite;

import com.badlogic.gdx.files.FileHandle;

public class DesktopDatabaseFactory extends DatabaseFactory
{
	public DesktopDatabaseFactory()
	{
		super();
	}

	@Override
	protected SQLite createInstance(FileHandle Path, AlternateDatabase alter)
	{
		try
		{
			return new DesktopDB(Path, alter);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return null;
	}

}
