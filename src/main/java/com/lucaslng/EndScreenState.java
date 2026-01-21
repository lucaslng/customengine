package com.lucaslng;

import java.awt.Color;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.GameStateSwitch;
import com.lucaslng.engine.ui.*;

public class EndScreenState extends GameState {

	private final Button menuButton;

	public EndScreenState(Engine engine) {
		super(engine);

		Button exitButton = new Button(100f, 100f, 100f, 100f, XAlignment.RIGHT, YAlignment.TOP, ColorList.RED);
		exitButton.addOperation(() -> System.exit(0));
		uiManager.elements.add(exitButton);
		uiManager.elements.add(new Text(174f, 66f, 0f, 0f, XAlignment.RIGHT, YAlignment.TOP, "x",
				new TextStyle("Pixeled", 10f, Color.BLACK)));

		uiManager.elements.add(new Text(-280f, 170f, 0f, 0f, XAlignment.CENTER, YAlignment.TOP, "You win!",
				new TextStyle("Pixeled", 16f, Color.BLACK)));

		// x ignored due to center alignment
		menuButton = new Button(0f, 540f, 550f, 160f, XAlignment.CENTER, YAlignment.TOP, ColorList.BLUE);
		menuButton.addOperation(() -> gameStateSwitch = new GameStateSwitch(GameStates.MAIN_MENU, null));
		uiManager.elements.add(menuButton);
		uiManager.elements.add(new Text(-150f, 500f, 0f, 0f, XAlignment.CENTER, YAlignment.TOP, "MENU",
				new TextStyle("Pixeled", 15f, Color.BLACK)));

	}

	@Override
	public void doLoop(double dt) {
	}

}
