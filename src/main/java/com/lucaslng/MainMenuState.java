package com.lucaslng;

import java.awt.Color;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.ui.*;

public class MainMenuState extends GameState {

	private final Button playButton, optionsButton;

	private GameStates switchGameState;

	public MainMenuState(Engine engine) {
		super(engine);

		// x ignored due to center alignment
		playButton = new Button(0f, 440f, 550f, 160f, XAlignment.CENTER, YAlignment.TOP, new Color(0, 186, 237));
		playButton.addOperation(() -> {
			engine.soundHandler.play("click");
			System.out.println("play");
			switchGameState = GameStates.PLAYING;
		});
		uiManager.elements.add(playButton);
		uiManager.elements.add(new Text(-150f, 400f, 0f, 0f, XAlignment.CENTER, YAlignment.TOP, "PLAY",
				new TextStyle("Pixeled", 15f, Color.BLACK)));

		optionsButton = new Button(0f, 740f, 550f, 160f, XAlignment.CENTER, YAlignment.TOP, new Color(225, 120, 26));
		optionsButton.addOperation(() -> {
			engine.soundHandler.play("click");
			System.out.println("options");
			switchGameState = GameStates.OPTIONS;
		});
		uiManager.elements.add(optionsButton);
		uiManager.elements.add(new Text(-240f, 700f, 0f, 0f, XAlignment.CENTER, YAlignment.TOP, "OPTIONS",
				new TextStyle("Pixeled", 15f, Color.BLACK)));

	}

	@Override
	public void init() {
		super.init();
		switchGameState = GameStates.MAIN_MENU;
	}

	@Override
	public GameStates doLoop(double dt) {
		return switchGameState;
	}

}
