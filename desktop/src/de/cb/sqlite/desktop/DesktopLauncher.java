package de.cb.sqlite.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.cb.sqlite.DesktopDatabaseFactory;
import de.cb.sqlite.MyGdxTest;

public class DesktopLauncher {
    public static void main(String[] arg) {

        //create instance of DatabaseFactory
        new DesktopDatabaseFactory();

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new MyGdxTest(), config);
    }
}
