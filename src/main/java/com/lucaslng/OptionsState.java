package com.lucaslng;

import java.awt.Color;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.ui.*;

public class OptionsState extends GameState {

	private GameStates switchGameState;

	public OptionsState(Engine engine) {
		super(engine);

		uiManager.elements.add(
				new ColoredRectElement(0, 0, 1400f, 1000f, XAlignment.CENTER, YAlignment.CENTER, new Color(240, 240, 240)));

		Button backButton = new Button(-470f, -400f, 400f, 140f, XAlignment.CENTER, YAlignment.CENTER, new Color(225, 150, 150));
		backButton.addOperation(() -> {
			switchGameState = GameStates.MAIN_MENU;
		});
		uiManager.elements.add(backButton);
		uiManager.elements.add(new Text(-600f, 80f, 0f, 0f, XAlignment.CENTER, YAlignment.TOP, "BACK",
				new TextStyle("Pixeled", 13f, Color.BLACK)));

		Text player1LeftKeyText = new Text(-20f, -170f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER,
				engine.settings.player1Left.name(),
				new TextStyle("Pixeled", 9f, Color.BLACK));
		KeySelector player1LeftKeySelector = new KeySelector(0f, -100f, 300f, 100f, XAlignment.CENTER, YAlignment.CENTER,
				new Color(100, 100, 100), engine.inputHandler, engine.settings.player1Left, player1LeftKeyText);
		uiManager.elements.add(player1LeftKeySelector);
		uiManager.elements.add(player1LeftKeyText);

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
