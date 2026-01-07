package com.lucaslng;

import com.lucaslng.engine.Engine;
import org.lwjgl.system.Configuration;

public class App {
	public static void main(String[] args) {

		Configuration.GLFW_LIBRARY_NAME.set("glfw_async");
		Engine engine = new Engine();
		engine.start(new Game(engine));
	}

}
