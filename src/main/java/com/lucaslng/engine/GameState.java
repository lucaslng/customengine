package com.lucaslng.engine;

import java.awt.Color;

import com.lucaslng.GameStates;
import com.lucaslng.engine.ui.*;

public abstract class GameState {
	protected final Engine engine;
	protected final UIManager uiManager;
	protected final EntityManager entityManager;
	protected GameStateSwitch gameStateSwitch;
	private long lastTick, lastFpsTime;
	private int frameCount;
	private double dt;
	private boolean active;
	private final Text fpsText;

	// we only want doLoop to have access to dt
	abstract public void doLoop(double dt);

	// constructor executes only once, when the entire game initializes
	public GameState(Engine engine) {
		this.engine = engine;
		uiManager = new UIManager(engine.settings, engine.window, engine.inputHandler);
		entityManager = new EntityManager();
		lastFpsTime = System.currentTimeMillis();
		lastTick = System.nanoTime();
		frameCount = 0;
		dt = 0d;
		active = false;
		fpsText = new Text(10f, 10f, 0f, 0f, XAlignment.LEFT, YAlignment.TOP, "FPS: ",
				new TextStyle("Arial", 6f, Color.BLACK));
		fpsText.visible = engine.settings.showFPS;
		uiManager.elements.add(fpsText);
	}

	// init method executes everytime the gameState changes to this one
	public void init(Engine engine, Object payload) {
		gameStateSwitch = null;
		uiManager.active = true;
		active = true;
	}

	// called when switching to a different state
	public void dispose() {
		uiManager.active = false;
		active = false;
	}

	public GameStateSwitch loop() {
		while (!engine.shouldClose()) {
			long frameStart = System.nanoTime();

			// calculate delta time in seconds
			dt = (frameStart - lastTick) / 1_000_000_000.0;
			lastTick = frameStart;

			doLoop(dt);
			engine.doLoop();

			if (gameStateSwitch != null) {
				dispose();
				return gameStateSwitch;
			}

			// FPS tracking
			frameCount++;
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastFpsTime >= 1000) {
				fpsText.text = "FPS: " + String.valueOf(frameCount);
				frameCount = 0;
				lastFpsTime = currentTime;
			}
		}
		return new GameStateSwitch(GameStates.EXIT, null);
	}

	public double getDt() {
		return dt;
	}

	public boolean active() {
		return active;
	}

}