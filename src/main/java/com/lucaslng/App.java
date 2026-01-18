package com.lucaslng;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;

import java.util.HashMap;

public class App {
	public static void main(String[] args) {

		Engine engine = new Engine();

		HashMap<GameStates, GameState> gameStates = new HashMap<>();
		gameStates.put(GameStates.PLAYING, new PlayingState(engine));
		gameStates.put(GameStates.MAIN_MENU, new PlayingState(engine));

		engine.start(gameStates, GameStates.PLAYING);
	}
}
