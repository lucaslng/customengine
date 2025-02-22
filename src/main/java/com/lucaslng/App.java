package com.lucaslng;

import com.lucaslng.engine.Constants;
import com.lucaslng.engine.Engine;

public class App {

	public final Constants constants = new Constants();
	public static void main(String[] args) {

		Engine engine = new Engine();
		engine.start(new Game(engine));
	}

}
