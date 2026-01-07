package com.lucaslng.engine;

public abstract class GameLoop {

	private final Engine engine;
	private long lastTick = 0, lastTickDuration = 0;
	public double dt = 0;

	public GameLoop(Engine engine) {
		this.engine = engine;
	}

	abstract public void init(Engine engine);

	abstract public void doLoop(Engine engine);

	public void execute() {
		System.out.println("Starting game loop...");
		long lastFpsTime = System.currentTimeMillis();
		int frameCount = 0;
		lastTick = System.nanoTime(); // Initialize lastTick
		
		while (!engine.shouldClose()) {
			long frameStart = System.nanoTime();
			
			doLoop(engine);
			engine.doLoop();
			
			long frameEnd = System.nanoTime();
			lastTickDuration = frameEnd - frameStart;
			lastTick = frameEnd;
			dt = dt();
			
			frameCount++;
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastFpsTime >= 1000) {
				System.out.println("fps: " + frameCount);
				frameCount = 0;
				lastFpsTime = currentTime;
			}

			long frameTimeMs = lastTickDuration / 1_000_000;
			long targetFrameTimeMs = 1000 / engine.settings().FPS;
			long sleepTime = targetFrameTimeMs - frameTimeMs;
			
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		System.exit(0);
	}

	public int fps() {
		int lastTickMs = lastTickMs();
		if (lastTickMs == 0) return -1;
		return 1000 / lastTickMs;
	}

	private int lastTickMs() {
		return (int) (lastTickDuration / 1_000_000);
	}

	private double dt() {
		return lastTickDuration / 1_000_000_000.0;
	}

}