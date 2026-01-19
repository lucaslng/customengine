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
		backButton = new Button(-600f, 110f, 400f, 140f, XAlignment.CENTER, YAlignment.TOP, new Color(225, 150, 150));
		backButton.addOperation(() -> {
			engine.soundHandler.play("click");
			switchGameState = GameStates.MAIN_MENU;
		});
		uiManager.elements.add(backButton);
		uiManager.elements.add(new Text(-730f, 80f, 0f, 0f, XAlignment.CENTER, YAlignment.TOP, "BACK",
				new TextStyle("Pixeled", 13f, Color.BLACK)));
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
