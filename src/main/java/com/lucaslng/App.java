package com.lucaslng;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.SoundHandler;

import java.util.HashMap;

public class App {
	public static void main(String[] args) {

		SoundHandler.loop("music");
		HashMap<GameStates, GameState> gameStates = new HashMap<>();
		Engine engine = new Engine(gameStates);

		gameStates.put(GameStates.PLAYING, new PlayingState(engine));
		gameStates.put(GameStates.MAIN_MENU, new MainMenuState(engine));
		gameStates.put(GameStates.OPTIONS, new OptionsState(engine));
		gameStates.put(GameStates.LEVEL_SELECTION, new LevelSelectionState(engine));
		gameStates.put(GameStates.END_SCREEN, new EndScreenState(engine));

		engine.start(GameStates.MAIN_MENU);
	}
}
