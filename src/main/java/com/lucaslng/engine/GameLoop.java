package com.lucaslng.engine;

public abstract class GameLoop {

	private final Engine engine;
	private long lastTick, lastTickDuration;

	public GameLoop(Engine engine) {
		this.engine = engine;
	}

	abstract public void init(Engine engine);

	abstract public void doLoop(Engine engine);

	public void execute() {
		long lastFpsTime = System.currentTimeMillis();
		int frameCount = 0;
		
		while (!engine.shouldClose()) {
			long startTime = System.nanoTime();
			
			doLoop(engine);
			engine.doLoop();
			
			lastTickDuration = System.nanoTime() - startTime;
			lastTick = System.nanoTime();
			
			frameCount++;
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastFpsTime >= 1000) {
				System.out.println("fps: " + frameCount);
				frameCount = 0;
				lastFpsTime = currentTime;
			}

			long frameTime = (lastTickDuration / 1_000_000);
			long targetFrameTime = 1000 / engine.settings().FPS;
			if (frameTime < targetFrameTime) {
				try {
					Thread.sleep(targetFrameTime - frameTime);
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

	public int lastTickMs() {
		return (int) (lastTickDuration / 1000000);
	}

}