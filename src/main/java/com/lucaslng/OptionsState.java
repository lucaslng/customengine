package com.lucaslng;

import java.awt.Color;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.GameStateSwitch;
import com.lucaslng.engine.ui.*;

public class OptionsState extends GameState {

	public OptionsState(Engine engine) {
		super(engine);

		Button exitButton = new Button(100f, 100f, 100f, 100f, XAlignment.RIGHT, YAlignment.TOP, new Color(225, 150, 150));
		exitButton.addOperation(() -> System.exit(0));
		uiManager.elements.add(exitButton);
		uiManager.elements.add(new Text(174f, 66f, 0f, 0f, XAlignment.RIGHT, YAlignment.TOP, "x",
				new TextStyle("Pixeled", 10f, Color.BLACK)));

		// Background rect
		uiManager.elements.add(
				new ColoredRectElement(0, 0, 1400f, 1000f, XAlignment.CENTER, YAlignment.CENTER, new Color(240, 240, 240)));

		Button backButton = new Button(-470f, -400f, 400f, 140f, XAlignment.CENTER, YAlignment.CENTER,
				new Color(225, 150, 150));
		backButton.addOperation(() -> gameStateSwitch = new GameStateSwitch(GameStates.MAIN_MENU, null));
		uiManager.elements.add(backButton);
		uiManager.elements.add(new Text(-600f, -500f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER, "BACK",
				new TextStyle("Pixeled", 13f, Color.BLACK)));

		uiManager.elements.add(new Text(-400f, 0f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER,
				"Left",
				new TextStyle("Pixeled", 9f, Color.BLACK)));
		uiManager.elements.add(new Text(-400f, 150f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER,
				"Right",
				new TextStyle("Pixeled", 9f, Color.BLACK)));
		uiManager.elements.add(new Text(-400f, 300f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER,
				"Jump",
				new TextStyle("Pixeled", 9f, Color.BLACK)));

		uiManager.elements.add(new Text(-150f, -120f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER,
				"Player 1",
				new TextStyle("Pixeled", 9f, Color.BLACK)));

		Text player1LeftKeyText = new Text(-20f, 0f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER,
				engine.settings.player1Left.name(),
				new TextStyle("Pixeled", 9f, Color.BLACK));
		KeySelector player1LeftKeySelector = new KeySelector(0f, 70f, 300f, 100f, XAlignment.CENTER, YAlignment.CENTER,
				new Color(100, 100, 100), engine.inputHandler, engine.settings.player1Left, player1LeftKeyText);
		uiManager.elements.add(player1LeftKeySelector);
		uiManager.elements.add(player1LeftKeyText);

		Text player1RightKeyText = new Text(-20f, 150f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER,
				engine.settings.player1Right.name(),
				new TextStyle("Pixeled", 9f, Color.BLACK));
		KeySelector player1RightKeySelector = new KeySelector(0f, 220f, 300f, 100f, XAlignment.CENTER, YAlignment.CENTER,
				new Color(100, 100, 100), engine.inputHandler, engine.settings.player1Right, player1RightKeyText);
		uiManager.elements.add(player1RightKeySelector);
		uiManager.elements.add(player1RightKeyText);

		Text player1JumpKeyText = new Text(-20f, 300f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER,
				engine.settings.player1Jump.name(),
				new TextStyle("Pixeled", 9f, Color.BLACK));
		KeySelector player1JumpKeySelector = new KeySelector(0f, 370f, 300f, 100f, XAlignment.CENTER, YAlignment.CENTER,
				new Color(100, 100, 100), engine.inputHandler, engine.settings.player1Jump, player1JumpKeyText);
		uiManager.elements.add(player1JumpKeySelector);
		uiManager.elements.add(player1JumpKeyText);

		uiManager.elements.add(new Text(200f, -120f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER,
				"Player 2",
				new TextStyle("Pixeled", 9f, Color.BLACK)));

		Text player2LeftKeyText = new Text(330f, 0f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER,
				engine.settings.player2Left.name(),
				new TextStyle("Pixeled", 9f, Color.BLACK));
		KeySelector player2LeftKeySelector = new KeySelector(350f, 70f, 300f, 100f, XAlignment.CENTER, YAlignment.CENTER,
				new Color(100, 100, 100), engine.inputHandler, engine.settings.player2Left, player2LeftKeyText);
		uiManager.elements.add(player2LeftKeySelector);
		uiManager.elements.add(player2LeftKeyText);

		Text player2RightKeyText = new Text(330f, 150f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER,
				engine.settings.player2Right.name(),
				new TextStyle("Pixeled", 9f, Color.BLACK));
		KeySelector player2RightKeySelector = new KeySelector(350f, 220f, 300f, 100f, XAlignment.CENTER, YAlignment.CENTER,
				new Color(100, 100, 100), engine.inputHandler, engine.settings.player2Right, player2RightKeyText);
		uiManager.elements.add(player2RightKeySelector);
		uiManager.elements.add(player2RightKeyText);

		Text player2JumpKeyText = new Text(330f, 300f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER,
				engine.settings.player2Jump.name(),
				new TextStyle("Pixeled", 9f, Color.BLACK));
		KeySelector player2JumpKeySelector = new KeySelector(350f, 370f, 300f, 100f, XAlignment.CENTER, YAlignment.CENTER,
				new Color(100, 100, 100), engine.inputHandler, engine.settings.player2Jump, player2JumpKeyText);
		uiManager.elements.add(player2JumpKeySelector);
		uiManager.elements.add(player2JumpKeyText);

	}

	@Override
	public void init(Object payload) {
		super.init(payload);
	}

	@Override
	public void doLoop(double dt) {
	}

}
