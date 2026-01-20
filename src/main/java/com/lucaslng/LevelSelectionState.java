package com.lucaslng;

import java.awt.Color;

import com.lucaslng.engine.Engine;
import com.lucaslng.engine.GameState;
import com.lucaslng.engine.GameStateSwitch;
import com.lucaslng.engine.ui.*;

public class LevelSelectionState extends GameState {

	public LevelSelectionState(Engine engine) {
		super(engine);

		Button exitButton = new Button(100f, 100f, 100f, 100f, XAlignment.RIGHT, YAlignment.TOP, ColorList.RED);
		exitButton.addOperation(() -> System.exit(0));
		uiManager.elements.add(exitButton);
		uiManager.elements.add(new Text(174f, 66f, 0f, 0f, XAlignment.RIGHT, YAlignment.TOP, "x",
				new TextStyle("Pixeled", 10f, Color.BLACK)));

		uiManager.elements.add(
				new ColoredRectElement(0, 0, 1400f, 1000f, XAlignment.CENTER, YAlignment.CENTER, ColorList.LIGHT_GRAY));

		Button backButton = new Button(-470f, -400f, 400f, 140f, XAlignment.CENTER, YAlignment.CENTER,
				ColorList.RED);
		backButton.addOperation(() -> gameStateSwitch = new GameStateSwitch(GameStates.MAIN_MENU, null));
		uiManager.elements.add(backButton);
		uiManager.elements.add(new Text(-600f, -500f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER, "BACK",
				new TextStyle("Pixeled", 13f, Color.BLACK)));

		uiManager.elements.add(new Text(-160f, -300f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER, "Levels", new TextStyle("Pixeled", 13, Color.BLACK)));

		for (int i = 1; i <= 10; i++) {
			float x = (i - 6) * 120f + 60f;
			Button button = new Button(x, 0f, 100f, 100f, XAlignment.CENTER, YAlignment.CENTER, ColorList.GRAY);
			GameStateSwitch gameStateSwitchWithLevel = new GameStateSwitch(GameStates.PLAYING, i);
			button.addOperation(() -> gameStateSwitch = gameStateSwitchWithLevel);
			uiManager.elements.add(button);
			uiManager.elements.add(new Text(x - 20f, -80f, 0f, 0f, XAlignment.CENTER, YAlignment.CENTER, i + "",
				new TextStyle("Pixeled", 10f, Color.BLACK)));
		}

		
	}

	@Override
	public void doLoop(double dt) {
	}
	
}
