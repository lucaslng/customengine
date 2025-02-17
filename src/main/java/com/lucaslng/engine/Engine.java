package com.lucaslng.engine;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.lucaslng.engine.renderer.GameFrame;
import com.lucaslng.engine.renderer.Renderer;

public class Engine {

	private final JFrame frame;
	private final Renderer renderer;
	private final EngineSettings settings;

	public Engine() {
		settings = new EngineSettings();
		frame = new GameFrame(settings);
		renderer = new Renderer(settings);
		frame.add(renderer, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

	public void doLoop() {
		renderer.render();
	}

}
