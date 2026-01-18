package com.lucaslng.engine;

import com.lucaslng.GameStates;
import com.lucaslng.engine.ui.UIManager;

public abstract class GameState {
  protected final Engine engine;
	protected final UIManager uiManager;
	protected final EntityManager entityManager;
	private long lastTick, lastFpsTime;
	private int frameCount;
	private double dt;

	// init method executes everytime the gameState changes to this one
	abstract public void init();

	// we only want doLoop to have access to dt
	abstract public GameStates doLoop(double dt);

	// called when switching to a different state
	abstract public void dispose();

	// constructor executes only once, when the entire game initializes
	public GameState(Engine engine) {
		this.engine = engine;
		uiManager = new UIManager(engine.window, engine.inputHandler);
		entityManager = new EntityManager();
		System.out.println("Starting game loop...");
		lastFpsTime = System.currentTimeMillis();
		lastTick = System.nanoTime();
		frameCount = 0;
		dt = 0d;
	}

	public GameStates loop() {
		while (!engine.shouldClose()) {
			long frameStart = System.nanoTime();

			// calculate delta time in seconds
			dt = (frameStart - lastTick) / 1_000_000_000.0;
			lastTick = frameStart;

			GameStates gameState = doLoop(dt);
			engine.doLoop();

			if (gameState != engine.gameState) {
				dispose();
				return gameState;
			}

			// FPS tracking
			frameCount++;
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastFpsTime >= 1000) {
				System.out.println("fps: " + frameCount);
				frameCount = 0;
				lastFpsTime = currentTime;
			}
		}
		return GameStates.EXIT;
	}

	public double getDt() {
		return dt;
	}

}