package com.lucaslng.engine;

import com.lucaslng.GameStates;

public abstract class GameState {
	private final Engine engine;
	private long lastTick, lastFpsTime;
	private int frameCount;
	private double dt;

	// init method executes everytime the gameState changes to this one
	abstract public void init(Engine engine);

	abstract public GameStates doLoop(Engine engine, double dt);

	// constructor executes only once, when the entire game initializes
	public GameState(Engine engine) {
		this.engine = engine;
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

			GameStates gameState = doLoop(engine, dt);
			engine.doLoop();

			if (gameState != engine.gameState)
				return gameState;

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