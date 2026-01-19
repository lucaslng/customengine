package com.lucaslng;

import java.awt.Color;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.ui.*;

public class OptionsState extends GameState {

	private final Button backButton;

	private GameStates switchGameState;

	public OptionsState(Engine engine) {
		super(engine);

		// x ignored due to center alignment
		backButton = new Button(0f, 80f, 600f, 200f, XAlignment.CENTER, YAlignment.BOTTOM);
		backButton.addOperation(() -> {
			engine.soundHandler.play("click");
			switchGameState = GameStates.MAIN_MENU;
		});
		uiManager.elements.add(backButton);
		uiManager.elements.add(new Text(-120f, 200f, 0f, 0f, XAlignment.CENTER, YAlignment.BOTTOM, "BACK",
				new TextStyle("Arial", 15f, Color.BLACK)));
	}

	@Override
	public void init() {
		super.init();
		switchGameState = GameStates.OPTIONS;
	}

	@Override
	public GameStates doLoop(double dt) {
		return switchGameState;
	}

}
