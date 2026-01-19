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
		switchGameState = GameStates.MAIN_MENU;

		// x ignored due to center alignment
		playButton = new Button(0f, 300f, 600f, 250f, XAlignment.CENTER, YAlignment.TOP);
		playButton.addOperation(() -> {
			engine.soundHandler.play("click");
			System.out.println("play");
			switchGameState = GameStates.PLAYING;
		});
		uiManager.elements.add(playButton);
		uiManager.elements.add(new Text(-120f, 360f, 0f, 0f, XAlignment.CENTER, YAlignment.TOP, "PLAY",
				new TextStyle("Arial", 15f, Color.BLACK)));

		optionsButton = new Button(0f, 700f, 600f, 250f, XAlignment.CENTER, YAlignment.TOP);
		optionsButton.addOperation(() -> {
			engine.soundHandler.play("click");
			System.out.println("options");
			switchGameState = GameStates.OPTIONS;
		});
		uiManager.elements.add(optionsButton);
		uiManager.elements.add(new Text(-200f, 760f, 0f, 0f, XAlignment.CENTER, YAlignment.TOP, "OPTIONS",
				new TextStyle("Arial", 15f, Color.BLACK)));

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
