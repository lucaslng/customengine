package com.lucaslng;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.ui.Button;
import com.lucaslng.engine.ui.XAlignment;
import com.lucaslng.engine.ui.YAlignment;

public class MainMenuState extends GameState {

	Button playButton;

	GameStates switchGameState;

	public MainMenuState(Engine engine) {
		super(engine);
		switchGameState = GameStates.MAIN_MENU;

		// x ignored due to center alignment
		playButton = new Button(0, 300f, 600f, 250f, XAlignment.CENTER, YAlignment.TOP);
		playButton.addOperation(() -> {
			engine.soundHandler.play("click");
			 switchGameState = GameStates.PLAYING;
			}
			);
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
