package de.cb.sqlite.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import de.cb.sqlite.AndroidDatabaseFactory;
import de.cb.sqlite.MyGdxTest;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create instance of DatabaseFactory
        new AndroidDatabaseFactory(this);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new MyGdxTest(), config);

    }
}
