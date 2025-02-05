package com.lucaslng;

import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.GL;

import static com.lucaslng.ThreadHandler.signalTerminate;
import static com.lucaslng.ThreadHandler.signalTerminated;

public class RenderLoop implements Runnable {

	private final AWTGLCanvasExplicitDispose canvas;

	public RenderLoop(AWTGLCanvasExplicitDispose canvas) {
		this.canvas = canvas;
	}

	@Override
	public void run() {
		while (true) {
			canvas.render();
			try {
				if (signalTerminate.tryAcquire(10, TimeUnit.MILLISECONDS)) {
					GL.setCapabilities(null);
					canvas.doDisposeCanvas();
					signalTerminated.release();
					return;
				}
			} catch (InterruptedException ignored) {
			}
		}
	}

}
