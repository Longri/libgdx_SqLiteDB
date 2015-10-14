package de.cb.sqlite;

import android.app.Activity;
import com.badlogic.gdx.files.FileHandle;

public class AndroidDatabaseFactory extends DatabaseFactory
{
	private final Activity activity;

	public AndroidDatabaseFactory(Activity activity)
	{
		super();
		this.activity = activity;
	}

	@Override
	protected SQLite createInstance(FileHandle Path, AlternateDatabase alter)
	{
		try
		{
			return new AndroidDB(activity, Path, alter);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return null;
	}

}
