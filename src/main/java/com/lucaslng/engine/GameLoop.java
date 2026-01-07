package com.lucaslng.engine;

public abstract class GameLoop {

	private final Engine engine;
	private long lastTick = 0, lastTickDuration;
	public double dt = 0;

	public GameLoop(Engine engine) {
		this.engine = engine;
	}

	abstract public void init(Engine engine);

	abstract public void doLoop(Engine engine);

	public void execute() {
		long lastFpsTime = System.currentTimeMillis();
		int frameCount = 0;
		
		while (!engine.shouldClose()) {
			long now = System.nanoTime();
			
			doLoop(engine);
			engine.doLoop();
			
			lastTickDuration = now - lastTick;
			lastTick = now;
			dt = dt();
			
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

	private int lastTickMs() {
		return (int) (lastTickDuration / 1_000_000);
	}

	private double dt() {
    return lastTickDuration / 1_000_000_000.0;
}

}
