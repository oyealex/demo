package com.badlogic.cubocy;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class CubocDesktop {
    public static void main(String[] argv) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        
        int height = 320;
        int width = 480;
//        configuration.setWindowSizeLimits(width, height, width, height);
        // configuration.resizable = false;
        configuration.setTitle("Cubocy");
        new Lwjgl3Application(new Cubocy(), configuration);

        // After creating the Application instance we can set the log level to
        // show the output of calls to Gdx.app.debug
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }
}
