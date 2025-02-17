package com.lucaslng.engine;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingWorker;

public abstract class GameLoop extends SwingWorker<Void, Void> {

	private final Engine engine;
	private long lastTick, lastTickDuration;

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
				doLoop(engine);
				lastTickDuration = System.nanoTime() - lastTick;
				lastTick = System.nanoTime();
			}

		}, 0, 1000 / engine.settings().FPS);

		Timer fpsTimer = new Timer();
		fpsTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				System.out.println("Game loop fps: " + fps() + "\nRender fps: " + engine.renderer().fps());
			}

		}, 1000, 1000);
		return null;
	}

	public int fps() {
		return 1000 / lastTickMs();
	}

	public int lastTickMs() {
		return (int) (lastTickDuration / 1000000);
	}

}
