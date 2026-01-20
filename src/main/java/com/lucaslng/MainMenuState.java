package com.lucaslng;

import java.awt.Color;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.GameStateSwitch;
import com.lucaslng.engine.ui.*;

public class MainMenuState extends GameState {

	private final Button playButton, optionsButton;

	public MainMenuState(Engine engine) {
		super(engine);

		Button exitButton = new Button(100f, 100f, 100f, 100f, XAlignment.RIGHT, YAlignment.TOP, ColorList.RED);
		exitButton.addOperation(() -> System.exit(0));
		uiManager.elements.add(exitButton);
		uiManager.elements.add(new Text(174f, 66f, 0f, 0f, XAlignment.RIGHT, YAlignment.TOP, "x",
				new TextStyle("Pixeled", 10f, Color.BLACK)));

		// x ignored due to center alignment
		playButton = new Button(0f, 440f, 550f, 160f, XAlignment.CENTER, YAlignment.TOP, ColorList.BLUE);
		playButton.addOperation(() -> gameStateSwitch = new GameStateSwitch(GameStates.LEVEL_SELECTION, null));
		uiManager.elements.add(playButton);
		uiManager.elements.add(new Text(-150f, 400f, 0f, 0f, XAlignment.CENTER, YAlignment.TOP, "PLAY",
				new TextStyle("Pixeled", 15f, Color.BLACK)));

		optionsButton = new Button(0f, 740f, 550f, 160f, XAlignment.CENTER, YAlignment.TOP, ColorList.ORANGE);
		optionsButton.addOperation(() -> gameStateSwitch = new GameStateSwitch(GameStates.OPTIONS, null));
		uiManager.elements.add(optionsButton);
		uiManager.elements.add(new Text(-240f, 700f, 0f, 0f, XAlignment.CENTER, YAlignment.TOP, "OPTIONS",
				new TextStyle("Pixeled", 15f, Color.BLACK)));

	}

	@Override
	public void doLoop(double dt) {
	}

}
