package com.lucaslng;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.ui.Button;

public class MainMenuState extends GameState {

	Button b;

	public MainMenuState(Engine engine) {
		super(engine);
		b = new Button(0f, 0f, 400f, 400f, false, false);
		uiManager.elements.add(b);
	}

	@Override
	public void init() {
		
	}

	@Override
	public GameStates doLoop(double dt) {
		return GameStates.MAIN_MENU;
	}

	@Override
	public void dispose() {
	}
	
}
