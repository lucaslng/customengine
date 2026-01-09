package com.lucaslng.engine;

public abstract class GameLoop { 
	private final Engine engine;
	private long lastTick = 0;
	private double dt = 0;

	public GameLoop(Engine engine) {
		this.engine = engine;
	}

	abstract public void init(Engine engine);
	abstract public void doLoop(Engine engine, double dt);

	public void execute() {
		System.out.println("Starting game loop...");
		long lastFpsTime = System.currentTimeMillis();
		int frameCount = 0;
		lastTick = System.nanoTime();
		
		while (!engine.shouldClose()) {
			long frameStart = System.nanoTime();
			
			// calculate delta time in seconds
			dt = (frameStart - lastTick) / 1_000_000_000.0;
			lastTick = frameStart;

			doLoop(engine, dt);
			engine.doLoop();
			
			// FPS tracking
			frameCount++;
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastFpsTime >= 1000) {
				System.out.println("fps: " + frameCount);
				frameCount = 0;
				lastFpsTime = currentTime;
			}
		}

		System.exit(0);
	}

	public double getDeltaTime() {
		return dt;
	}

}