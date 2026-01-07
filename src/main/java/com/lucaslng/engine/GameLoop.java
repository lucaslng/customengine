// GameLoop.java

package com.lucaslng.engine;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingWorker;

public abstract class GameLoop extends SwingWorker<Void, Void> {

	private final Engine engine;
	private long lastTick, lastTickDuration;
	public double dt = 0;

	public GameLoop(Engine engine) {
		this.engine = engine;
	}

	abstract public void init(Engine engine);

	abstract public void doLoop(Engine engine);

	@Override
	protected Void doInBackground() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				long now = System.nanoTime();
        lastTickDuration = now - lastTick;
        lastTick = now;
        dt = dt();
				doLoop(engine);
				engine.doLoop();
			}

		}, 0, 1000 / engine.settings().FPS);

		Timer fpsTimer = new Timer();
		fpsTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				System.out.println("fps: " + fps());
			}

		}, 1000, 1000);
		return null;
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
