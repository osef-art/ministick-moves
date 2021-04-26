package com.mygdx.moves.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.moves.MainScreen;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "ministick moves";
		config.width = 480;
		config.height = 480;
//		config.vSyncEnabled = true;
		config.pauseWhenMinimized = true;
		config.pauseWhenBackground = true;
		config.addIcon("android/assets/icons/face0.png", Files.FileType.Local);

		new LwjglApplication(new MainScreen(), config);
	}
}
