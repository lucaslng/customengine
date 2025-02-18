package com.lucaslng;

import com.lucaslng.engine.Engine;

public class App {
	public static void main(String[] args) {
		
		Engine engine = new Engine();
		engine.start(new Game(engine));
	}

}
