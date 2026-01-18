package com.lucaslng;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.ui.Button;

public class MainMenuState extends GameState {

	Button playButton;

	GameStates switchGameState;

	public MainMenuState(Engine engine) {
		super(engine);
		switchGameState = GameStates.MAIN_MENU;

		playButton = new Button(660f, 300f, 600f, 250f, false, false);
		playButton.addOperation(() -> switchGameState = GameStates.PLAYING);
		uiManager.elements.add(playButton);

	}

	@Override
	public void init() {
		
	}

	@Override
	public GameStates doLoop(double dt) {
		
		return switchGameState;
	}

	@Override
	public void dispose() {
	}
	
}
