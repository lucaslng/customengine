package com.lucaslng;

import com.lucaslng.engine.Engine;
import org.lwjgl.system.Configuration;
import com.lucaslng.engine.utils.OsFetcher;

public class App {
	public static void main(String[] args) {

        if (OsFetcher.isMac()) {
            Configuration.GLFW_LIBRARY_NAME.set("glfw_async");
        }

		Engine engine = new Engine();
		engine.start(new Game(engine));
	}
}
